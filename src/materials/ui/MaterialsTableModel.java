package materials.ui;

import data.Material;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import materials.Defect;

public class MaterialsTableModel extends AbstractTableModel
        implements MaterialListener {

    private ArrayList<Material> materials = new ArrayList<>();

    public void setData(List<Material> newData) {
        materials = new ArrayList<>(newData);
        this.fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return MaterialColumn.values().length;
    }

    @Override
    public int getRowCount() {
        return materials.size();
    }

    @Override
    public String getColumnName(int column) {
        return MaterialColumn.at(column).name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return MaterialColumn.at(columnIndex).valueIn(materials.get(rowIndex));
    }

    public Material getMaterial(int row) {
        return materials.get(row);
    }
    
    @Override
    public void materialStateChanged(Material theMaterial) {
        for (int i = 0; i < materials.size(); i++) {
            Material material = materials.get(i);
            if (theMaterial.getMaterialId() == material.getMaterialId()) {
                materials.set(i, theMaterial);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
        throw new Defect("No existing state for " + theMaterial.getMaterialId());
    }        

    public void addMaterial(Material newMaterial) {
        materials.add(newMaterial);
        int row = materials.size() - 1;
        fireTableRowsInserted(row, row);
    }
    
    public void deleteMaterial(Material theMaterial) {
        int theRow = materials.indexOf(theMaterial);

        if (theRow > -1) {
            materials.remove(theMaterial);
            fireTableRowsDeleted(theRow, theRow);
        }
    }
}
