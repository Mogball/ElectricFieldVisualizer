package fieldvisualizer;

import static fieldvisualizer.Field.nonReversed;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.List;

/**
 * A point charge is an infinitesimally small object that generates a
 * uniform electric field around it.
 *
 * @author Jeff Niu
 */
public class PointCharge implements Charge {

    /**
     * The x position.
     */
    public double x;
    /**
     * The y position.
     */
    public double y;
    /**
     * The charge, which can be positive or negative and can used to adjust
     * the strength of the electric field around it.
     */
    public double q;

    /**
     * Create a new point charge.
     *
     * @param x the x position
     * @param y the y position
     * @param q the charge
     */
    public PointCharge(double x, double y, double q) {
        this.x = x;
        this.y = y;
        this.q = q;
    }

    /**
     * The origins is the list of initial points from which field lines
     * will be traced. A decision was made to have a negative charge have
     * zero origins. That means that negative charges can only be observed
     * when there are positive charges to project field lines. Otherwise,
     * things get too messy.
     *
     * @param angle the projection angle
     * @return a set of initial points
     */
    public List<Vector> getOrigins(double angle) {
        if (signum(q) < 0) {
            return new ArrayList<>();
        }
        double step = 0.5;
        List<Vector> origins = new ArrayList<>();
        double arg = 0;
        do {
            origins.add(new Vector(x + step * cos(arg),
                    y + step * sin(arg)));
            arg += angle;
        } while (arg < 2 * PI);
        return origins;
    }

    /**
     * The field strength at a particular point around the point charge can
     * be calculated using Coulomb's law and the direction is directly away
     * or towards the charge, depending on its sign.
     *
     * @param x the x point coordinate
     * @param y the y point coordinate
     * @return the field vector at that point
     */
    @Override
    public Vector fieldAt(double x, double y) {
        double dx = x - this.x;
        double dy = y - this.y;
        double xSq = dx * dx;
        double ySq = dy * dy;
        double rSq = xSq + ySq;
        double arg = Vector.arg(dx, dy);
        double mag = q / rSq; //k * q / r / r;
        return new Vector(mag * cos(arg), mag * sin(arg));
    }

    /**
     * Trace the field lines around this charge. The field lines are
     * traced, for a positive charge, from angles originating just around
     * the charge center.
     *
     * @param angle the projection angle
     * @param step the resolution
     * @param charges the list of charges
     * @return a set of paths
     */
    @Override
    public List<PathIterator> trace(double angle, double step,
            Charge[] charges) {
        List<PathIterator> trace = new ArrayList<>();
        List<Vector> origins = getOrigins(angle);
        for (Vector origin : origins) {
            List<Vector> points = new ArrayList<>();
            double px = origin.x;
            double py = origin.y;
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
                double change = Vector.angleDiff(theta1, theta2);
                // This ensures that lines that go really far
                // off the screen are not rendered
                if (px > 1600 || px < 0 || py > 900 || py < -200) {
                    if (abs(change) < 0.002) {
                        break;
                    }
                }
                i++;
                // We cap the number of steps that can be taken
                // and we check to see if the path has
                // reversed direction
            } while (nonReversed(theta2, theta1) && i < 1500);
            trace.add(new PathIterator(points.iterator()));
        }
        return trace;
    }
    
}
