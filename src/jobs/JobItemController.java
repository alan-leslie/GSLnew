package jobs;

import gsl.DBHelper;
import data.Address;
import data.BatchSheet;
import data.City;
import data.Client;
import data.Country;
import data.JobItem;
import data.MaterialBatch;
import data.StockItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import jobs.ui.JobItemDisplayInterface;
import jobs.ui.JobTableModel;

public class JobItemController implements JobItemControllerInterface {

    JobItem theJob;
    JobItemDisplayInterface theUI;
    private final DBHelper theHelper;
    private JobTableModel theTableModel = null;
    private final boolean isCreate;

    public JobItemController(DBHelper theHelper, JobItem theJob) {
        this.theHelper = theHelper;

        if (theJob == null) {
            isCreate = true;
            this.theJob = createNewJob();
        } else {
            isCreate = false;
            this.theJob = theJob;
        }
    }

    private JobItem createNewJob() {
        Date dateNow = new Date();
        String userId = "alan";

        JobItem newJob = new JobItem();
        newJob.setCreateDate(dateNow);
        newJob.setUserId(userId);
        newJob.setLastUpdate(dateNow);
        newJob.setJobName(getNextJobName());

        Client client = new Client();
        client.setCreateDate(dateNow);
        client.setUserId(userId);
        client.setLastUpdate(dateNow);

        Address clientAddress = new Address();
        clientAddress.setCreateDate(dateNow);
        clientAddress.setUserId(userId);
        clientAddress.setLastUpdate(dateNow);

        City clientCity = new City();
        clientCity.setCreateDate(dateNow);
        clientCity.setUserId(userId);
        clientCity.setLastUpdate(dateNow);

        Country clientCountry = new Country();
        clientCountry.setCreateDate(dateNow);
        clientCountry.setUserId(userId);
        clientCountry.setLastUpdate(dateNow);

        clientCity.setCountry(clientCountry);
        clientAddress.setCity(clientCity);
        client.setAddress(clientAddress);
        newJob.setClient(client);

        StockItem stockItem = new StockItem();
        stockItem.setCreateDate(dateNow);
        stockItem.setUserId(userId);
        stockItem.setLastUpdate(dateNow);

        newJob.setStockItem(stockItem);

        return newJob;
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
        JobItem updatedJob = theUI.getJob();

        if (updatedJob.getJobName().equals("")) {
            ValidationResult theErr = new ValidationResult(ValidationResult.type.ERROR,
                    "Please enter job name.");
            theMessages.add(theErr);
        }

        return theMessages;
    }

    private List<ValidationResult> validateUpdate() {
        List<ValidationResult> theMessages = new ArrayList<>();
        JobItem updatedJob = theUI.getJob();

        if (updatedJob.getJobName().equals("")) {
            ValidationResult theErr = new ValidationResult(ValidationResult.type.ERROR,
                    "Please enter job name.");
            theMessages.add(theErr);
        }
        
        return theMessages;
    }

    @Override
    public List<ValidationResult> validateDelete() {
        List<ValidationResult> theMessages = new ArrayList<>();
        JobItem updatedJob = theUI.getJob();
        return theMessages;
    }

    @Override
    public void saveJob() {
        if(isCreate){
            createJob();
        } else {
            updateJob();
        }
    }
    
    private void updateJob() {
        JobItem updatedJob = theUI.getJob();
        theHelper.saveItem(updatedJob);
        theTableModel.jobStateChanged(updatedJob);
    }

    private void createJob() {
        JobItem updatedJob = theUI.getJob();
        theHelper.saveItem(updatedJob);
        theTableModel.addJob(updatedJob);
    }

    @Override
    public void deleteJob() {
        JobItem uiJob = theUI.getJob();
        uiJob.setActive(false);
        uiJob.getStockItem().setActive(false);
        Iterator theBatchIterator = uiJob.getStockItem().getMaterialBatchs().iterator();

        while (theBatchIterator.hasNext()) {
            MaterialBatch theBatch = (MaterialBatch) theBatchIterator.next();
            Iterator theSheetIterator = theBatch.getBatchSheets().iterator();
            theBatch.setActive(false);

            while (theSheetIterator.hasNext()) {
                BatchSheet theSheet = (BatchSheet) theSheetIterator.next();
                theSheet.setActive(false);
            }
        }

        theHelper.saveItem(uiJob);
        theTableModel.deleteJob(uiJob);
    }

    // TODO - maybe a revert/undo though
    @Override
    public void clear() {
        theUI.clear();
    }

    @Override
    public void setUI(JobItemDisplayInterface theWindow) {
        this.theUI = theWindow;
    }

    public void setParentTableModel(JobTableModel newModel, int curentRow) {
        theTableModel = newModel;
    }

    public JobItem getJob() {
        return theJob;
    }

    private String getNextJobName() {
        return theHelper.getNextJobName();
    }
}