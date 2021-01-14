package unl.core;

public class LocationIdWithElevation {
    private String locationId;
    private Elevation elevation;

    public LocationIdWithElevation(String locationId, Elevation elevation) {
        this.locationId = locationId;
        this.elevation = elevation;
    }

    public String getLocationId() {
        return this.locationId;
    }

    public Elevation getElevation() {
        return this.elevation;
    }
}
