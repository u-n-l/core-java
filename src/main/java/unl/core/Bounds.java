package unl.core;

public class Bounds {
    private double n;
    private double e;
    private double s;
    private double w;

    public Bounds(double n, double e, double s, double w) {
        this.n = n;
        this.e = e;
        this.s = s;
        this.w = w;
    }

    public double getN() {
        return n;
    }

    public double getS() {
        return s;
    }

    public double getE() {
        return e;
    }

    public double getW() {
        return w;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bounds bounds = (Bounds) o;
        return Double.compare(bounds.n, n) == 0 &&
                Double.compare(bounds.e, e) == 0 &&
                Double.compare(bounds.s, s) == 0 &&
                Double.compare(bounds.w, w) == 0;
    }
}
