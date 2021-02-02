package unl.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class LocationIdWithElevation {
    @NotNull
    private String locationId;
    @NotNull
    private Elevation elevation;

    public LocationIdWithElevation(@NotNull String locationId, @NotNull Elevation elevation) {
        this.locationId = locationId;
        this.elevation = elevation;
    }

    public LocationIdWithElevation(@NotNull String locationId) {
        this(locationId, UnlCore.DEFAULT_ELEVATION);
    }

    @NotNull
    public String getLocationId() {
        return this.locationId;
    }

    @NotNull
    public Elevation getElevation() {
        return this.elevation;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationIdWithElevation that = (LocationIdWithElevation) o;
        return Objects.equals(locationId, that.locationId) &&
                Objects.equals(elevation, that.elevation);
    }
}
