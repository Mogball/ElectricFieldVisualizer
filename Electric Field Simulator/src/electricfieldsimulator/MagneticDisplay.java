package electricfieldsimulator;

import static electricfieldsimulator.FieldSimulator.size;
import fieldvisualizer.BarMagnet;
import fieldvisualizer.Line;
import fieldvisualizer.PathIterator;
import fieldvisualizer.Vector;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.hypot;
import static java.lang.Math.sin;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 *
 * @author Tiger He, Jeff Niu
 */
public class MagneticDisplay extends JPanel implements MouseWheelListener {

    private static final int P_RAD = 15;

    private final FieldSimulator fieldSim;

    private List<Wire> wires;
    private List<BarMagnet> magnets;

    private ImageIcon inWire;
    private ImageIcon outWire;
    private ImageIcon horseMagnet;

    private JButton inWireButton;
    private JButton outWireButton;
    private JButton garbageButton;
    private JButton resetButton;
    private JButton menuButton;

    private Point mouse;

    private String held;
    private double mouseX;
    private double mouseY;

    private double magnetStartX;
    private double magnetStartY;

    /**
     * The local field object for drawing and rendering magnetic field
     * lines.
     *
     * @author Jeff Niu
     */
    private fieldvisualizer.Field magneticField;

    /**
     * Constructor for instantiating display screen
     *
     * @param triangleSimulator surrounding JFrame to hold panel
     * @author Jeff Niu
     */
    MagneticDisplay(FieldSimulator fieldSim) {
        super(); // calls JPanel constructor
        setLayout(null); // no layout manager
        setPreferredSize(fieldSim.size); // set panel to be same size as window
        this.fieldSim = fieldSim;
        init();
    }

    /**
     * Initialize display screen variables
     *
     * @author Jeff Niu
     */
    private void init() {

        setBackground(Color.white);

        this.addMouseWheelListener(this);
        
        wires = new ArrayList<>();
        magnets = new ArrayList<>();

        inWire = new ImageIcon("inWire.png");
        outWire = new ImageIcon("outWire.png");
        horseMagnet = new ImageIcon("magnetIcon.png");

        inWireButton = new JButton(inWire);
        inWireButton.setBounds(10, 30, 128, 128);
        add(inWireButton);
        inWireButton.addActionListener((inWireAdd) -> {
            held = "inWire";
        });

        outWireButton = new JButton(outWire);
        outWireButton.setBounds(10, 168, 128, 128);
        add(outWireButton);
        outWireButton.addActionListener((outWireAdd) -> {
            held = "outWire";
        });

        JButton magnetButton = new JButton(horseMagnet);
        magnetButton.setBounds(10, 306, 128, 128);
        add(magnetButton);
        magnetButton.addActionListener((evt) -> {
            held = "placeMagnet";
        });

        garbageButton = new JButton(new ImageIcon("trash.png"));
        garbageButton.setBounds(10, 444, 128, 128);
        add(garbageButton);
        garbageButton.addActionListener((dropHeld) -> {
            if (held != null) {
                held = null;
            }
        });

        menuButton = new JButton("Menu");
        menuButton.setBounds(24, 740, 100, 30);
        add(menuButton);
        menuButton.addActionListener((goMenu) -> {
            wires.clear();
            magnets.clear();
            held = null;
            fieldSim.setContentPane(fieldSim.menuDisplay);
            fieldSim.pack();
        });

        // Hitting this will clear all onscreen objects
        resetButton = new JButton("Reset");
        resetButton.setBounds(24, 700, 100, 30);
        add(resetButton);
        resetButton.addActionListener((evt) -> {
            wires.clear();
            magnets.clear();
        });

        mouse = getMousePosition();

        mouseX = 0;
        mouseY = 0;

        // create mouse listener object
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            /**
             *
             *
             * @param e mouse event
             */
            @Override
            public void mousePressed(MouseEvent e) {

                // if something is held
                if (held != null) {
                    switch (held) {
                        case "inWire": {
                            mouse = getMousePosition();
                            double x = mouse.getX();
                            double y = mouse.getY();
                            wires.add(new Wire(x, y, 1));
                            held = null;
                            break;
                        }
                        case "outWire": {
                            mouse = getMousePosition();
                            double x = mouse.getX();
                            double y = mouse.getY();
                            wires.add(new Wire(x, y, -1));
                            held = null;
                            break;
                        }
                        case "placeMagnet": {
                            mouse = getMousePosition();
                            magnetStartX = mouse.getX();
                            magnetStartY = mouse.getY();
                            held = "finishMagnet";
                            break;
                        }
                        case "finishMagnet": {
                            double mx = (mouseX + magnetStartX) / 2;
                            double my = (mouseY + magnetStartY) / 2;
                            double dx = mouseX - mx;
                            double dy = mouseY - my;
                            double l = hypot(dx, dy);
                            double a = fieldvisualizer.Vector.arg(dx, dy);
                            BarMagnet barMagnet = new BarMagnet(mx, my,
                                    100, l, a);
                            magnets.add(barMagnet);
                            held = null;
                            break;
                        }
                    }
                    if (held != null) {
                        Scanner s = new Scanner(held);
                        if (s.next().equals("magnet")) {
                            double l = s.nextDouble();
                            double arg = s.nextDouble();
                            magnets.add(new BarMagnet((int) mouseX, (int) mouseY,
                                    100, l, arg));
                            held = null;
                        }
                        s.close();
                    }
                } else {
                    // check if clicked on wire
                    double x = mouseX;
                    double y = mouseY;

                    int size = wires.size();
                    for (int i = 0; i < size; i++) {
                        if (Math.abs(x - wires.get(i).x) < P_RAD
                                && Math.abs(y - wires.get(i).y) < P_RAD) {
                            held = wires.get(i).toString(); // Jeff: fixed a bug where picking up anything will always give a proton
                            wires.remove(i);
                            return;
                        }
                    }
                    size = magnets.size();
                    for (int i = 0; i < size; i++) {
                        BarMagnet magnet = magnets.get(i);
                        int sx = (int) magnet.south.x;
                        int sy = (int) magnet.south.y;
                        int ex = (int) magnet.north.x;
                        int ey = (int) magnet.north.y;
                        Polygon[] magnetHalves = magnetToPolygon(
                                sx, sy, ex, ey);
                        if (magnetHalves[0].contains(mouse)
                                || magnetHalves[1].contains(mouse)) {
                            // Since the held variable is a string, this
                            // is going to have to be the workaround
                            double dx = ex - sx;
                            double dy = ey - sy;
                            double l = hypot(dx / 2, dy / 2);
                            double arg = Vector.arg(dx, dy);
                            held = String.format("magnet %.2f %.2f",
                                    l, arg);
                            magnets.remove(i);
                            return;
                        }
                    }
                }
            }

            /**
             * Do nothing when mouse button is released
             *
             * @param e mouse event
             */
            @Override
            public void mouseReleased(MouseEvent e) {
            }

            /**
             * Do nothing when mouse enters window
             *
             * @param e mouse event
             */
            @Override
            public void mouseEntered(MouseEvent e) {
            }

            /**
             * Do nothing when mouse exits window
             *
             * @param e mouse event
             */
            @Override
            public void mouseExited(MouseEvent e) {
            }

        });

        // Initialize the magnetic field
        magneticField = new fieldvisualizer.ElectroMagneticField();

        // We will redraw everything on a set delay
        Timer drawer = new Timer();
        drawer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                repaint();
            }

        }, 100, 100);

    }

    /**
     * Display graphics
     *
     * @param g graphics object
     */
    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        mouse = getMousePosition();

        if (mouse != null) {
            mouseX = mouse.getX();
            mouseY = mouse.getY();
        }

        // Draw the electric field lines
        drawField(g);

        drawMagnets(g);
        drawWires(g);
        drawHeld(g);

        drawText(g);
    }

    /**
     * Transfer the charges into the electric field and draw the electric
     * field lines.
     *
     * @param g
     * @author Jeff Niu
     */
    private void drawField(Graphics g) {
        // Transfer charges into the field
        magneticField.getCharges().clear();
        magneticField.getCharges().addAll(wires);
        magneticField.getCharges().addAll(magnets);
        // Add the charge held by the mouse
        if (held != null) {
            if (held.equals("inWire") || held.equals("outWire")) {
                fieldvisualizer.Charge heldCharge = new Wire(mouseX, mouseY,
                        held.equals("inWire") ? 1 : -1);
                magneticField.add(heldCharge);
            }
            // If a magnet is in the process of being drawn, render lines
            if (held.equals("finishMagnet")) {
                double mx = (mouseX + magnetStartX) / 2;
                double my = (mouseY + magnetStartY) / 2;
                double dx = mouseX - mx;
                double dy = mouseY - my;
                if (dx != 0 && dy != 0) {
                    double l = hypot(dx, dy);
                    double arg = fieldvisualizer.Vector.arg(dx, dy);
                    fieldvisualizer.Charge barMagnet = new BarMagnet(mx, my,
                            100, l, arg);
                    magneticField.add(barMagnet);
                }
            }
            // If the mouse holds a magnet, draw it
            Scanner s = new Scanner(held);
            if (s.next().equals("magnet") && mouse != null) {
                double l = s.nextDouble();
                double arg = s.nextDouble();
                magneticField.add(new BarMagnet(mouseX, mouseY, 100, l, arg));
            }
            s.close();
        }
        List<PathIterator> trace = magneticField.trace(PI / 6, 3);
        for (PathIterator path : trace) {
            while (path.hasNext()) {
                Line l = path.next();
                if (l.p.x > size.width || l.p.x < 0) {
                    continue;
                }
                if (l.p.y > size.height || l.p.y < 0) {
                    continue;
                }
                g.drawLine((int) l.p.x, (int) l.p.y, (int) l.q.x, (int) l.q.y);
            }
        }
    }

    private void drawText(Graphics g) {
        g.drawString("Holding " + held, 5, 20);
    }

    private void drawWires(Graphics g) {
        int size = wires.size();
        for (int i = 0; i < size; i++) {
            Wire wire = wires.get(i);
            int x = (int) wire.x;
            int y = (int) wire.y;
            if (wire.q > 0) {
                g.drawImage(new ImageIcon("inWire.png").getImage(), x - P_RAD,
                        y - P_RAD, P_RAD * 2, P_RAD * 2, this);
            } else if (wire.q < 0) {
                g.drawImage(new ImageIcon("outWire.png").getImage(), x - P_RAD,
                        y - P_RAD, P_RAD * 2, P_RAD * 2, this);
            }
        }
    }

    private void drawMagnets(Graphics g) {
        for (BarMagnet magnet : magnets) {
            int ex = (int) magnet.north.x;
            int ey = (int) magnet.north.y;
            int sx = (int) magnet.south.x;
            int sy = (int) magnet.south.y;
            drawMagnet(sx, sy, ex, ey, g);
        }
    }

    private void drawHeld(Graphics g) {
        if (held != null) {
            switch (held) {
                case "inWire":
                    g.drawImage(inWire.getImage(), (int) mouseX - P_RAD,
                            (int) mouseY - P_RAD, P_RAD * 2, P_RAD * 2,
                            this);
                    break;
                case "outWire":
                    g.drawImage(outWire.getImage(), (int) mouseX - P_RAD,
                            (int) mouseY - P_RAD, P_RAD * 2, P_RAD * 2,
                            this);
                    break;
                case "placeMagnet":
                    g.drawImage(horseMagnet.getImage(), (int) mouseX - P_RAD,
                            (int) mouseY - P_RAD, P_RAD * 2, P_RAD * 2,
                            this);
                    break;
                case "finishMagnet":
                    // Define manually and draw the bar magnet
                    int sx = (int) magnetStartX;
                    int sy = (int) magnetStartY;
                    int ex = (int) mouseX;
                    int ey = (int) mouseY;
                    drawMagnet(sx, sy, ex, ey, g);
            }
            Scanner s = new Scanner(held);
            if (s.next().equals("magnet")) {
                double l = s.nextDouble();
                double arg = s.nextDouble();
                double lsina = l * sin(arg);
                double lcosa = l * cos(arg);
                double ex = mouseX + lcosa;
                double ey = mouseY + lsina;
                double sx = mouseX - lcosa;
                double sy = mouseY - lsina;
                drawMagnet((int) sx, (int) sy, (int) ex, (int) ey, g);
            }
            s.close();
        }
    }

    private void drawMagnet(int sx, int sy, int ex, int ey, Graphics g) {
        Polygon[] magnetHalves = magnetToPolygon(sx, sy, ex, ey);
        g.setColor(Color.blue);
        g.fillPolygon(magnetHalves[0]);
        g.setColor(Color.red);
        g.fillPolygon(magnetHalves[1]);
    }

    private Polygon[] magnetToPolygon(int sx, int sy, int ex, int ey) {
        int mx = (ex + sx) / 2;
        int my = (ey + sy) / 2;
        int dx = ex - sx;
        int dy = sy - ey;
        double arg = Vector.arg(dx, dy);
        int[] X = new int[4];
        int[] Y = new int[4];
        int cosArgPlus = (int) (20 * cos(arg + PI / 2));
        int sinArgPlus = (int) (20 * sin(arg + PI / 2));
        int cosArgMinus = (int) (20 * cos(arg - PI / 2));
        int sinArgMinus = (int) (20 * sin(arg - PI / 2));
        X[0] = sx + cosArgPlus;
        Y[0] = sy - sinArgPlus;
        X[1] = sx + cosArgMinus;
        Y[1] = sy - sinArgMinus;
        X[3] = mx + cosArgPlus;
        Y[3] = my - sinArgPlus;
        X[2] = mx + cosArgMinus;
        Y[2] = my - sinArgMinus;
        Polygon[] magnetHalves = new Polygon[2];
        magnetHalves[0] = new Polygon(X, Y, 4);
        X[0] = X[2];
        Y[0] = Y[2];
        X[1] = X[3];
        Y[1] = Y[3];
        X[2] = ex + cosArgPlus;
        Y[2] = ey - sinArgPlus;
        X[3] = ex + cosArgMinus;
        Y[3] = ey - sinArgMinus;
        magnetHalves[1] = new Polygon(X, Y, 4);
        return magnetHalves;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe) {
        if (held != null) {
            Scanner s = new Scanner(held);
            if (s.next().equals("magnet")) {
                double l = s.nextDouble();
                double arg = s.nextDouble();
                arg += mwe.getWheelRotation() * 0.3;
                held = String.format("magnet %.2f %.2f", l, arg);
            }
            s.close();
        }
    }

}
