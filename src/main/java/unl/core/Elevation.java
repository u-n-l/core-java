package unl.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Elevation {
    private int elevation;
    @NotNull
    private String elevationType;

    public Elevation(int elevation, @NotNull String elevationType) {
        this.elevation = elevation;
        this.elevationType = elevationType;
    }

    public Elevation(int elevation) {
        this(elevation, "floor");
    }

    public int getElevation() {
        return this.elevation;
    }

    @NotNull
    public String getElevationType() {
        return this.elevationType;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Elevation elevation = (Elevation) o;
        return this.elevation == elevation.elevation &&
                Objects.equals(elevationType, elevation.elevationType);
    }
}
