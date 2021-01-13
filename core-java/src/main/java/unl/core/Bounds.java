package unl.core;

public class Bounds {
    private Coordinates sw;
    private Coordinates ne;
    private Elevation elevation;

    public Bounds(Coordinates sw, Coordinates ne, Elevation elevation) {
        this.sw = sw;
        this.ne = ne;
        this.elevation = elevation;
    }

    public Coordinates getSw() {
        return this.sw;
    }

    public Coordinates getNe() {
        return this.ne;
    }
}
