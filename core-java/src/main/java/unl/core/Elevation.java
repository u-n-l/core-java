package unl.core;

import java.util.Objects;

public class Elevation {
    private int elevationNumber;
    private String elevationType;

    public Elevation(int elevationNumber, String elevationType) {
        this.elevationNumber = elevationNumber;
        this.elevationType = elevationType;
    }

    public Elevation(int elevationNumber) {
        this.elevationNumber = elevationNumber;
        this.elevationType = "floor";
    }

    public int getElevationNumber() {
        return this.elevationNumber;
    }

    public String getElevationType() {
        return this.elevationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Elevation elevation = (Elevation) o;
        return elevationNumber == elevation.elevationNumber &&
                Objects.equals(elevationType, elevation.elevationType);
    }
}
