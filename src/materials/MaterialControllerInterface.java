
package materials;

import java.util.List;
import jobs.ValidationResult;
import materials.ui.MaterialDisplayInterface;

/**
 *
 * @author alan
 */
public interface MaterialControllerInterface {

    void clear();

    void deleteMaterial();

    void setUI(MaterialDisplayInterface theWindow);    

    public List<ValidationResult> validate();

    public void saveMaterial();

    public List<ValidationResult> validateDelete();
}
