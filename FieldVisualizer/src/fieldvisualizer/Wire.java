package fieldvisualizer;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;

/**
 * A wire that carries a current generates a circular magnetic field around
 * it. In this program, we may have wires that either carry current into
 * the screen or out of the screen. In this program, it is assumed that the
 * wires carry negative charges -- electrons. So, the left hand rules
 * apply. A solenoid can be created by having all wires of one direction on
 * one side and all of the other on the other side.
 *
 * @author Jeff Niu
 */
public class Wire implements Charge {

    /**
     * The x position.
     */
    public final double x;
    /**
     * The y position.
     */
    public final double y;
    /**
     * The current strength and direction. A negative strength indicates
     * out of the screen and a positive strength indicates into the screen.
     */
    public final double q;

    /**
     * Create a new wire.
     *
     * @param x the x position
     * @param y the y position
     * @param q the current strength and direction
     */
    public Wire(double x, double y, double q) {
        this.x = x;
        this.y = y;
        this.q = q;
    }

    /**
     * The magnetic field vector at a particular point around the wire has
     * an inverse relationship with distance and is circular.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the field vector
     */
    @Override
    public Vector fieldAt(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        double xSq = dx * dx;
        double ySq = dy * dy;
        double rSq = xSq + ySq;
        double r = sqrt(rSq);
        double B = q / r;
        double arg = Vector.arg(dx, dy) + PI / 2;
        return new Vector(B * cos(arg), B * sin(arg));
    }

    /**
     * @return a set of points projected around the wire
     */
    public List<Vector> getOrigins() {
        double step = 150;
        List<Vector> origins = new ArrayList<>();
        for (int i = -2; i <= 2; i++) {
            origins.add(new Vector(x + step * i, y));
        }
        return origins;
    }

    /**
     * @return the trace of the magnetic field lines from the origins
     */
    @Override
    public List<PathIterator> trace(double angle, double step,
            Charge[] charges) {
        List<PathIterator> trace = new ArrayList<>();
        List<Vector> origins = getOrigins();
        //step *= signum(q);
        for (Vector origin : origins) {
            List<Vector> points = new ArrayList<>();
            double px = origin.x;
            double py = origin.y;
            double totalChange = 0;
            double theta1;
            double theta2
                    = Field.fieldAt(origin.x, origin.y, charges).arg();
            int i = 0;
            do {
                theta1 = theta2;
                points.add(new Vector(px, py));
                px += step * cos(theta1);
                py += step * sin(theta1);
                theta2 = Field.fieldAt(px, py, charges).arg();
                totalChange += Vector.angleDiff(theta1, theta2);
                i++;
                if (!Field.nonReversed(theta1, theta2)) {
                    break;
                }
            } while (abs(totalChange) < 2 * PI && i < 1500);
            trace.add(new PathIterator(points.iterator()));
        }
        return trace;
    }

}
