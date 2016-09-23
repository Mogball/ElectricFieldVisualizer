package fieldvisualizer;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import static java.lang.Math.PI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This is a test class for the field visualizer underlying mechanisms: the
 * field vector calculations and field line projection.
 *
 * @author Jeff Niu
 */
@SuppressWarnings("serial")
public class FieldVisualizer extends JPanel {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new FieldVisualizer();
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private static void print(Object s) {
        System.out.println(s);
    }

    /**
     * The field.
     */
    private final Field field;

    /**
     * Create a new test area.
     */
    public FieldVisualizer() {
        init();
        field = new ElectroMagneticField();
    }

    /**
     * Initialize the display.
     */
    private void init() {
        Dimension dim = new Dimension(1600, 900);
        JFrame frame = new JFrame("Field Visualizer");

        setLayout(null);
        setPreferredSize(dim);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(dim);
        frame.setLayout(null);
        frame.setContentPane(this);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);

        /**
         * As part of the test, re-render the panel at a set interval and
         * use the mouse to move a charge around.
         */
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                field.getCharges().clear();
                Point mouse = FieldVisualizer.this.getMousePosition();
                if (mouse != null) {
                    field.add(new PointCharge(500, 450, 0.01));
                    field.add(new PointCharge(1100, 450, 0.01));
                    field.add(new BarMagnet(mouse.x, mouse.y, 1, 100, 0));
                    repaint();
                }
            }

        }, 100, 100);
    }

    /**
     * Paint the field lines.
     *
     * @param g
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (field != null) {
            List<PathIterator> trace = field.trace(PI / 6, 3);
            for (PathIterator path : trace) {
                while (path.hasNext()) {
                    Line l = path.next();
                    g.drawLine((int) l.p.x, (int) l.p.y, (int) l.q.x, (int) l.q.y);
                }
            }
        }
    }

}
