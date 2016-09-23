package fieldvisualizer;

import java.util.List;

/**
 * A charge is any object that generates a field around it. The charge will
 * affect the overall field in the region. The net field at a point is the
 * vector sum of all the fields associated with each charge.
 *
 * @author Jeff Niu
 */
public interface Charge {

    /**
     * This method will return the field vector associated with this charge
     * at a particular point in space.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the vector associated with this point
     */
    public Vector fieldAt(double x, double y);

    /**
     * Calling this method will request the charge to return a set of path
     * iterators that describe the magnetic field lines around this object.
     * Not all charge objects will report a trace. An array of the charges
     * is passed directly to the charge to avoid previous issues with
     * concurrent modification in lists.
     *
     * @param angle trace projection angle (if it is used)
     * @param step trace projection step (resolution)
     * @param charges the array of charges in the field
     * @return a set of path iterators
     */
    public List<PathIterator> trace(double angle, double step,
            Charge[] charges);

}
