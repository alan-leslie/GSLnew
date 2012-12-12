package jobs.ui;

import data.JobItem;
import data.Material;
import data.MaterialBatch;
import data.MaterialSheet;
import data.StockItem;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import jobs.JobNumber;

public enum JobColumn {

    JOB_ID("Job Id") {
        @Override
        public Object valueIn(JobItem job) {
            String jobName = job.getJobName();
            String[] splitName = jobName.trim().split("/");
            JobNumber theJobNumber = new JobNumber("0", "123");
            
            if(splitName.length > 1){
                theJobNumber = new JobNumber(splitName[1], splitName[0]);
            }
            
            return theJobNumber;
        }
    },
    COMPANY_NAME("Company Name") {
        @Override
        public Object valueIn(JobItem job) {
            return job.getCustomerCompany().getCompanyName();
        }
    },
    ORDER_NO("Order No.") {
        @Override
        public Object valueIn(JobItem job) {
            return job.getStockItem().getOrderRef();
        }
    },
    CLIENT_NAME("Client Name") {
        @Override
        public Object valueIn(JobItem job) {
            return job.getClient().getClientName();
        }
    },
    MATERIAL_TYPE("Material Type") {
        @Override
        public Object valueIn(JobItem job) {
            List<String> addedValues = new ArrayList<>();
            StringBuilder theBuilder = new StringBuilder();
            StockItem theStockItem = (StockItem) (job.getStockItem());

            if (theStockItem != null) {
                Set materialBatchs = theStockItem.getMaterialBatchs();
                Iterator batchIterator = materialBatchs.iterator();

                while (batchIterator.hasNext()) {
                    MaterialBatch theFirstBatch = (MaterialBatch) batchIterator.next();
                    MaterialSheet theFirstMaterialSheet = theFirstBatch.getMaterialSheet();

                    if (theFirstMaterialSheet != null) {
                        Material theMaterial = theFirstMaterialSheet.getMaterial();

                        if (theMaterial != null) {
                            String materialType = theMaterial.getMaterialType();

                            if (!materialType.isEmpty() && !addedValues.contains(materialType)) {
                                if (!theBuilder.toString().isEmpty()) {
                                    theBuilder.append("/");
                                }

                                theBuilder.append(materialType);
                                addedValues.add(materialType);
                            }
                        }
                    }
                }
            }

            return theBuilder.toString();
        }
    },
    MATERIAL_NAME("Material Name") {
        @Override
        public Object valueIn(JobItem job) {
            List<String> addedValues = new ArrayList<>();
            StockItem theStockItem = (StockItem) (job.getStockItem());
            StringBuilder theBuilder = new StringBuilder();

            if (theStockItem != null) {
                Set materialBatchs = theStockItem.getMaterialBatchs();
                Iterator batchIterator = materialBatchs.iterator();

                while (batchIterator.hasNext()) {
                    MaterialBatch theFirstBatch = (MaterialBatch) batchIterator.next();
                    MaterialSheet theFirstMaterialSheet = theFirstBatch.getMaterialSheet();

                    if (theFirstMaterialSheet != null) {
                        Material theMaterial = theFirstMaterialSheet.getMaterial();

                        if (theMaterial != null) {
                            String materialDesc = theMaterial.getDescription();

                            if (!materialDesc.isEmpty() && !addedValues.contains(materialDesc)) {
                                if (!theBuilder.toString().isEmpty()) {
                                    theBuilder.append("/");
                                }

                                theBuilder.append(materialDesc);
                                addedValues.add(materialDesc);
                            }
                        }
                    }
                }
            }

            return theBuilder.toString();
        }
    },
    THICKNESS("Thickness") {
        @Override
        public Object valueIn(JobItem job) {
            Iterator it = job.getStockItem().getMaterialBatchs().iterator();
            StringBuilder thicknesses = new StringBuilder();

            while (it.hasNext()) {
                MaterialBatch theBatch = (MaterialBatch) it.next();
                MaterialSheet materialSheet = theBatch.getMaterialSheet();

                if (!(thicknesses.toString().contains(materialSheet.getThickness()))) {
                    if (!thicknesses.toString().isEmpty()) {
                        thicknesses.append(", ");
                    }

                    thicknesses.append(materialSheet.getThickness());
                }
            }

            return thicknesses.toString();
        }
    },
    TEMPLATE_DATE("Template Date") {
        @Override
        public Object valueIn(JobItem job) {
            Date templateDate = job.getTemplateDate();
            return templateDate;
        }
    },
    INVOICE_NO("Invoice No.") {
        @Override
        public Object valueIn(JobItem job) {
            return job.getInvoiceRef();
        }
    };

    abstract public Object valueIn(JobItem theItem);
    public final String name;

    private JobColumn(String name) {
        this.name = name;
    }

    public static JobColumn at(int offset) {
        return values()[offset];
    }
}
