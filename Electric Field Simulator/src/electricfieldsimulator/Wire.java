package electricfieldsimulator;

/**
 *
 * @author Tiger He
 */
public class Wire extends fieldvisualizer.Wire {

    /**
     * @author Tiger He
     */
    Wire(double x, double y, double q) {
        super(x, y, q);
    }

    /**
     * @author Tiger He
     */
    @Override
    public String toString() {
        if (q < 0) {
            return "outWire";
        } else if (q > 0) {
            return "inWire";
        } else {
            return "null";
        }
    }
}
