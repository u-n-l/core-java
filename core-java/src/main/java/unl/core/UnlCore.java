package unl.core;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class UnlCore {
    public final static int DEFAULT_PRECISION = 9;
    public final static Elevation DEFAULT_ELEVATION = new Elevation(0, "floor");
    private final static String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";

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
     * Encodes latitude/longitude coordinates to locationId, to specified precision.
     * Elevation information is specified in elevationOptions parameter.
     *
     * @param lat - Latitude in degrees.
     * @param lon - Longitude in degrees.
     * @param precision - Number of characters in resulting locationId.
     * @param elevationOptions - elevation options, including elevation number and type.
     * @throws IllegalArgumentException when the coordinates are invalid.
     * @return locationId of supplied latitude/longitude.
     * @example String locationId = UnlCore.getInstance().encode(52.205, 0.119, 7, new Elevation(9, "floor")); // => 'u120fxw@9'
     */
    public String encode(double lat, double lon, int precision, Elevation elevationOptions) {
        if(Double.isNaN(lat) || Double.isNaN(lon) || Double.isNaN(precision)){
            throw new IllegalArgumentException("Invalid coordinates or precision");
        }

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
                locationId.append(BASE32.charAt(idx));
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
     * Encodes latitude/longitude coordinates to locationId, to specified precision.
     *
     * @param lat - Latitude in degrees.
     * @param lon - Longitude in degrees.
     * @param precision - Number of characters in resulting locationId.
     * @throws IllegalArgumentException when the coordinates are invalid.
     * @return locationId of supplied latitude/longitude.
     * @example String locationId = var locationId = UnlCore.getInstance().encode(52.205, 0.119, new Elevation(2, "floor")); // => 'u120fxw@2'
     */
    public String encode(double lat, double lon, int precision) {
        return encode(lat, lon, precision, DEFAULT_ELEVATION);
    }

    /**
     * Encodes latitude/longitude coordinates to locationId, to default precision: 9.
     * Elevation information is specified in options parameter.
     *
     * @param lat - Latitude in degrees.
     * @param lon - Longitude in degrees.
     * @param elevationOptions - elevation options, including elevation number and type.
     * @throws IllegalArgumentException - the coordinates are invalid.
     * @return locationId of supplied latitude/longitude.
     * @example String locationId = UnlCore.getInstance().encode(52.205, 0.119, 7); // => 'u120fxw'
     */
    public String encode(double lat, double lon, Elevation elevationOptions) {
        // refine locationId until it matches precision of supplied lat/lon
        for (int p = 1; p <= DEFAULT_PRECISION; p++) {
            String hash = encode(lat, lon, p);
            PointWithElevation posn = decode(hash);
            if (posn.getCoordinates().getLat() == lat && posn.getCoordinates().getLon() == lon)
                return hash;
        }

        return encode(lat, lon, DEFAULT_PRECISION , elevationOptions);
    }

    /**
     * Encodes latitude/longitude coordinates to locationId, to default precision: 9.
     *
     * @param lat - Latitude in degrees.
     * @param lon - Longitude in degrees.
     * @throws IllegalArgumentException when the coordinates are invalid
     * @return locationId of supplied latitude/longitude.
     * @example String locationId = UnlCore.getInstance().encode(57.64, 10.41); // => 'u4pruvh36'
     */
    public String encode(double lat, double lon) {
        return encode(lat, lon, DEFAULT_ELEVATION);
    }

    /**
     * Decode locationId to latitude/longitude and elevation (location is approximate centre of locationId cell,
     * to reasonable precision).
     *
     * @param locationId - LocationId string to be converted to latitude/longitude.
     * @throws IllegalArgumentException - the LocationId is invalid.
     * @return an instance of PointWithElevation, containing: center of locationId, elevation and SW/NE latitude/longitude bounds of the locationId.
     * @example PointWithElevation pointWithElevation = UnlCore.getInstance().decode('u120fxw'); // => new PointWithElevation(new Point(52.205, 0.1188), new Elevation(0, "floor"), new BoundsWithElevation(new Bounds(new Point(52.20428466796875, 0.11810302734375), new Point(52.205657958984375, 0.119476318359375)), new Elevation(0, "floor")))
     * PointWithElevation pointWithElevation = UnlCore.getInstance().decode('u120fxw@3'); // => new PointWithElevation(new Point(52.205, 0.1188), new Elevation(3, "floor"), new BoundsWithElevation(new Bounds(new Point(52.20428466796875, 0.11810302734375), new Point(52.205657958984375, 0.119476318359375)), new Elevation(3, "floor")))
     * PointWithElevation pointWithElevation = UnlCore.getInstance().decode('u120fxw#87'); // => new PointWithElevation(new Point(52.205, 0.1188), new Elevation(87, "heightincm"), new BoundsWithElevation(new Bounds(new Point(52.20428466796875, 0.11810302734375), new Point(52.205657958984375, 0.119476318359375)), new Elevation(87, "heightincm")))
     */
    public PointWithElevation decode(String locationId) {
        LocationIdWithElevation locationIdWithElevation = excludeElevation(locationId);
        BoundsWithElevation boundsWithElevation = bounds(locationIdWithElevation.getLocationId());
        Bounds bounds = boundsWithElevation.getBounds();

        double latMin = bounds.getSw().getLat(), lonMin = bounds.getSw().getLon();
        double latMax = bounds.getNe().getLat(), lonMax = bounds.getNe().getLon();

        // cell centre
        double lat = (latMin + latMax) / 2;
        double lon = (lonMin + lonMax) / 2;

        // round to close to centre without excessive precision: ⌊2-log10(Δ°)⌋ decimal places
        lat = new BigDecimal(lat).setScale((int) Math.floor(2 - Math.log(latMax - latMin) / Math.log(10)),  BigDecimal.ROUND_HALF_DOWN).doubleValue();
        lon = new BigDecimal(lon).setScale((int) Math.floor(2 - Math.log(lonMax - lonMin) / Math.log(10)), BigDecimal.ROUND_HALF_DOWN).doubleValue();

        return new PointWithElevation(
                new Point(lat, lon),
                locationIdWithElevation.getElevation(),
                boundsWithElevation
        );
    }

    /**
     * Adds elevation chars and elevation
     * It is mainly used by internal functions
     *
     * @param locationIdWithoutElevation - LocationId without elevation chars.
     * @param elevationOptions - instance of Elevation, having the height of the elevation and elevation type (floor | heightincm) as attributes.
     * @throws IllegalArgumentException - the LocationId is invalid.
     * @return string containing locationId and elevation info
     */
    public String appendElevation(String locationIdWithoutElevation, Elevation elevationOptions) {
        if (locationIdWithoutElevation.length() < 0) {
            throw new IllegalArgumentException("Invalid locationId");
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
     * @param locationIdWithElevation - LocationId with elevation chars.
     * @throws  IllegalArgumentException - the LocationId is invalid.
     * @return An instance of LocationIdWithElevation.
     */
    public LocationIdWithElevation excludeElevation(String locationIdWithElevation) {
        if (locationIdWithElevation.length() < 0) {
            throw new IllegalArgumentException("Invalid locationId");
        }

        if (locationIdWithElevation.contains("#") && locationIdWithElevation.contains("@")) {
            throw new IllegalArgumentException("Invalid locationId");
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

        return new LocationIdWithElevation(locationIdWithoutElevation, new Elevation(elevation, elevationType));
    }

    /**
     * Returns SW/NE latitude/longitude bounds of specified locationId cell.
     *
     * @param locationId - Cell that bounds are required of.
     * @return instance of BoundsWithElevation having the sw/ne latitude/longitude bounds of specified locationId cell together with the elevation information
     * @throws IllegalArgumentException - the LocationId is invalid.
     */
    public BoundsWithElevation bounds(String locationId) {
        LocationIdWithElevation locationIdWithElevation = excludeElevation(locationId);
        String locationIdWithoutElevation = locationIdWithElevation.getLocationId();

        boolean evenBit = true;
        double latMin = -90, latMax = 90;
        double lonMin = -180, lonMax = 180;

        for (int i = 0; i < locationIdWithoutElevation.length(); i++) {
            char chr = locationIdWithoutElevation.charAt(i);
            int idx = BASE32.indexOf(chr);

            if (idx == -1) throw new IllegalArgumentException("Invalid locationId");

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
                new Bounds(
                        new Point(latMin, lonMin),
                        new Point(latMax, lonMax)
                ),
                new Elevation(locationIdWithElevation.getElevation().getElevationNumber(), locationIdWithElevation.getElevation().getElevationType())
        );

        return resultBounds;
    }

    /**
     * Determines adjacent cell in given direction.
     *
     * @param locationId - Cell to which adjacent cell is required.
     * @param direction  - Direction from locationId (N/S/E/W).
     * @throws IllegalArgumentException - the LocationId is invalid.
     * @return LocationId of adjacent cell.
     */
    public String adjacent(String locationId, String direction) {
        final String DIRECTIONS_STRING = "nsew";
        // based on github.com/davetroy/geohash-js
        LocationIdWithElevation locationIdWithElevation = excludeElevation((locationId));
        String locationIdString = locationIdWithElevation.getLocationId();
        int elevation = locationIdWithElevation.getElevation().getElevationNumber();
        String elevationType = locationIdWithElevation.getElevation().getElevationType();


        String directionChar = direction.toLowerCase();
        int directionNumber;

        if (locationIdString.length() == 0) {
            throw new IllegalArgumentException("Invalid locationId");
        }
        if (!DIRECTIONS_STRING.contains(direction)) {
            throw new IllegalArgumentException("Invalid direction");
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
                parent + BASE32.charAt(neighbour[directionNumber][type].indexOf(lastCh));

        if (elevation != 0 && !elevationType.equals("")) {
            return appendElevation(nextLocationId, locationIdWithElevation.getElevation());
        }

        return nextLocationId;
    }

    /**
     * Returns all 8 adjacent cells to specified locationId.
     *
     * @param locationId - LocationId neighbours are required of.
     * @throws IllegalArgumentException - the LocationId is invalid.
     * @return and instance of Neighbour class containing the 8 adjacent cells of the specified locationId: n,ne,e,se,s,sw,w,nw.
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

    /**
     * Returns the vertical and horizontal lines that can be used to draw a UNL grid in the specified
     * SW/NE latitude/longitude bounds and precision. Each line is represented by an array of two
     * coordinates in the format: [[startLon, startLat], [endLon, endLat]].
     *
     * @param bounds - The bound within to return the grid lines.
     * @param precision - Number of characters to consider for the locationId of a grid cell
     * @return A list of [[number, number],[number, number]] representing the grid lines.
     */
    public List<double[][]> gridLines(Bounds bounds, int precision) {
        List<double[][]> lines = new ArrayList<>();

        double lonMin = bounds.getSw().getLon();
        double lonMax = bounds.getNe().getLon();

        double latMin = bounds.getSw().getLat();
        double latMax = bounds.getNe().getLat();


        String swCellLocationId = encode(
                bounds.getSw().getLat(),
                bounds.getSw().getLon(),
                precision,
                DEFAULT_ELEVATION
        );
        BoundsWithElevation swCellBounds = bounds(swCellLocationId);

        double latStart = swCellBounds.getBounds().getNe().getLat();
        double lonStart = swCellBounds.getBounds().getNe().getLon();

        String currentCellLocationId = swCellLocationId;
        BoundsWithElevation currentCellBounds = swCellBounds;
        double currentCellNorthLatitude = latStart;

        while (currentCellNorthLatitude <= latMax) {
            lines.add(new double[][]{{lonMin, currentCellNorthLatitude}, {lonMax, currentCellNorthLatitude}});

            currentCellLocationId = adjacent(currentCellLocationId, "n");
            currentCellBounds = bounds(currentCellLocationId);
            currentCellNorthLatitude = currentCellBounds.getBounds().getNe().getLat();
        }

        currentCellLocationId = swCellLocationId;
        double currentCellEastLongitude = lonStart;

        while (currentCellEastLongitude <= lonMax) {
            lines.add(new double[][]{{currentCellEastLongitude, latMin}, {currentCellEastLongitude, latMax}});

            currentCellLocationId = adjacent(currentCellLocationId, "e");
            currentCellBounds = bounds(currentCellLocationId);
            currentCellEastLongitude = currentCellBounds.getBounds().getNe().getLon();
        }

        return lines;
    }

    /**
     * Returns the vertical and horizontal lines that can be used to draw a UNL grid in the specified
     * SW/NE latitude/longitude bounds, using the default precision: 9. Each line is represented by an array of two
     * coordinates in the format: [[startLon, startLat], [endLon, endLat]].
     *
     * @param bounds - The bound within to return the grid lines.
     * @return A list of [[number, number],[number, number]] representing the grid lines.
     */
    public List<double[][]> gridLines(Bounds bounds) {
        return gridLines(bounds, DEFAULT_PRECISION);
    }
}
