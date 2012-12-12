
package materials.ui;

import materials.MaterialControllerInterface;
import java.awt.event.*;

/**
 *
 * @author al
 */
public class CloseListener implements ActionListener {
    private final MaterialControllerInterface theController;

    CloseListener(MaterialControllerInterface theNewController) {
        theController = theNewController;
    }

    public void actionPerformed(ActionEvent e) {
        theController.clear();
    }
}
