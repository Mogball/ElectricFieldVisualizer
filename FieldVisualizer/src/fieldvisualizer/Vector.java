package fieldvisualizer;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;
import static java.lang.Math.hypot;

/**
 * A vector is a quantity with both direction and magnitude. This is a
 * simple vector that is represented by its x and y components. Moreover,
 * this simple vector can be used to represent a coordinate point.
 *
 * @author Jeff Niu
 */
public class Vector {

    /**
     * This method was created in response to a limitation of the arctan
     * function. The vector argument function will always return an angle
     * value between 0 and 2pi. As such, when an angle rotates across the
     * zero mark, there is a huge jump in angle different values. This
     * method will operate on some assumptions to hopefully determine the
     * true difference between two angles. The first assumption is that
     * true difference between the two angles is rather small. The second
     * assumption is that the angles do indeed fall between 0 and 2pi.
     * Finally, we assume that any issues with the angle difference
     * calculation is because each angle is on each side of the zero mark.
     *
     * @param theta1 the first angle
     * @param theta2 the second angle
     * @return the true difference between the two angles
     */
    public static double angleDiff(double theta1, double theta2) {
        double diff = theta2 - theta1;
        if (abs(diff) > PI / 2) {
            if (theta2 > theta1) {
                theta1 += PI / 2;
            } else {
                theta2 += PI / 2;
            }
            diff = theta2 - theta1;
        }
        return diff;
    }

    /**
     * Add two vectors.
     *
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the sum of the two vectors
     */
    public static Vector sum(Vector v1, Vector v2) {
        return new Vector(v1.x + v2.x, v1.y + v2.y);
    }

    /**
     * The argument of a vector is the angle between the vector and the x
     * axis. The argument is between 0 and 2 pi. The argument when both x
     * and y are zero is undefined.
     *
     * @param x the x component
     * @param y the y component
     * @return
     */
    public static double arg(double x, double y) {
        if (x == 0) {
            if (y > 0) {
                return PI / 2;
            } else if (y < 0) {
                return 3 * PI / 2;
            } else {
                return Double.NaN;
            }
        }
        if (y == 0) {
            if (x > 0) {
                return 0;
            } else if (x < 0) {
                return PI;
            } else {
                return Double.NaN;
            }
        }
        double arg = atan(y / x);
        if (x < 0) {
            arg += PI;
        }
        arg += 2 * PI;
        arg %= 2 * PI;
        return arg;
    }

    /**
     * The x component.
     */
    public final double x;
    /**
     * The y component.
     */
    public final double y;

    /**
     * Create an empty vector.
     */
    public Vector() {
        this(0, 0);
    }

    /**
     * Create a vector.
     *
     * @param x the x component
     * @param y the y component
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Add another vector to this vector.
     *
     * @param v the other vector
     * @return the sum
     */
    public Vector add(Vector v) {
        return sum(this, v);
    }

    /**
     * @return the magnitude of this vector
     */
    public double mag() {
        return hypot(x, y);
    }

    /**
     * @return the argument of this vector
     */
    public double arg() {
        return arg(x, y);
    }

    /**
     * @return a string representation of this vector
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Vector: ");
        sb.append(String.format("x = %.2f, ", x));
        sb.append(String.format("y = %.2f, ", y));
        sb.append(String.format("arg = %.2f%n", arg() / PI * 180));
        return sb.toString();
    }

}
