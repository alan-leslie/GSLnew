
package materials.ui;

import java.awt.event.*;

/**
 *
 * @author al
 */

public class AddThicknessListener implements ActionListener {
    private final MaterialDialog theUI;

    AddThicknessListener(MaterialDialog theNewUI) {
        theUI = theNewUI;
    }

    public void actionPerformed(ActionEvent e) {
        theUI.addThickness();
    }
}
