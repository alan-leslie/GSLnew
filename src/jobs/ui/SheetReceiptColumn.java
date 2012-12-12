package jobs.ui;

import data.BatchSheet;

/**
 *
 * @author alan
 */
public enum SheetReceiptColumn {

    TYPE("Material Type") {
        @Override
        public Object valueIn(BatchSheet theSheet) {
            String theType = "";

            if (theSheet != null) {
                theType = theSheet.getMaterialBatch().getMaterialSheet().getMaterial().getMaterialType();
            }
            
            return theType;
        }

        @Override
        public void setValueAt(Object value, BatchSheet theSheet) {
//            column is not editanleso do nothing
        }
    },
    DESCRIPTION("Material Name") {
        @Override
        public Object valueIn(BatchSheet theSheet) {
            String theDescription = "";

            if (theSheet != null) {
                theDescription = theSheet.getMaterialBatch().getMaterialSheet().getMaterial().getDescription();
            }
            return theDescription;
        }

        @Override
        public void setValueAt(Object value, BatchSheet theSheet) {
//            column is not editanleso do nothing
        }
    },
    THICKNESS("Thickness") {
        @Override
        public Object valueIn(BatchSheet theSheet) {
            return theSheet.getMaterialBatch().getMaterialSheet().getThickness();
        }

        @Override
        public void setValueAt(Object value, BatchSheet theSheet) {
//            column is not editanleso do nothing
        }
    },
    BAR_CODE("Bar code") {
        @Override
        public Object valueIn(BatchSheet theSheet) {
            return theSheet.getBarCode();
        }

        @Override
        public void setValueAt(Object value, BatchSheet theSheet) {
            theSheet.setBarCode((String) value);
        }
    };

    abstract public void setValueAt(Object value, BatchSheet theSheet);
    abstract public Object valueIn(BatchSheet theSheet);
    public final String name;

    private SheetReceiptColumn(String name) {
        this.name = name;
    }

    public static SheetReceiptColumn at(int offset) {
        return values()[offset];
    }
}
