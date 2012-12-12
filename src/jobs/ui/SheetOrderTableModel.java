package jobs.ui;

import data.MaterialBatch;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import jobs.Defect;

/**
 *
 * @author alan
 */
public class SheetOrderTableModel extends AbstractTableModel implements MaterialBatchListener {
    @Override
    public boolean isCellEditable(int rowIndex, int colIndex){
        return false;
    }
    
    private ArrayList<MaterialBatch> orderData = new ArrayList<>();

    public void setData(List<MaterialBatch> newData) {
        orderData = new ArrayList<>(newData);
        this.fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return SheetOrderColumn.values().length;
    }

    @Override
    public int getRowCount() {
        return orderData.size();
    }

    @Override
    public String getColumnName(int column) {
        return SheetOrderColumn.at(column).name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return SheetOrderColumn.at(columnIndex).valueIn(orderData.get(rowIndex));
    }
        
    public MaterialBatch getItemAtRow(int selectedRow) {
        return orderData.get(selectedRow);
    }
    
    @Override
    public void batchStateChanged(MaterialBatch newBatch) {
        for (int i = 0; i < orderData.size(); i++) {
            if (newBatch.getMaterialBatchId() == orderData.get(i).getMaterialBatchId()) {
                orderData.set(i, newBatch);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
        throw new Defect("No existing state for " + newBatch.getMaterialBatchId());
    }

    public void addBatch(MaterialBatch newBatch) {
        orderData.add(newBatch);
        int row = orderData.size() - 1;
        fireTableRowsInserted(row, row);
    }

    public void deleteBatch(MaterialBatch theBatch) {
        int theRow = orderData.indexOf(theBatch);

        if (theRow > -1) {
            orderData.remove(theBatch);
            fireTableRowsDeleted(theRow, theRow);
        }
    }

    public List<MaterialBatch> getOrderData() {
        List<MaterialBatch> theOrderData = orderData;
        return theOrderData;
    }

    MaterialBatch getItemAt(int i) {
        return orderData.get(i);
    }
}
