package materials.ui;

//import materials.MaterialListener;
import data.MaterialSheet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import materials.Defect;

/**
 *
 * @author alan
 */
public class ThicknessTableModel extends AbstractTableModel implements ThicknessListener {

    private ArrayList<MaterialSheet> thicknessData = new ArrayList<>();
    
    @Override
    public boolean isCellEditable(int rowIndex, int colIndex){
        return true;
    }

    public void setData(List<MaterialSheet> newData) {
        thicknessData = new ArrayList<>(newData);
        this.fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return ThicknessColumn.values().length;
    }

    @Override
    public int getRowCount() {
        return thicknessData.size();
    }

    @Override
    public String getColumnName(int column) {
        return ThicknessColumn.at(column).name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return ThicknessColumn.at(columnIndex).valueIn(thicknessData.get(rowIndex));
    }

    @Override
  public void thicknessStateChanged(MaterialSheet theThickness) {
    for (int i = 0; i < thicknessData.size(); i++) {
      if (theThickness.getMaterialSheetId() == thicknessData.get(i).getMaterialSheetId()) {
        thicknessData.set(i, theThickness); 
        fireTableRowsUpdated(i, i);
        return;
      }
    }
    throw new Defect("No existing state for " + theThickness.getMaterialSheetId());
  } 
    
    public void addThickness(MaterialSheet newThickness) {
        thicknessData.add(newThickness);
        int row = thicknessData.size() - 1;
        fireTableRowsInserted(row, row);
    }

    public void deleteThickness(MaterialSheet theThickness) {
        int theRow = thicknessData.indexOf(theThickness);

        if (theRow > -1) {
            thicknessData.remove(theThickness);
            fireTableRowsDeleted(theRow, theRow);
        }
    }

    List<MaterialSheet> getData() {
        return thicknessData;
    }

    MaterialSheet getItemAtRow(int selectedRow) {
        return thicknessData.get(selectedRow);
    }
    
    @Override
    public void setValueAt(Object value, int row, int col) {
        ThicknessColumn.at(col).setValueAt(value, thicknessData.get(row)); 
        // = value;
//        data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}
