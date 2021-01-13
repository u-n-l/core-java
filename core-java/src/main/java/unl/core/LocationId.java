package unl.core;

public class LocationId {
    private String locationId;
    private Elevation elevation;

    public LocationId(String locationId, Elevation elevation) {
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
