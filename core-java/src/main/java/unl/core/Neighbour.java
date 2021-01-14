package unl.core;

import java.util.Objects;

public class Neighbour {
    public String n;
    public String ne;
    public String e;
    public String se;
    public String s;
    public String sw;
    public String w;
    public String nw;

    public Neighbour(String n, String ne, String e, String se, String s, String sw, String w, String nw) {
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
    public boolean equals(Object o) {
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
