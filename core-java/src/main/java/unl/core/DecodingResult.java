package unl.core;

public class DecodingResult {
    public Coordinates coordinates;
    public Elevation elevation;
    public Bounds bounds;

    public DecodingResult(Coordinates coordinates, Elevation elevation, Bounds bounds) {
        this.coordinates = coordinates;
        this.elevation = elevation;
        this.bounds = bounds;
    }
}
