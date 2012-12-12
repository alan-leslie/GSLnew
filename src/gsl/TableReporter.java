package gsl;

import javax.swing.JTable;
import javax.swing.table.TableModel;
import jobs.ui.JobColumn;
import jobs.ui.JobTableModel;

/**
 *
 * @author alan
 */
public class TableReporter {

    public TableReporter() {
    }

    public String reportEngineered(JTable table) {
        JobTableModel model = (JobTableModel) table.getModel();
        int engineeredTotal = 0;

        for (int i = 1; i < model.getRowCount(); i++) {
            int col = JobColumn.MATERIAL_TYPE.ordinal();

            Object cellValue = model.getValueAt(i, col);
            if (cellValue != null) {
                String materialTypes = (String)cellValue;
                if(materialTypes.contains("Silestone") || 
                   materialTypes.contains("Zodiac") || 
                   materialTypes.contains("Other Engineered")){
                    engineeredTotal += 1;                  
                }
            }       
        }
        
        float percentage = ((float)engineeredTotal/(float)(model.getRowCount() - 1)) * (float)100.0;
        int round = Math.round(percentage);
        return Integer.toString(round);
    }
}
