package unl.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class UnlCore {
    public final static int DEFAULT_PRECISION = 9;
    public final static Elevation DEFAULT_ELEVATION = new Elevation(0, "floor");
    private final static String BASE32 = "0123456789bcdefghjkmnpqrstuvwxyz";
    private final static String LOCATION_ID_REGEX = "^[0123456789bcdefghjkmnpqrstuvwxyz]{3,16}[@#]?[0-9]{0,3}$";
    private final static String COORDINATES_REGEX = "^-?[0-9]{0,2}\\.?[0-9]{0,16},\\s?-?[0-9]{0,3}\\.?[0-9]{0,16}$";

    private final static String BASE_URL = "https://map.unl.global/api/v1/location/";
    private final static String WORDS_ENDPOINT = "words/";
    private final static String GEOHASH_ENDPOINT = "geohash/";
    private final static String COORDINATES_ENDPOINT = "coordinates/";

    /**
     * The unique instance of the UnlCore class.
     */
    private static UnlCore instance;

    /**
     * @return the unique instance of the UnlCore class.
     */
    @NotNull
    public synchronized static UnlCore getInstance() {
        if (instance == null) {
            instance = new UnlCore();
        }
        return instance;
    }

    /**
     * Encodes latitude/longitude coordinates to locationId, to specified precision.
     * Elevation information is specified in elevation parameter.
     *
     * @param lat       the latitude in degrees.
     * @param lon       the longitude in degrees.
     * @param precision the number of characters in resulting locationId.
     * @param elevation the elevation object, containing the elevation number and type: 'floor' | 'heightincm'.
     * @return the locationId of supplied latitude/longitude.
     * @throws IllegalArgumentException if the coordinates are invalid.
     * @example String locationId = UnlCore.getInstance().encode(52.205, 0.119, 7, new Elevation(9, "floor")); // => 'u120fxw@9'
     */
    @NotNull
    public String encode(double lat, double lon, int precision, @NotNull Elevation elevation) {
        if (Double.isNaN(lat) || Double.isNaN(lon) || Double.isNaN(precision)) {
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

        int elevationNumber = elevation.getElevation();
        String elevationType = elevation.getElevationType();
        Elevation elevationObject = new Elevation(elevationNumber, elevationType);

        return appendElevation(
                locationId.toString(),
                elevationObject
        );
    }

    /**
     * Encodes latitude/longitude coordinates to locationId, to specified precision.
     *
     * @param lat       the latitude in degrees.
     * @param lon       the longitude in degrees.
     * @param precision the number of characters in resulting locationId.
     * @return the locationId of supplied latitude/longitude.
     * @throws IllegalArgumentException if the coordinates are invalid.
     * @example String locationId = var locationId = UnlCore.getInstance().encode(52.205, 0.119, new Elevation(2, "floor")); // => 'u120fxw@2'
     */
    @NotNull
    public String encode(double lat, double lon, int precision) {
        return encode(lat, lon, precision, DEFAULT_ELEVATION);
    }

    /**
     * Encodes latitude/longitude coordinates to locationId, to default precision: 9.
     * Elevation information is specified in elevation parameter.
     *
     * @param lat       the latitude in degrees.
     * @param lon       the longitude in degrees.
     * @param elevation the elevation object, containing elevation number and type.
     * @return the locationId of supplied latitude/longitude.
     * @throws IllegalArgumentException if the coordinates are invalid.
     * @example String locationId = UnlCore.getInstance().encode(52.205, 0.119, 7); // => 'u120fxw'
     */
    @NotNull
    public String encode(double lat, double lon, @NotNull Elevation elevation) {
        // refine locationId until it matches precision of supplied lat/lon
        for (int p = 1; p <= DEFAULT_PRECISION; p++) {
            String hash = encode(lat, lon, p);
            PointWithElevation posn = decode(hash);
            if (posn.getCoordinates().getLat() == lat && posn.getCoordinates().getLon() == lon)
                return hash;
        }

        return encode(lat, lon, DEFAULT_PRECISION, elevation);
    }

    /**
     * Encodes latitude/longitude coordinates to locationId, to default precision: 9.
     *
     * @param lat the latitude in degrees.
     * @param lon the longitude in degrees.
     * @return the locationId of supplied latitude/longitude.
     * @throws IllegalArgumentException if the coordinates are invalid.
     * @example String locationId = UnlCore.getInstance().encode(57.64, 10.41); // => 'u4pruvh36'
     */
    @NotNull
    public String encode(double lat, double lon) {
        return encode(lat, lon, DEFAULT_ELEVATION);
    }

    /**
     * Decode locationId to latitude/longitude and elevation (location is approximate centre of locationId cell,
     * to reasonable precision).
     *
     * @param locationId the locationId string to be converted to latitude/longitude.
     * @return an instance of PointWithElevation, containing: center of locationId, elevation info and SW/NE latitude/longitude bounds of the locationId.
     * @throws IllegalArgumentException if the locationId is invalid.
     * @example PointWithElevation pointWithElevation = UnlCore.getInstance().decode('u120fxw'); // => new PointWithElevation(new Point(52.205, 0.1188), new Elevation(0, "floor"), new BoundsWithElevation(new Bounds(new Point(52.20428466796875, 0.11810302734375), new Point(52.205657958984375, 0.119476318359375)), new Elevation(0, "floor")))
     * PointWithElevation pointWithElevation = UnlCore.getInstance().decode('u120fxw@3'); // => new PointWithElevation(new Point(52.205, 0.1188), new Elevation(3, "floor"), new BoundsWithElevation(new Bounds(new Point(52.20428466796875, 0.11810302734375), new Point(52.205657958984375, 0.119476318359375)), new Elevation(3, "floor")))
     * PointWithElevation pointWithElevation = UnlCore.getInstance().decode('u120fxw#87'); // => new PointWithElevation(new Point(52.205, 0.1188), new Elevation(87, "heightincm"), new BoundsWithElevation(new Bounds(new Point(52.20428466796875, 0.11810302734375), new Point(52.205657958984375, 0.119476318359375)), new Elevation(87, "heightincm")))
     */
    @NotNull
    public PointWithElevation decode(@NotNull String locationId) {
        LocationIdWithElevation locationIdWithElevation = excludeElevation(locationId);
        BoundsWithElevation boundsWithElevation = bounds(locationIdWithElevation.getLocationId());
        Bounds bounds = boundsWithElevation.getBounds();

        double latMin = bounds.getSw().getLat(), lonMin = bounds.getSw().getLon();
        double latMax = bounds.getNe().getLat(), lonMax = bounds.getNe().getLon();

        // cell centre
        double lat = (latMin + latMax) / 2;
        double lon = (lonMin + lonMax) / 2;

        // round to close to centre without excessive precision: ⌊2-log10(Δ°)⌋ decimal places
        lat = new BigDecimal(lat).setScale((int) Math.floor(2 - Math.log(latMax - latMin) / Math.log(10)), BigDecimal.ROUND_HALF_DOWN).doubleValue();
        lon = new BigDecimal(lon).setScale((int) Math.floor(2 - Math.log(lonMax - lonMin) / Math.log(10)), BigDecimal.ROUND_HALF_DOWN).doubleValue();

        Point point = new Point(lat, lon);
        return new PointWithElevation(point, locationIdWithElevation.getElevation(), boundsWithElevation);
    }

    /**
     * Adds elevation chars and elevation.
     * It is mainly used by internal functions.
     *
     * @param locationIdWithoutElevation the locationId without elevation chars.
     * @param elevation                  the instance of Elevation, having the height of the elevation and elevation type (floor | heightincm) as attributes.
     * @return a string containing locationId and elevation info.
     * @throws IllegalArgumentException if the locationId is invalid.
     */
    @NotNull
    public String appendElevation(@NotNull String locationIdWithoutElevation, @NotNull Elevation elevation) {
        if (locationIdWithoutElevation.length() < 0) {
            throw new IllegalArgumentException("Invalid locationId");
        }

        if (elevation.getElevation() == 0) {
            return locationIdWithoutElevation;
        }

        char elevationChar = '@';
        if (elevation.getElevationType() == "heightincm") {
            elevationChar = '#';
        }

        return locationIdWithoutElevation + elevationChar + elevation.getElevation();
    }

    /**
     * Returns locationId and elevation properties.
     * It is mainly used by internal functions.
     *
     * @param locationIdWithElevation the locationId with elevation chars.
     * @return an instance of LocationIdWithElevation.
     * @throws IllegalArgumentException if the locationId is invalid.
     */
    @NotNull
    public LocationIdWithElevation excludeElevation(@NotNull String locationIdWithElevation) {
        if (locationIdWithElevation.length() == 0) {
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

        Elevation excludedElevation = new Elevation(elevation, elevationType);
        return new LocationIdWithElevation(locationIdWithoutElevation, excludedElevation);
    }

    /**
     * Returns SW/NE latitude/longitude bounds of specified locationId cell.
     *
     * @param locationId the cell that bounds are required of.
     * @return an instance of BoundsWithElevation having the sw/ne latitude/longitude bounds of specified locationId cell together with the elevation information.
     * @throws IllegalArgumentException if the locationId is invalid.
     */
    @NotNull
    public BoundsWithElevation bounds(@NotNull String locationId) {
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

        Bounds bounds = new Bounds(new Point(latMin, lonMin), new Point(latMax, lonMax));
        Elevation elevation = new Elevation(locationIdWithElevation.getElevation().getElevation(), locationIdWithElevation.getElevation().getElevationType());

        return new BoundsWithElevation(bounds, elevation);
    }

    /**
     * Determines adjacent cell in given direction.
     *
     * @param locationId the cell to which adjacent cell is required.
     * @param direction  the direction from locationId (N/S/E/W).
     * @return the locationId of adjacent cell.
     * @throws IllegalArgumentException if the locationId is invalid.
     */
    @NotNull
    public String adjacent(@NotNull String locationId, @NotNull String direction) {
        final String DIRECTIONS_STRING = "nsew";
        // based on github.com/davetroy/geohash-js
        LocationIdWithElevation locationIdWithElevation = excludeElevation((locationId));
        String locationIdString = locationIdWithElevation.getLocationId();
        int elevation = locationIdWithElevation.getElevation().getElevation();
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
     * @param locationId the locationId neighbours are required of.
     * @return an instance of Neighbour class containing the 8 adjacent cells of the specified locationId: n, ne, e, se, s, sw, w, nw.
     * @throws IllegalArgumentException if the locationId is invalid.
     */
    @NotNull
    public Neighbour neighbour(@NotNull String locationId) {
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
     * @param bounds    the bound within to return the grid lines.
     * @param precision the number of characters to consider for the locationId of a grid cell.
     * @return a list of double[][] representing the grid lines.
     */
    @NotNull
    public List<double[][]> gridLines(@NotNull Bounds bounds, int precision) {
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
     * @param bounds the bound within to return the grid lines.
     * @return a list of double[][] representing the grid lines.
     */
    @NotNull
    public List<double[][]> gridLines(@NotNull Bounds bounds) {
        return gridLines(bounds, DEFAULT_PRECISION);
    }

    /**
     * Returns the location object, which encapsulates the coordinates, elevation, bounds, geohash and words,
     * corresponding to the location string (id or lat-lon coordinates). It requires the api key used to access
     * the location APIs.
     *
     * @param location the location (Id or lat-lon coordinates) of the point for which you would like the address.
     * @param apiKey   the UNL API key used to access the location APIs.
     * @return an instance of Location class, containing the coordinates, elevation, bounds, geohash and words.
     * @throws IllegalArgumentException if the api key string is empty or the location is invalid.
     * @throws UnlCoreException         if the call to location endpoint is unsuccessful.
     */
    @Nullable
    public Location toWords(@NotNull String location, @NotNull String apiKey) throws UnlCoreException {
        if (apiKey.length() == 0) {
            throw new IllegalArgumentException("API key not set");
        }

        String type;
        if (location.matches(LOCATION_ID_REGEX)) {
            type = GEOHASH_ENDPOINT;
        } else if (location.matches(COORDINATES_REGEX)) {
            type = COORDINATES_ENDPOINT;
        } else {
            throw new IllegalArgumentException("Could not interpret your input, " + location + ". Expected a locationId or lat, lon coordinates.");
        }

        String url = BASE_URL + type + location;
        String response = LocationService.callEndpoint(url, apiKey);
        Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationDeserializer()).create();

        return gson.fromJson(response, Location.class);
    }

    /**
     * Returns the location object, which encapsulates the coordinates, elevation, bounds, geohash and words,
     * corresponding to the words string. It requires the api key used to access
     * the location APIs.
     *
     * @param words  the words representing the point for which you would like the coordinates.
     * @param apiKey the UNL API key used to access the location APIs.
     * @return an instance of Location class, containing the coordinates, elevation, bounds, geohash and words.
     * @throws IllegalArgumentException if the api key string is empty.
     * @throws UnlCoreException         if the call to location APIs is unsuccessful.
     */
    @Nullable
    public Location words(@NotNull String words, @Nullable String apiKey) throws UnlCoreException {
        if (apiKey == null || apiKey.length() == 0) {
            throw new IllegalArgumentException("API key not set");
        }

        String url = BASE_URL + WORDS_ENDPOINT + words;
        String response = LocationService.callEndpoint(url, apiKey);
        Gson gson = new GsonBuilder().registerTypeAdapter(Location.class, new LocationDeserializer()).create();

        return gson.fromJson(response, Location.class);
    }
}
