package jobs.ui;

import java.awt.event.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingWorker;
import jobs.JobItemControllerInterface;
import jobs.ValidationResult;

/**
 *
 * @author al
 */
public class SaveListener implements ActionListener {

    private final JobItemControllerInterface theController;
    private final JobItemDisplayInterface theUI;
    private boolean jobHasError;

    SaveListener(JobItemControllerInterface theNewController,
            JobItemDisplayInterface theUI) {
        theController = theNewController;
        this.theUI = theUI;
        jobHasError = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        validate();

        if (!hasError()) {
            save();
        }
    }

    private void validate() {
        theUI.disableSave();
        SwingWorker worker = new SwingWorker<List<ValidationResult>, Void>() {
            @Override
            public List<ValidationResult> doInBackground() {
                List<ValidationResult> items = theController.validate();

                return items;
            }

            @Override
            public void done() {
                try {
                    List<ValidationResult> items = get();

                    if (!items.isEmpty()) {
                        ValidationResult theFirstResult = items.get(0);
                        theUI.reportRrror(theFirstResult.getMessage());
                        theUI.enableSave();
                        jobHasError = true;
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(SaveListener.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(SaveListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        worker.execute();
    }

    private void save() {
        theUI.disableSave();
        SwingWorker worker = new SwingWorker<String, Void>() {
            @Override
            public String doInBackground() {
                theController.saveJob();
                return "";
            }

            @Override
            public void done() {
                theUI.clear();
            }
        };

        worker.execute();
    }

    private boolean hasError() {
        return jobHasError;
    }
}
