package fieldvisualizer;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;

/**
 * A field can be either a magnetic or electric field. A field describes a
 * set of vectors associated with every point in space, depending on what
 * objects may be found in the field that affect the field vectors.
 *
 * @author Jeff Niu
 */
public interface Field {

    /**
     * Every field has a field vector associated with every point (x,y) in
     * space. This method will return that vector. This method will be used
     * to draw the field by getting the vector at a point and then moving
     * in the direction of that vector by a small increment. There may be
     * some errors for calculations around singularities, regions of
     * infinite or zero field, due to floating point error. However, such
     * regions will generally be avoided. This is now a static helper
     * method for calculating the field at a particular point since
     * iterating through the list of charges while tracing each charge
     * causes a concurrent modification exception.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the vector associated with these coordinates
     */
    public static Vector fieldAt(double x, double y, Charge[] charges) {
        double xField = 0;
        double yField = 0;
        for (Charge charge : charges) {
            Vector field = charge.fieldAt(x, y);
            xField += field.x;
            yField += field.y;
        }
        return new Vector(xField, yField);
    }

    /**
     * This method will add another charge object to the field.
     *
     * @param charge the charge to add
     */
    public void add(Charge charge);

    /**
     * Get the list of all charges that are in this field.
     *
     * @return the charges
     */
    public List<Charge> getCharges();

    /**
     * This method will return a list of path iterators that describe
     * geometrically the field lines, which can be used to draw the field
     * lines for visualization.
     *
     * @param angle the projection angle
     * @param step the resolution step
     * @return the list of field lines
     */
    public default List<PathIterator> trace(double angle, double step) {
        List<PathIterator> trace = new ArrayList<>();
        List<Charge> list = getCharges();
        Charge[] charges = list.toArray(new Charge[list.size()]);
        for (Charge charge : charges) {
            List<PathIterator> chargeTrace
                    = charge.trace(angle, step, charges);
            if (chargeTrace != null) {
                trace.addAll(chargeTrace);
            }
        }
        return trace;
    }

    /**
     * This helper method will determine whether a field line has crossed a
     * charge, in which point the trace stops for that path. Crossing a
     * charge causes a drastic change in direction. This method is
     * available for use by other classes.
     *
     * @param theta1 the new angle
     * @param theta2 the old angle
     * @return true if a charge has been crossed
     */
    static boolean nonReversed(double theta1, double theta2) {
        double delta = abs(theta1 - theta2);
        return PI / 2 > delta || delta > 3 * PI / 2;
    }

}
