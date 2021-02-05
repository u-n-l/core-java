package unl.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PointWithElevation {
    @NotNull
    public Point coordinates;
    @NotNull
    public Elevation elevation;
    @NotNull
    public Bounds bounds;

    public PointWithElevation(@NotNull Point coordinates, @NotNull Elevation elevation, @NotNull Bounds bounds) {
        this.coordinates = coordinates;
        this.elevation = elevation;
        this.bounds = bounds;
    }

    public PointWithElevation(@NotNull Point coordinates, @NotNull Bounds bounds) {
        this(coordinates, UnlCore.DEFAULT_ELEVATION, bounds);
    }

    public Point getCoordinates() {
        return coordinates;
    }

    @NotNull
    public Elevation getElevation() {
        return elevation;
    }

    @NotNull
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointWithElevation that = (PointWithElevation) o;
        return Objects.equals(coordinates, that.coordinates) &&
                Objects.equals(elevation, that.elevation) &&
                Objects.equals(bounds, that.bounds);
    }
}
