package companies.ui;

import companies.CompanyListener;
import companies.Defect;
import data.CustomerCompany;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class CompaniesTableModel extends AbstractTableModel implements CompanyListener {

    private ArrayList<CustomerCompany> customers = new ArrayList<>();

    public void setData(List<CustomerCompany> newData) {
        customers = new ArrayList<>(newData);
        this.fireTableDataChanged();
    }

    public CustomerCompany getCompany(int row) {
        return customers.get(row);
    }

    @Override
    public int getColumnCount() {
        return CompanyColumn.values().length;
    }

    @Override
    public int getRowCount() {
        return customers.size();
    }

    @Override
    public String getColumnName(int column) {
        return CompanyColumn.at(column).name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return CompanyColumn.at(columnIndex).valueIn(customers.get(rowIndex));
    }

    @Override
    public void companyStateChanged(CustomerCompany newCompany) {
        for (int i = 0; i < customers.size(); i++) {
            CustomerCompany customer = customers.get(i);
            if (newCompany.getCustomerId() == customer.getCustomerId()) {
                customers.set(i, newCompany);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
        throw new Defect("No existing Sniper state for " + newCompany.getCustomerId());
    }

    public void addCompany(CustomerCompany newCompany) {
        customers.add(newCompany);
        int row = customers.size() - 1;
        fireTableRowsInserted(row, row);
    }

    public void deleteCompany(CustomerCompany theCompany) {
        int theRow = customers.indexOf(theCompany);

        if (theRow > -1) {
            customers.remove(theCompany);
            fireTableRowsDeleted(theRow, theRow);
        }
    }
}
