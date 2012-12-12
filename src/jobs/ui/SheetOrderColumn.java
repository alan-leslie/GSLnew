package jobs.ui;

import data.MaterialBatch;
import data.MaterialSheet;

/**
 *
 * @author alan
 */
public enum SheetOrderColumn {

    TYPE("Material Type") {
        @Override
        public Object valueIn(MaterialBatch theBatch) {
            MaterialSheet theSheet = theBatch.getMaterialSheet();
            String theType = "";

            if (theSheet != null) {
                theType = theSheet.getMaterial().getMaterialType();
            }
            
            return theType;
        }
    },
    DESCRIPTION("Material Name") {
        @Override
        public Object valueIn(MaterialBatch theBatch) {
            MaterialSheet theSheet = theBatch.getMaterialSheet();
            String theDescription = "";

            if (theSheet != null) {
                theDescription = theSheet.getMaterial().getDescription();
            }
            return theDescription;
        }
    },
    THICKNESS("Thickness") {
        @Override
        public Object valueIn(MaterialBatch theBatch) {
            MaterialSheet theSheet = theBatch.getMaterialSheet();
            String theThickness = "";

            if (theSheet != null) {
                theThickness = theSheet.getThickness();
            }
            return theThickness;
        }
    },
    QUANTITY("Quantity") {
        @Override
        public Object valueIn(MaterialBatch theBatch) {
            return theBatch.getQuantity();
        }
    };

    abstract public Object valueIn(MaterialBatch theBatch);

    public final String name;

    private SheetOrderColumn(String name) {
        this.name = name;
    }

    public static SheetOrderColumn at(int offset) {
        return values()[offset];
    }
}
