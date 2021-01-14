package unl.core;

public class BoundsWithElevation {
    private Bounds bounds;
    private Elevation elevation;

    public BoundsWithElevation(Point sw, Point ne, Elevation elevation) {

        this.elevation = elevation;
    }

    public Bounds getBounds() {
        return bounds;
    }
}
