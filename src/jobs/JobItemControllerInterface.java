
package jobs;

import java.util.List;
import jobs.ui.JobItemDisplayInterface;

/**
 *
 * @author alan
 */
public interface JobItemControllerInterface {

    void clear();

    List<ValidationResult> validate();
           
    List<ValidationResult> validateDelete();
        
    void deleteJob();
    
    void saveJob();

    void setUI(JobItemDisplayInterface theWindow);    
}
