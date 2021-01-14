package unl.core;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationIdWithElevation that = (LocationIdWithElevation) o;
        return Objects.equals(locationId, that.locationId) &&
                Objects.equals(elevation, that.elevation);
    }
}
