package unl.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Elevation {
    private int elevationNumber;
    @NotNull
    private String elevationType;

    public Elevation(int elevationNumber, @Nullable String elevationType) {
        this.elevationNumber = elevationNumber;
        if (elevationType == null) {
            this.elevationType = "floor";
        } else {
            this.elevationType = elevationType;
        }
    }

    public Elevation(int elevationNumber) {
        this.elevationNumber = elevationNumber;
        this.elevationType = "floor";
    }

    public int getElevationNumber() {
        return this.elevationNumber;
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
        return elevationNumber == elevation.elevationNumber &&
                Objects.equals(elevationType, elevation.elevationType);
    }
}
