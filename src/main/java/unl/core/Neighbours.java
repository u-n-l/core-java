package unl.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Neighbours {
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

    public Neighbours(
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

    @NotNull
    public String getN() {
        return n;
    }

    @NotNull
    public String getNe() {
        return ne;
    }

    @NotNull
    public String getE() {
        return e;
    }

    @NotNull
    public String getSe() {
        return se;
    }

    @NotNull
    public String getS() {
        return s;
    }

    @NotNull
    public String getSw() {
        return sw;
    }

    @NotNull
    public String getW() {
        return w;
    }

    @NotNull
    public String getNw() {
        return nw;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Neighbours neighbours = (Neighbours) o;
        return Objects.equals(n, neighbours.n) &&
                Objects.equals(ne, neighbours.ne) &&
                Objects.equals(e, neighbours.e) &&
                Objects.equals(se, neighbours.se) &&
                Objects.equals(s, neighbours.s) &&
                Objects.equals(sw, neighbours.sw) &&
                Objects.equals(w, neighbours.w) &&
                Objects.equals(nw, neighbours.nw);
    }
}
