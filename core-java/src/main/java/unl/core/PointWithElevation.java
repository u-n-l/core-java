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
    public BoundsWithElevation bounds;

    public PointWithElevation(@NotNull Point coordinates, @Nullable Elevation elevation, @NotNull BoundsWithElevation bounds) {
        this.coordinates = coordinates;
        if(elevation == null){
            this.elevation = UnlCore.DEFAULT_ELEVATION;
        } else {
            this.elevation = elevation;
        }
        this.bounds = bounds;
    }

    public Point getCoordinates() {
        return coordinates;
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
