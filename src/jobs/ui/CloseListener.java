
package jobs.ui;

import jobs.JobItemControllerInterface;
import java.awt.event.*;

/**
 *
 * @author al
 */
public class CloseListener implements ActionListener {
    private final JobItemControllerInterface theController;

    CloseListener(JobItemControllerInterface theNewController) {
        theController = theNewController;
    }

    public void actionPerformed(ActionEvent e) {
        theController.clear();
    }
}
