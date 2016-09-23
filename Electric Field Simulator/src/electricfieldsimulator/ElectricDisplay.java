package electricfieldsimulator;

import static electricfieldsimulator.FieldSimulator.size;
import fieldvisualizer.Line;
import fieldvisualizer.PathIterator;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import static java.lang.Math.PI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author Jeff Niu, Tiger He
 */
public class ElectricDisplay extends JPanel {

    private static final int P_RAD = 15;

    private final FieldSimulator fieldSim;

    private List<PointCharge> charges;

    private ImageIcon proton;
    private ImageIcon electron;

    private JButton protonButton;
    private JButton electronButton;
    private JButton garbageButton;
    private JButton resetButton;
    private JButton menuButton;

    private Point mouse;
    private String held;

    private int mouseX;
    private int mouseY;

    /**
     * The local field object for drawing and rendering electric field
     * lines.
     *
     * @author Jeff Niu
     */
    private fieldvisualizer.Field electricField;

    /**
     * Constructor for instantiating display screen
     *
     * @param triangleSimulator surrounding JFrame to hold panel
     */
    ElectricDisplay(FieldSimulator eFieldSim) {
        super(); // calls JPanel constructor
        setLayout(null); // no layout manager
        setPreferredSize(FieldSimulator.size); // set panel to be same size as window
        this.fieldSim = eFieldSim;
        init();
    }

    /**
     * Initialize display screen variables
     */
    private void init() {

        setBackground(Color.white);

        charges = new ArrayList<>();

        proton = new ImageIcon("proton.png");
        electron = new ImageIcon("electron.png");

        protonButton = new JButton(proton);
        protonButton.setBounds(10, 30, 128, 128);
        add(protonButton);
        protonButton.addActionListener((protonAdd) -> {
            if (held == null) {
                held = "proton";
            }
        });

        electronButton = new JButton(electron);
        electronButton.setBounds(10, 168, 128, 128);
        add(electronButton);
        electronButton.addActionListener((electronAdd) -> {
            if (held == null) {
                held = "electron";
            }
        });

        menuButton = new JButton("Menu");
        menuButton.setBounds(24, 740, 100, 30);
        add(menuButton);
        menuButton.addActionListener((goMenu) -> {
            held = null;
            charges.clear();
            fieldSim.setContentPane(fieldSim.menuDisplay);
            fieldSim.pack();

            // Clear the field
            electricField.getCharges().clear();
        });

        garbageButton = new JButton(new ImageIcon("trash.png"));
        garbageButton.setBounds(10, 306, 128, 128);
        add(garbageButton);
        garbageButton.addActionListener((dropItem) -> {
            if (held != null) {
                held = null;
            }
        });

        // Hitting this will clear all onscreen objects
        resetButton = new JButton("Reset");
        resetButton.setBounds(24, 700, 100, 30);
        add(resetButton);
        resetButton.addActionListener((reset) -> {
            charges.clear();
            held = null;

            // Clear the field
            electricField.getCharges().clear();
        });

        mouse = getMousePosition();

        mouseX = 0;
        mouseY = 0;

        // create mouse listener object
        addMouseListener(new MouseListener() {
            /**
             * Do nothing when mouse clicked
             *
             * @param e mouse event
             */
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

                    // We create either a negative or positive point
                    // charge based on what is currently held
                    mouse = getMousePosition();
                    double x = mouse.getX();
                    double y = mouse.getY();
                    double charge = held.equals("proton") ? 1 : -1;
                    charges.add(new PointCharge(x, y, charge));
                    held = null;

                } else {
                    // check if clicked on point q
                    if (mouse != null) {
                        int x = mouse.x;
                        int y = mouse.y;

                        int size = charges.size();
                        for (int i = 0; i < size; i++) {
                            if (Math.abs(x - charges.get(i).x) < P_RAD
                                    && Math.abs(y - charges.get(i).y) < P_RAD) {
                                held = charges.get(i).toString();
                                charges.remove(i);
                                break;
                            }
                        }
                    }

                }

            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        // Initialize the field object
        electricField = new fieldvisualizer.ElectroMagneticField();

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

        // get mouse coordinates
        mouse = getMousePosition();
        if (mouse != null) {
            mouseX = (int) mouse.getX();
            mouseY = (int) mouse.getY();
        }

        // Draw the electric field lines
        drawField(g);

        drawCharges(g);
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
        electricField.getCharges().clear();
        electricField.getCharges().addAll(charges);
        // Add the charge held by the mouse
        if (held != null) {
            fieldvisualizer.Charge heldCharge = new PointCharge(mouseX,
                    mouseY, held.equals("proton") ? 1 : -1);
            electricField.add(heldCharge);
        }
        List<PathIterator> trace = electricField.trace(PI / 6, 3);
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

    private void drawCharges(Graphics g) {
        int size = charges.size();
        for (int i = 0; i < size; i++) {
            PointCharge charge = charges.get(i);
            int x = (int) charge.x;
            int y = (int) charge.y;
            if (charge.q > 0) {
                g.drawImage(new ImageIcon("proton.png").getImage(), x - P_RAD,
                        y - P_RAD, P_RAD * 2, P_RAD * 2, this);
            } else if (charge.q < 0) {
                g.drawImage(new ImageIcon("electron.png").getImage(), x - P_RAD,
                        y - P_RAD, P_RAD * 2, P_RAD * 2, this);
            }
        }
    }

    private void drawText(Graphics g) {
        g.drawString("Holding " + held, 5, 20);
    }

    private void drawHeld(Graphics g) {
        if (held != null) {
            switch (held) {
                case "proton":
                    g.drawImage(proton.getImage(), mouseX - P_RAD,
                            mouseY - P_RAD, P_RAD * 2, P_RAD * 2, this);
                    break;
                case "electron":
                    g.drawImage(electron.getImage(), mouseX - P_RAD,
                            mouseY - P_RAD, P_RAD * 2, P_RAD * 2, this);
                    break;
            }
        }
    }

}
