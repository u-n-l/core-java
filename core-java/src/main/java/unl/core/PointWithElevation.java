package unl.core;

import java.util.Objects;

public class PointWithElevation {
    public Point coordinates;
    public Elevation elevation;
    public BoundsWithElevation bounds;

    public PointWithElevation(Point coordinates, Elevation elevation, BoundsWithElevation bounds) {
        this.coordinates = coordinates;
        this.elevation = elevation;
        this.bounds = bounds;
    }

    public Point getCoordinates() {
        return coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointWithElevation that = (PointWithElevation) o;
        return Objects.equals(coordinates, that.coordinates) &&
                Objects.equals(elevation, that.elevation) &&
                Objects.equals(bounds, that.bounds);
    }
}
