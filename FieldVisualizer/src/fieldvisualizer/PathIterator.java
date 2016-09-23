package fieldvisualizer;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A path iterator describes the traced line of the field lines.
 *
 * @author Jeff Niu
 */
public class PathIterator {

    /**
     * The set of lines that describe the entire equipotential line.
     */
    private final Iterator<Line> path;

    /**
     * Create a new path iterator from a set of points that were traced
     * along the field.
     *
     * @param points the iterator over the points
     */
    public PathIterator(Iterator<Vector> points) {
        double step = 10;
        List<Line> paths = new ArrayList<>();
        int i = 0;
        Vector p = points.next();
        // Convert the points into lines so that they can 
        // be easily accessed later
        while (points.hasNext()) {
            Vector q = points.next();
            paths.add(new Line(p, q));
            i++;
            if (i % 50 == 0) {
                double dx = p.x - q.x;
                double dy = p.y - q.y;
                double arg = Vector.arg(dx, dy);
                double a1 = arg - PI / 6;
                double a2 = arg + PI / 6;
                double tx1 = q.x + step * cos(a1);
                double ty1 = q.y + step * sin(a1);
                double tx2 = q.x + step * cos(a2);
                double ty2 = q.y + step * sin(a2);
                paths.add(new Line(q, new Vector(tx1, ty1)));
                paths.add(new Line(q, new Vector(tx2, ty2)));
            }
            p = q;
        }
        path = paths.iterator();
    }

    /**
     * @return whether the line continues
     */
    public boolean hasNext() {
        return path.hasNext();
    }

    /**
     * @return the next segment in the line
     */
    public Line next() {
        return path.next();
    }

}
