package stock.ui;

import data.JobItem;
import data.Material;
import data.MaterialBatch;
import data.MaterialSheet;
import data.StockItem;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import jobs.JobNumber;

public enum StockColumn {

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
    MATERIAL_NAME("Material") {
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
    QTY_12MM("Qty 12mm") {
        @Override
        public Object valueIn(JobItem job) {
            Iterator it = job.getStockItem().getMaterialBatchs().iterator();
            int totalQty = 0;

            while (it.hasNext()) {
                MaterialBatch theBatch = (MaterialBatch) it.next();
                MaterialSheet materialSheet = theBatch.getMaterialSheet();

                if (materialSheet.getThickness().contains("12mm")) {
                    totalQty++;
                }
            }

            return totalQty;
        }
    },
    QTY_20MM("Qty 20mm") {
        @Override
        public Object valueIn(JobItem job) {
            Iterator it = job.getStockItem().getMaterialBatchs().iterator();
            int totalQty = 0;

            while (it.hasNext()) {
                MaterialBatch theBatch = (MaterialBatch) it.next();
                MaterialSheet materialSheet = theBatch.getMaterialSheet();

                if (materialSheet.getThickness().contains("20mm")) {
                    totalQty++;
                }
            }

            return totalQty;
        }
    },
    QTY_30MM("Qty 30mm") {
        @Override
        public Object valueIn(JobItem job) {
            Iterator it = job.getStockItem().getMaterialBatchs().iterator();
            int totalQty = 0;

            while (it.hasNext()) {
                MaterialBatch theBatch = (MaterialBatch) it.next();
                MaterialSheet materialSheet = theBatch.getMaterialSheet();

                if (materialSheet.getThickness().contains("30mm")) {
                    totalQty++;
                }
            }

            return totalQty;
        }
    };

    abstract public Object valueIn(JobItem snapshot);
    public final String name;

    private StockColumn(String name) {
        this.name = name;
    }

    public static StockColumn at(int offset) {
        return values()[offset];
    }
}
