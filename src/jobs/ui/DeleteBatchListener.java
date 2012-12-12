
package jobs.ui;

import java.awt.event.*;

/**
 *
 * @author al
 */
public class DeleteBatchListener implements ActionListener {
    private final JobDialog theUI;

    DeleteBatchListener(JobDialog theNewUI) {
        theUI = theNewUI;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        theUI.deleteBatch();
    }
}
