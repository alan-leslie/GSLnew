package gsl.ui;

import java.awt.Component;

/**
 *
 * @author alan
 */
public class ComponentPair {

    private final String labelText;
    private final Component theField;

    public ComponentPair(String label, Component field) {
        labelText = label;
        theField = field;
    }

    public String getLabel() {
        return labelText;
    }

    public Component getField() {
        return theField;
    }
}
