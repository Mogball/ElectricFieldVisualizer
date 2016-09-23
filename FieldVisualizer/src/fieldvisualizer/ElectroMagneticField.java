package fieldvisualizer;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an implementation of the field class and is able to handle
 * charges. Now that calculating field vectors has been made a static task,
 * this class is essentially one made to store the charges in a field.
 *
 * @author Jeff Niu
 */
public class ElectroMagneticField implements Field {
    
    /**
     * The list of charges in this field.
     */
    private final List<Charge> charges;

    /**
     * Create a new field.
     */
    public ElectroMagneticField() {
        charges = new ArrayList<>();
    }

    /**
     * Add a charge to this field.
     * 
     * @param charge the charge to add
     */
    @Override
    public void add(Charge charge) {
        charges.add(charge);
    }

    /**
     * @return the list of charges in this field
     */
    @Override
    public List<Charge> getCharges() {
        return charges;
    }

}
