package unl.core;

import org.jetbrains.annotations.Nullable;

public class Point {
    private double lat;
    private double lon;

    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double getLat() {
        return this.lat;
    }

    public double getLon() {
        return this.lon;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.lat, lat) == 0 &&
                Double.compare(point.lon, lon) == 0;
    }
}
