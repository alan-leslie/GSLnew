
package jobs.ui;

import java.awt.event.*;

/**
 *
 * @author al
 */

public class AddBatchListener implements ActionListener {
    private final JobDialog theUI;

    AddBatchListener(JobDialog theNewUI) {
        theUI = theNewUI;
    }    

    public void actionPerformed(ActionEvent e) {
        theUI.addBatch();
    }
}
