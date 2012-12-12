
package materials.ui;

import java.awt.event.*;

/**
 *
 * @author al
 */
public class DeleteThicknessListener implements ActionListener {
    private final MaterialDialog theUI;

    DeleteThicknessListener(MaterialDialog theNewUI) {
        theUI = theNewUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        theUI.deleteThickness();
    }
}
