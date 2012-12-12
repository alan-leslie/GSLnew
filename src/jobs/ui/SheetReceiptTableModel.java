package jobs.ui;

import data.BatchSheet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import jobs.Defect;

/**
 *
 * @author alan
 */
public class SheetReceiptTableModel extends AbstractTableModel implements BatchSheetListener {

    private ArrayList<BatchSheet> receiptData = new ArrayList<>();

    @Override
    public boolean isCellEditable(int rowIndex, int colIndex) {
        if (colIndex == SheetReceiptColumn.BAR_CODE.ordinal()) {
            return true;
        }
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        SheetReceiptColumn.at(col).setValueAt(value, receiptData.get(row));
        fireTableCellUpdated(row, col);
    }

    public void setData(List<BatchSheet> newData) {
        receiptData = new ArrayList<>(newData);
        this.fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return SheetReceiptColumn.values().length;
    }

    @Override
    public int getRowCount() {
        return receiptData.size();
    }

    @Override
    public String getColumnName(int column) {
        return SheetReceiptColumn.at(column).name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return SheetReceiptColumn.at(columnIndex).valueIn(receiptData.get(rowIndex));
    }

    @Override
    public void batchSheetStateChanged(BatchSheet theSheet) {
        for (int i = 0; i < receiptData.size(); i++) {
            if (theSheet.getBatchSheetId() == receiptData.get(i).getBatchSheetId()) {
                receiptData.set(i, theSheet);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
        throw new Defect("No existing state for " + theSheet.getBatchSheetId());
    }

    public void addReceipt(BatchSheet newReceipt) {
        receiptData.add(newReceipt);
        int row = receiptData.size() - 1;
        fireTableRowsInserted(row, row);
    }

    public List<BatchSheet> getReceiptData() {
        List<BatchSheet> theReceiptData = receiptData;
        return theReceiptData;
    }
    
    public void deleteReceipt(BatchSheet theSheet) {
        int theRow = receiptData.indexOf(theSheet);

        if (theRow > -1) {
            receiptData.remove(theSheet);
            fireTableRowsDeleted(theRow, theRow);
        }
    }
}
