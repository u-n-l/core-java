package unl.core;

public class BoundsWithElevation {
    private Point sw;
    private Point ne;
    private Elevation elevation;

    public BoundsWithElevation(Point sw, Point ne, Elevation elevation) {
        this.sw = sw;
        this.ne = ne;
        this.elevation = elevation;
    }

    public Point getSw() {
        return this.sw;
    }

    public Point getNe() {
        return this.ne;
    }
}
