package fieldvisualizer;

/**
 * A simple class that represents a line: two points.
 * 
 * @author Jeff Niu
 */
public class Line {

    /**
     * The first point.
     */
    public final Vector p;
    /**
     * The second point.
     */
    public final Vector q;

    /**
     * Create a new line.
     * 
     * @param p the first point
     * @param q the second point
     */
    public Line(Vector p, Vector q) {
        this.p = p;
        this.q = q;
    }

}
