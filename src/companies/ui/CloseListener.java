
package companies.ui;

import companies.CompanyControllerInterface;
import java.awt.event.*;

/**
 *
 * @author al
 */
public class CloseListener implements ActionListener {
    private final CompanyControllerInterface theController;

    CloseListener(CompanyControllerInterface theNewController) {
        theController = theNewController;
    }

    public void actionPerformed(ActionEvent e) {
        theController.clear();
    }
}
