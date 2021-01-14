package unl.core;

import java.util.Objects;

public class BoundsWithElevation {
    private Bounds bounds;
    private Elevation elevation;

    public BoundsWithElevation(Bounds bounds, Elevation elevation) {
        this.bounds = bounds;
        this.elevation = elevation;
    }

    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundsWithElevation that = (BoundsWithElevation) o;
        return Objects.equals(bounds, that.bounds) &&
                Objects.equals(elevation, that.elevation);
    }
}
