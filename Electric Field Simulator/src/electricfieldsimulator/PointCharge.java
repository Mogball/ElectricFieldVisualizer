package electricfieldsimulator;

/**
 *
 * @author Tiger He
 */
public class PointCharge extends fieldvisualizer.PointCharge {

    PointCharge(double x, double y, double q) {
        super(x, y, q);
    }

    /**
     * Jeff: as I understand, in your system, the object "held" is a
     * string, which is either "electron" or "proton". This toString()
     * method is a sort of fix to the bug where picking up anything from
     * the field will always give a proton.
     *
     * @return
     */
    @Override
    public String toString() {
        if (q < 0) {
            return "electron";
        } else if (q > 0) {
            return "proton";
        } else {
            return "null";
        }
    }
}
