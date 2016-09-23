package fieldvisualizer;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.List;

/**
 * A bar magnet is a typical magnet with a north and south pole directly
 * opposite of each other in a rectangle. The bar magnet magnetic field and
 * behavior is emulated using two charges: one positive charge and one
 * negative charge.
 *
 * @author Jeff Niu
 */
public class BarMagnet implements Charge {

    /**
     * The point charge that represents the north end of the magnet.
     */
    public final PointCharge north;
    /**
     * The point charge that represents the south end of the magnet.
     */
    public final PointCharge south;

    /**
     * Create a new bar magnet.
     *
     * @param x the center x coordinate
     * @param y the center y coordinate
     * @param q the magnetic strength
     * @param l the length of the magnet from the center to the edge
     * @param arg the orientation angle of the magnet
     */
    public BarMagnet(double x, double y, double q, double l, double arg) {
        double lsina = l * sin(arg);
        double lcosa = l * cos(arg);
        double tx1 = x + lcosa;
        double ty1 = y + lsina;
        double tx2 = x - lcosa;
        double ty2 = y - lsina;
        north = new PointCharge(tx1, ty1, q);
        south = new PointCharge(tx2, ty2, -q);
    }

    /**
     * The magnetic field vector at a point around the bar magnet is the
     * sum of the vectors around the positive north and negative south
     * charges that emulate the magnet.
     *
     * @param x point x coordinate
     * @param y point y coordinate
     * @return
     */
    @Override
    public Vector fieldAt(double x, double y) {
        return north.fieldAt(x, y).add(south.fieldAt(x, y));
    }

    /**
     * The field line trace around a bar magnet is equivalent to the trace
     * around both the north and south charges.
     *
     * @param angle the trace projection angle
     * @param step the trace resolution
     * @param charges the array of charges
     * @return a set of path iterators 
     */
    @Override
    public List<PathIterator> trace(double angle, double step,
            Charge[] charges) {
        List<PathIterator> trace = new ArrayList<>();
        trace.addAll(north.trace(angle, step, charges));
        trace.addAll(south.trace(angle, step, charges));
        return trace;
    }

}
