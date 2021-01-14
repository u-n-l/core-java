package unl.core;

public class Bounds {
    private Point sw;
    private Point ne;

    public Bounds(Point sw, Point ne) {
        this.sw = sw;
        this.ne = ne;
    }

    public Point getSw() {
        return this.sw;
    }

    public Point getNe() {
        return this.ne;
    }
}
