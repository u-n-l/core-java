package unl.core;

import org.jetbrains.annotations.NotNull;

public class Location {
    @NotNull
    private Point point;
    @NotNull
    private Elevation elevation;
    @NotNull
    private BoundsWithElevation bounds;
    @NotNull
    private String geohash;
    @NotNull
    private String words;


    public Location(@NotNull Point point, @NotNull Elevation elevation, @NotNull BoundsWithElevation bounds, @NotNull String geohash, @NotNull String words) {
        this.point = point;
        this.elevation = elevation;
        this.bounds = bounds;
        this.geohash = geohash;
        this.words = words;
    }

    @NotNull
    public Point getPoint() {
        return point;
    }

    @NotNull
    public Elevation getElevation() {
        return elevation;
    }

    @NotNull
    public BoundsWithElevation getBounds() {
        return bounds;
    }

    @NotNull
    public String getGeohash() {
        return geohash;
    }

    @NotNull
    public String getWords() {
        return words;
    }
}
