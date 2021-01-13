package unl.core;

public class Elevation {
    private int elevationNumber;
    private String elevationType;

    public Elevation(int elevationNumber, String elevationType) {
        this.elevationNumber = elevationNumber;
        this.elevationType = elevationType;
    }

    public int getElevationNumber() {
        return this.elevationNumber;
    }

    public String getElevationType() {
        return this.elevationType;
    }
}
