
package companies;

import companies.ui.CompanyDisplayInterface;
import java.util.List;
import jobs.ValidationResult;

/**
 *
 * @author alan
 */
public interface CompanyControllerInterface {

    void clear();

    void deleteCompany();

    void setUI(CompanyDisplayInterface theWindow);    

    public List<ValidationResult> validate();

    public void saveCompany();

    public List<ValidationResult> validateDelete();
}
