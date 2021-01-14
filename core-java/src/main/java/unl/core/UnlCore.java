package unl.core;

import java.math.BigDecimal;

public class UnlCore {
    private final static String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";

    /**
     * The unique instance of the Unl Core class
     */
    private static UnlCore instance;

    /**
     * @return the unique instance of the UnlCore class.
     */
    public synchronized static UnlCore getInstance() {
        if (instance == null) {
            instance = new UnlCore();
        }
        return instance;
    }

    /**
     * Encodes latitude/longitude coordinates to locationId, either to specified precision or
     * to default precision. Elevation information can be optionally specified in options parameter.
     *
     * @param {number} lat - Latitude in degrees.
     * @param {number} lon - Longitude in degrees.
     * @param {number} [precision] - Number of characters in resulting locationId. Default value is 9.
     * @param {object} [options] - Number of options. Including elevation
     * @throws Invalid locationId.
     * @returns {string} LocationId of supplied latitude/longitude.
     * @example var locationId = UnlCore.encode(52.205, 0.119, 7); // => 'u120fxw'
     * var locationId = UnlCore.encode(52.205, 0.119, 7, { elevation: 9, elevationType: 'floor'}); // => 'u120fxw@9'
     */
    public String encode(double lat, double lon, int precision, Elevation elevationOptions) {
        int idx = 0;
        int bit = 0;
        boolean evenBit = true;
        StringBuilder locationId = new StringBuilder();

        double latMin = -90, latMax = 90;
        double lonMin = -180, lonMax = 180;

        while (locationId.length() < precision) {
            if (evenBit) {
                // bisect E-W longitude
                double lonMid = (lonMin + lonMax) / 2;

                if (lon >= lonMid) {
                    idx = idx * 2 + 1;
                    lonMin = lonMid;
                } else {
                    idx = idx * 2;
                    lonMax = lonMid;
                }
            } else {
                // bisect N-S latitude
                double latMid = (latMin + latMax) / 2;

                if (lat >= latMid) {
                    idx = idx * 2 + 1;
                    latMin = latMid;
                } else {
                    idx = idx * 2;
                    latMax = latMid;
                }
            }

            evenBit = !evenBit;

            if (++bit == 5) {
                // 5 bits gives us a character: append it and start over
                locationId.append(UnlCore.base32.charAt(idx));
                bit = 0;
                idx = 0;
            }
        }

        int elevation = elevationOptions.getElevationNumber();
        String elevationType = elevationOptions.getElevationType();

        return UnlCore.getInstance().appendElevation(
                locationId.toString(),
                new Elevation(elevation, elevationType)
        );
    }

    /**
     * Decode locationId to latitude/longitude and elevation (location is approximate centre of locationId cell,
     * to reasonable precision).
     *
     * @param {string} locationId - LocationId string to be converted to latitude/longitude.
     * @throws Invalid locationId.
     * @returns {{lat:number, lon:number, elevation:number, elevationType:string, bounds:{sw: {lat: number, lon: number}, ne: {lat: number, lon: number}, elevation: number, elevationType: string}}} Center of locationId, elevation and SW/NE latitude/longitude bounds of the locationId.
     * @example var latlon = UnlCore.decode('u120fxw'); // => { lat: 52.205, lon: 0.1188, elevation:0, elevationType: floor, bounds: {elevation:0, elevationType:floor, ne: {lat: 52.205657958984375, lon: 0.119476318359375}, sw: {lat: 52.20428466796875, lon: 0.11810302734375}}}
     * var latlon = UnlCore.decode('u120fxw@3'); // => { lat: 52.205, lon: 0.1188, elevation:3, elevationType: floor,  bounds: {elevation:0, elevationType:floor, ne: {lat: 52.205657958984375, lon: 0.119476318359375}, sw: {lat: 52.20428466796875, lon: 0.11810302734375}}}
     * var latlon = UnlCore.decode('u120fxw#87'); // => { lat: 52.205, lon: 0.1188, elevation:87, elevationType: heightincm,  bounds: {elevation:0, elevationType:floor, ne: {lat: 52.205657958984375, lon: 0.119476318359375}, sw: {lat: 52.20428466796875, lon: 0.11810302734375}}}
     */
    public PointWithElevation decode(String locationId) {
        LocationIdWithElevation locationIdWithElevation = excludeElevation(locationId);
        BoundsWithElevation bounds = bounds(locationIdWithElevation.getLocationId());

        double latMin = bounds.getSw().getLat(), lonMin = bounds.getSw().getLon();
        double latMax = bounds.getNe().getLat(), lonMax = bounds.getNe().getLon();

        // cell centre
        double lat = (latMin + latMax) / 2;
        double lon = (lonMin + lonMax) / 2;

        // round to close to centre without excessive precision: ⌊2-log10(Δ°)⌋ decimal places
        lat = new BigDecimal(lat).setScale((int) Math.floor(2 - Math.log(latMax - latMin) / Math.log(10))).doubleValue();
        lon = new BigDecimal(lon).setScale((int) Math.floor(2 - Math.log(lonMax - lonMin) / Math.log(10))).doubleValue();

        return new PointWithElevation(
                new Point(lat, lon),
                locationIdWithElevation.getElevation(),
                bounds
        );
    }

    /**
     * Adds elevation chars and elevation
     * It is mainly used by internal functions
     *
     * @param {string} locationIdWithoutElevation - LocationId without elevation chars.
     * @param {string} elevation - Height of the elevation.
     * @param {string} elevationType - floor | heightincm.
     * @throws Invalid locationId.
     * @returns {string}
     */
    public String appendElevation(String locationIdWithoutElevation, Elevation elevationOptions) {
        if (locationIdWithoutElevation.length() < 0) {
            throw new Error("Invalid locationId");
        }

        if (elevationOptions.getElevationNumber() == 0) {
            return locationIdWithoutElevation;
        }

        char elevationChar = '@';
        if (elevationOptions.getElevationType() == "heightincm") {
            elevationChar = '#';
        }

        return locationIdWithoutElevation + elevationChar + elevationOptions.getElevationNumber();
    }

    /**
     * Returns locationId and elevation properties.
     * It is mainly used by internal functions
     *
     * @param {string} locationIdWithElevation - LocationId with elevation chars.
     * @throws Invalid locationId.
     * @returns {locationId: string, elevation: Number, elevationType: string }
     */
    public LocationIdWithElevation excludeElevation(String locationIdWithElevation) {
        if (locationIdWithElevation.length() < 0) {
            throw new Error("Invalid locationId");
        }

        if (locationIdWithElevation.contains("#") && locationIdWithElevation.contains("@")) {
            throw new Error("Invalid locationId");
        }

        String locationIdWithoutElevation = locationIdWithElevation.toLowerCase();
        String elevationType = "floor";
        int elevation = 0;

        if (locationIdWithElevation.contains("#")) {
            locationIdWithoutElevation = locationIdWithElevation.split("#")[0];
            elevation = Integer.parseInt(locationIdWithElevation.split("#")[1]);
            elevationType = "heightincm";
        }

        if (locationIdWithElevation.contains("@")) {
            locationIdWithoutElevation = locationIdWithElevation.split("@")[0];
            elevation = Integer.parseInt(locationIdWithElevation.split("@")[1]);
        }

        return new LocationIdWithElevation(locationIdWithElevation, new Elevation(elevation, elevationType));
    }

    public BoundsWithElevation bounds(String locationId) {
        LocationIdWithElevation locationIdWithElevation = excludeElevation(locationId);
        String locationIdWithoutElevation = locationIdWithElevation.getLocationId();

        boolean evenBit = true;
        double latMin = -90, latMax = 90;
        double lonMin = -180, lonMax = 180;

        for (int i = 0; i < locationIdWithoutElevation.length(); i++) {
            char chr = locationIdWithoutElevation.charAt(i);
            int idx = UnlCore.base32.indexOf(chr);

            if (idx == -1) throw new Error("Invalid locationId");

            for (int n = 4; n >= 0; n--) {
                int bitN = (idx >> n) & 1;

                if (evenBit) {
                    // longitude
                    double lonMid = (lonMin + lonMax) / 2;

                    if (bitN == 1) {
                        lonMin = lonMid;
                    } else {
                        lonMax = lonMid;
                    }
                } else {
                    // latitude
                    double latMid = (latMin + latMax) / 2;

                    if (bitN == 1) {
                        latMin = latMid;
                    } else {
                        latMax = latMid;
                    }
                }

                evenBit = !evenBit;
            }

        }

        BoundsWithElevation resultBounds = new BoundsWithElevation(
                new Point(latMin, lonMin),
                new Point(latMax, lonMax),
                new Elevation(locationIdWithElevation.getElevation().getElevationNumber(), locationIdWithElevation.getElevation().getElevationType())
        );

        return resultBounds;
    }

    /**
     * Determines adjacent cell in given direction.
     *
     * @param locationId - Cell to which adjacent cell is required.
     * @param direction  - Direction from locationId (N/S/E/W).
     * @throws Invalid locationId.
     * @returns {string} LocationId of adjacent cell.
     */
    public String adjacent(String locationId, String direction) {
        // based on github.com/davetroy/geohash-js
        LocationIdWithElevation locationIdWithElevation = excludeElevation((locationId));
        String locationIdString = locationIdWithElevation.getLocationId();
        int elevation = locationIdWithElevation.getElevation().getElevationNumber();
        String elevationType = locationIdWithElevation.getElevation().getElevationType();


        String directionChar = direction.toLowerCase();
        int directionNumber = 0;

        if (locationIdString.length() == 0) {
            throw new Error("Invalid locationId");
        }
        if (!"nsew".contains(direction)) {
            throw new Error("Invalid direction");
        }

        switch (directionChar) {
            case "s":
                directionNumber = 1;
                break;
            case "e":
                directionNumber = 2;
                break;
            case "w":
                directionNumber = 3;
                break;
            default:
                directionNumber = 0;
        }

        String[][] neighbour = {
                {"p0r21436x8zb9dcf5h7kjnmqesgutwvy", "bc01fg45238967deuvhjyznpkmstqrwx"}, //n
                {"14365h7k9dcfesgujnmqp0r2twvyx8zb", "238967debc01fg45kmstqrwxuvhjyznp"}, //s
                {"bc01fg45238967deuvhjyznpkmstqrwx", "p0r21436x8zb9dcf5h7kjnmqesgutwvy"}, //e
                {"238967debc01fg45kmstqrwxuvhjyznp", "14365h7k9dcfesgujnmqp0r2twvyx8zb"} //w
        };
        String[][] border = {
                {"prxz", "bcfguvyz"}, //n
                {"028b", "0145hjnp"}, //s
                {"bcfguvyz", "prxz"}, //e
                {"0145hjnp", "028b"} //w
        };

        char lastCh = locationIdString.charAt(locationIdString.length() - 1); // last character of hash
        String parent = locationIdString.substring(0, locationIdString.length() - 1); // hash without last character
        int type = locationIdString.length() % 2;

        // check for edge-cases which don't share common prefix
        if (border[directionNumber][type].indexOf(lastCh) != -1 && !parent.equals("")) {
            parent = adjacent(parent, direction);
        }

        // append letter for direction to parent
        String nextLocationId =
                parent + UnlCore.base32.charAt(neighbour[directionNumber][type].indexOf(lastCh));

        if (elevation != 0 && !elevationType.equals("")) {
            return appendElevation(nextLocationId, locationIdWithElevation.getElevation());
        }

        return nextLocationId;
    }

    /**
     * Returns all 8 adjacent cells to specified locationId.
     *
     * @param {string} locationId - LocationId neighbours are required of.
     * @throws Invalid locationId.
     * @returns {{n,ne,e,se,s,sw,w,nw: string}}
     */
    public Neighbour neighbour(String locationId) {
        return new Neighbour(
                adjacent(locationId, "n"),
                adjacent(adjacent(locationId, "n"), "e"),
                adjacent(locationId, "e"),
                adjacent(adjacent(locationId, "s"), "e"),
                adjacent(locationId, "s"),
                adjacent(adjacent(locationId, "s"), "w"),
                adjacent(locationId, "w"),
                adjacent(adjacent(locationId, "n"), "w")
        );
    }
}
