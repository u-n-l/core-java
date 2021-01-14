package unl.core;

public class PointWithElevation {
    public Point coordinates;
    public Elevation elevation;
    public BoundsWithElevation bounds;

    public PointWithElevation(Point coordinates, Elevation elevation, BoundsWithElevation bounds) {
        this.coordinates = coordinates;
        this.elevation = elevation;
        this.bounds = bounds;
    }
}
