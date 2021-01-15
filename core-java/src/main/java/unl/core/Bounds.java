package unl.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Bounds {
    @NotNull
    private Point sw;
    @NotNull
    private Point ne;

    public Bounds(@NotNull Point sw,@NotNull Point ne) {
        this.sw = sw;
        this.ne = ne;
    }

    @NotNull
    public Point getSw() {
        return this.sw;
    }
    @NotNull
    public Point getNe() {
        return this.ne;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bounds bounds = (Bounds) o;
        return Objects.equals(sw, bounds.sw) &&
                Objects.equals(ne, bounds.ne);
    }
}
