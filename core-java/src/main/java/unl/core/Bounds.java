package unl.core;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bounds bounds = (Bounds) o;
        return Objects.equals(sw, bounds.sw) &&
                Objects.equals(ne, bounds.ne);
    }
}
