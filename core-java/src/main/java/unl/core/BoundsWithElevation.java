package unl.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BoundsWithElevation {
    @NotNull
    private Bounds bounds;
    @NotNull
    private Elevation elevation;

    public BoundsWithElevation(@NotNull Bounds bounds,@NotNull Elevation elevation) {
        this.bounds = bounds;
        this.elevation = elevation;
    }

    @NotNull
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoundsWithElevation that = (BoundsWithElevation) o;
        return Objects.equals(bounds, that.bounds) &&
                Objects.equals(elevation, that.elevation);
    }
}
