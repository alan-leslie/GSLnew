package companies;

import companies.ui.CompaniesTableModel;
import companies.ui.CompanyDisplayInterface; 
import data.Address;
import data.City;
import data.Contact;
import data.Country;
import data.CustomerCompany;
import gsl.DBHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jobs.ValidationResult;

public class CompanyController implements CompanyControllerInterface {

    private final CustomerCompany theCompany;
    private CompanyDisplayInterface theUI;
    private DBHelper theHelper;
    private CompaniesTableModel theTableModel = null;
    private boolean isCreate = false;

    public CompanyController(CustomerCompany theCompany) {
        if (theCompany == null) {
            this.theCompany = createNewCompany();
            isCreate = true;
        } else {
            this.theCompany = theCompany;
            isCreate = false;
        }
    }

    private CustomerCompany createNewCompany() {
        Date dateNow = new Date();
        String userId = "alan";
        CustomerCompany newCompany = new CustomerCompany();
        newCompany.setCreateDate(dateNow);
        newCompany.setUserId(userId);
        newCompany.setLastUpdate(dateNow);
        Address newAddress = new Address();
        newAddress.setCreateDate(dateNow);
        newAddress.setUserId(userId);
        newAddress.setLastUpdate(dateNow);
        City newCity = new City();
        newCity.setCreateDate(dateNow);
        newCity.setUserId(userId);
        newCity.setLastUpdate(dateNow);
        Country newCountry = new Country();
        newCountry.setCreateDate(dateNow);
        newCountry.setUserId(userId);
        newCountry.setLastUpdate(dateNow);
        Contact newContact = new Contact();
        newContact.setCreateDate(dateNow);
        newContact.setUserId(userId);
        newContact.setLastUpdate(dateNow);

        newCompany.setAddress(newAddress);
        newCompany.setContact(newContact);
        newAddress.setCity(newCity);
        newCity.setCountry(newCountry);

        return newCompany;
    }
    
    @Override
    public List<ValidationResult> validate() {
        if(isCreate){
            return validateCreate();
        } else {
            return validateUpdate();            
        }
    }

    private List<ValidationResult> validateCreate() {
        List<ValidationResult> theMessages = new ArrayList<>();
        CustomerCompany updatedJob = theUI.getCompany();

        if (updatedJob.getCompanyName().equals("")) {
            ValidationResult theErr = new ValidationResult(ValidationResult.type.ERROR,
                    "Please enter job name.");
            theMessages.add(theErr);
        }

        return theMessages;
    }

    private List<ValidationResult> validateUpdate() {
        List<ValidationResult> theMessages = new ArrayList<>();
        CustomerCompany updatedJob = theUI.getCompany();
        
        if (updatedJob.getCompanyName().equals("")) {
            ValidationResult theErr = new ValidationResult(ValidationResult.type.ERROR,
                    "Please enter job name.");
            theMessages.add(theErr);
        }
        
        return theMessages;
    }

    @Override
    public List<ValidationResult> validateDelete() {
        List<ValidationResult> theMessages = new ArrayList<>();
        CustomerCompany updatedJob = theUI.getCompany();
        return theMessages;
    }

    @Override
    public void saveCompany() {
        if(isCreate){
            createCompany();
        } else {
            updateCompany();
        }
    }
    
    private void updateCompany() {
        CustomerCompany updatedCompany = theUI.getCompany();
        theHelper.saveCompany(updatedCompany);
        theTableModel.companyStateChanged(updatedCompany);
    }

    private void createCompany() {
        CustomerCompany updatedCompany = theUI.getCompany();
        theHelper.saveCompany(updatedCompany);
        theTableModel.addCompany(theCompany);
    }

    @Override
    public void deleteCompany() {
        CustomerCompany uiCompany = theUI.getCompany();
        uiCompany.setActive(false);
        theHelper.saveCompany(uiCompany);
        theTableModel.deleteCompany(uiCompany);
    }

    // TODO - maybe a revert/undo though
    @Override
    public void clear() {
        theUI.clear();
    }

    @Override
    public void setUI(CompanyDisplayInterface theWindow) {
        this.theUI = theWindow;
    }

    public void setParentTableModel(CompaniesTableModel newModel, int curentRow) {
        theTableModel = newModel;
    }

    public CustomerCompany getCompany() {
        return theCompany;
    }

    public void setSessionHelper(DBHelper theData) {
        theHelper = theData;
    }
}