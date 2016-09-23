package electricfieldsimulator;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Tiger He
 */
public class MenuDisplay extends JPanel {

    /**
     * @author Tiger He
     */
    private FieldSimulator fieldSim;
    /**
     * @author Tiger He
     */
    JButton electricButton;
    /**
     * @author Tiger He
     */
    JButton magneticButton;
    /**
     * @author Tiger He
     */
    Font titleFont;

    /**
     * Create menu screen
     *
     * @param triangleSimulator surrounding JFrame to hold panel
     * @author Tiger He
     */
    MenuDisplay(FieldSimulator eFieldSim) {
        super(); // calls JPanel constructor
        setLayout(null); // no layout manager
        setPreferredSize(eFieldSim.size); // set panel to be same size as window
        this.fieldSim = eFieldSim;
        init();
    }

    /**
     * Initialize display screen variables
     * @author Tiger He
     */
    private void init() {

        setBackground(Color.white);

        titleFont = new Font("Garamond", Font.PLAIN, 64);
        JLabel title = new JLabel("Field Simulator");
        title.setBounds(600, 100, 500, 50);
        title.setFont(titleFont);
        add(title);
        Font bFont = new Font("Garamond", Font.PLAIN, 20);

        // switch to simulate electric field
        electricButton = new JButton("Electric Field");
        electricButton.setBounds(650, 200, 300, 40);
        electricButton.setFont(bFont);
        add(electricButton);
        electricButton.addActionListener((evt) -> {
            fieldSim.setContentPane(fieldSim.electricDisplay);
            fieldSim.pack();
        });

        // switch to simulate magnetic field
        magneticButton = new JButton("Magnetic Field");
        magneticButton.setBounds(650, 250, 300, 40);
        magneticButton.setFont(bFont);
        add(magneticButton);
        magneticButton.addActionListener((evt) -> {
            fieldSim.setContentPane(fieldSim.magneticDisplay);
            fieldSim.pack();
        });

        JButton quitButton = new JButton("Exit");
        quitButton.setFont(bFont);
        quitButton.setBounds(650, 300, 300, 40);
        add(quitButton);
        quitButton.addActionListener((evt) -> Runtime.getRuntime().exit(0));

    }

}
