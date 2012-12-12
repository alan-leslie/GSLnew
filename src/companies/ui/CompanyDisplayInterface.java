
package companies.ui;

import data.CustomerCompany;

/**
 *
 * @author alan
 */
public interface CompanyDisplayInterface {

    void clear();

    CustomerCompany getCompany();

    void reportRrror(String theError);

    void setCompany(CustomerCompany company);

    public void disableSave();

    public void enableSave();

    public void disableDelete();

    public void enableDelete();
}
