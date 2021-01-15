package unl.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Neighbour {
    @NotNull
    private String n;
    @NotNull
    private String ne;
    @NotNull
    private String e;
    @NotNull
    private String se;
    @NotNull
    private String s;
    @NotNull
    private String sw;
    @NotNull
    private String w;
    @NotNull
    private String nw;

    public Neighbour(
            @NotNull String n,
            @NotNull String ne,
            @NotNull String e,
            @NotNull String se,
            @NotNull String s,
            @NotNull String sw,
            @NotNull String w,
            @NotNull String nw) {
        this.n = n;
        this.ne = ne;
        this.e = e;
        this.se = se;
        this.s = s;
        this.sw = sw;
        this.w = w;
        this.nw = nw;
    }

    @Override
    public boolean equals(@Nullable  Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neighbour neighbour = (Neighbour) o;
        return Objects.equals(n, neighbour.n) &&
                Objects.equals(ne, neighbour.ne) &&
                Objects.equals(e, neighbour.e) &&
                Objects.equals(se, neighbour.se) &&
                Objects.equals(s, neighbour.s) &&
                Objects.equals(sw, neighbour.sw) &&
                Objects.equals(w, neighbour.w) &&
                Objects.equals(nw, neighbour.nw);
    }
}
