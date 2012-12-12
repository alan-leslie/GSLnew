package materials;

import data.Material;
import gsl.DBHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jobs.ValidationResult;
import materials.ui.MaterialDisplayInterface;
import materials.ui.MaterialsTableModel;

public class MaterialController implements MaterialControllerInterface {
    Material theMaterial;
    MaterialDisplayInterface theUI;
    private MaterialsTableModel theTableModel = null;
    private DBHelper theHelper;
    private boolean isCreate = false;

    public MaterialController(Material theMaterial) {
        if (theMaterial == null) {
            this.theMaterial = createNewMaterial();
            isCreate = true;
        } else {
            this.theMaterial = theMaterial;
            isCreate = false;
        }   
    }
    
    private Material createNewMaterial() {
        Date dateNow = new Date();
        String userId = "alan";
        Material newMaterial = new Material();
        newMaterial.setCreateDate(dateNow);
        newMaterial.setUserId(userId);
        newMaterial.setLastUpdate(dateNow);

        return newMaterial;
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
        Material updatedMaterial = theUI.getMaterial();

        if (updatedMaterial.getDescription().equals("")) {
            ValidationResult theErr = new ValidationResult(ValidationResult.type.ERROR,
                    "Please enter job name.");
            theMessages.add(theErr);
        }

        return theMessages;
    }

    private List<ValidationResult> validateUpdate() {
        List<ValidationResult> theMessages = new ArrayList<>();
        Material updatedMaterial = theUI.getMaterial();
        
        if (updatedMaterial.getDescription().equals("")) {
            ValidationResult theErr = new ValidationResult(ValidationResult.type.ERROR,
                    "Please enter job name.");
            theMessages.add(theErr);
        }
        
        return theMessages;
    }

    @Override
    public List<ValidationResult> validateDelete() {
        List<ValidationResult> theMessages = new ArrayList<>();
        Material updatedMaterial = theUI.getMaterial();
        return theMessages;
    }

    @Override
    public void saveMaterial() {
        if(isCreate){
            createMaterial();
        } else {
            updateMaterial();
        }
    }
    
    private void updateMaterial() {
        Material updatedMaterial = theUI.getMaterial();
        theHelper.saveMaterial(updatedMaterial);
        theTableModel.materialStateChanged(updatedMaterial);
    }

    private void createMaterial() {
        Material updatedMaterial = theUI.getMaterial();
        theHelper.saveMaterial(updatedMaterial);
        theTableModel.addMaterial(updatedMaterial);
    }

    @Override
    public void deleteMaterial() {
        theMaterial.setActive(false);
        theHelper.saveMaterial(theMaterial);
        theTableModel.deleteMaterial(theMaterial);
    }

    @Override
    public void clear() {
         theUI.clear();
     }

    @Override
    public void setUI(MaterialDisplayInterface theWindow) {
        this.theUI = theWindow;
    }

    public void setParentTableModel(MaterialsTableModel newModel, int i) {
        theTableModel = newModel;
    }

    public void setSessionHelper(DBHelper theData) {
        theHelper = theData;
    }
    
    public Material getMaterial() {
        return theMaterial;
    }
}