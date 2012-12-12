
package jobs.ui;

import data.JobItem;
import data.Material;

/**
 *
 * @author alan
 */
public interface JobItemDisplayInterface {

    void clear();

    JobItem getJob();

    void reportRrror(String theError);

    void setJob(JobItem job);

    public void disableSave();

    public void enableSave();

    public void disableDelete();

    public void enableDelete();
}
