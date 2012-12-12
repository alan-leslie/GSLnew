package companies.ui;

import companies.CompanyController;
import gsl.DBHelper;
import data.CustomerCompany;
import gsl.ui.TableWindow;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.TableModel;

public class CompaniesListWindow extends JPanel implements TableWindow {

    public static final String APPLICATION_TITLE = "Companies";
    private static final String COMPANY_TABLE_NAME = "Company Table";
    public static final String MAIN_WINDOW_NAME = "Company List";
    public static final String SHOW_LIST_BUTTON_NAME = "show list button";
    static Font textFont = new java.awt.Font("Tahoma", 0, 12);
    private CompaniesTableModel model = new CompaniesTableModel();
    private JPopupMenu popupMenu = new JPopupMenu();
    private CustomerCompany selectedCompany = null;
    private final String[] searchTypeStrings = {"All", "Company Name"};
    private DBHelper theData;
    private int selectedRow = -1;
    private JFrame theFrame;
    private final JButton showListButton;
    private JTextField searchStringField = null;
    private JComboBox searchTypeCombo = null;
    private JTable companiesTable = null;

    public CompaniesListWindow(JFrame theFrame) {
        setName(MAIN_WINDOW_NAME);
        this.theFrame = theFrame;

        showListButton = new JButton("Show list");
        showListButton.setName(SHOW_LIST_BUTTON_NAME);
        showListButton.setEnabled(false);

        fillPanel();
        setVisible(true);
        setHelper();
    }

    private void fillPanel() {
        companiesTable = makeCompaniesTable();
        JPanel controls = makeControls();
        this.setLayout(new BorderLayout());
        this.add(controls, BorderLayout.NORTH);
        this.add(new JScrollPane(companiesTable), BorderLayout.CENTER);
        this.add(makeSouth(), BorderLayout.SOUTH);
    }

    private JPanel makeControls() {
        JPanel controls = new JPanel(new FlowLayout());
        searchTypeCombo = new JComboBox(searchTypeStrings);
        searchTypeCombo.setFont(textFont);
        searchTypeCombo.setSelectedIndex(0);

        JLabel theLabel = new JLabel("Search Type:", JLabel.TRAILING);
        controls.add(theLabel);
        theLabel.setLabelFor(searchTypeCombo);
        controls.add(searchTypeCombo);

        searchStringField = newSearchStringField();

        controls.add(searchStringField);

        showListButton.setName(SHOW_LIST_BUTTON_NAME);

        showListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }
        });

        controls.add(showListButton);

        return controls;
    }

    private JPanel makeSouth() {
        final JButton addButton = new JButton("Add Company...");
        addButton.setFont(textFont);

        JPanel controls = new JPanel(new FlowLayout());

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCompany(e);
            }
        });

        controls.add(addButton);

        return controls;
    }

    private JTextField newSearchStringField() {
        JTextField newSearchStringField = new JTextField();
        newSearchStringField.setFont(textFont);
        newSearchStringField.setColumns(10);
        newSearchStringField.setName("search string");
        return newSearchStringField;
    }

    void editRow(ActionEvent e) {
        CompanyDialog companyDialog = new CompanyDialog(theFrame, "Company", theData);
        CompanyController theController = new CompanyController(selectedCompany);
        theController.setUI(companyDialog);
        theController.setSessionHelper(getHelper());
        theController.setParentTableModel(getModel(), -1);
        companyDialog.setController(theController);
        companyDialog.setCompany(selectedCompany);
        companyDialog.pack();

        companyDialog.setLocationRelativeTo(this);
        companyDialog.setVisible(true);
    }

    void addCompany(ActionEvent e) {
        CompanyDialog companyDialog = new CompanyDialog(theFrame, "Company", theData);
        CompanyController theController = new CompanyController(selectedCompany);
        theController.setUI(companyDialog);
        theController.setSessionHelper(getHelper());
        theController.setParentTableModel(getModel(), -1);

        companyDialog.setController(theController);
        companyDialog.setCompany(theController.getCompany());
        companyDialog.pack();

        companyDialog.setLocationRelativeTo(this);
        companyDialog.setVisible(true);
    }

    private JTable makeCompaniesTable() {
        companiesTable = new JTable(model);
        companiesTable.setName(COMPANY_TABLE_NAME);
        companiesTable.setAutoCreateRowSorter(true);

        JMenuItem menuItem = new JMenuItem("Edit Company...");
        menuItem.addActionListener(new InsertRowsActionAdapter(this));
        popupMenu.add(menuItem);

        companiesTable.addMouseListener(new PopupListener());

        return companiesTable;
    }

    public void setData(List<CustomerCompany> companies) {
        model.setData(companies);
    }

    @Override
    public JTable getTable() {
        return companiesTable;
    }

    class PopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
//            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                JTable source = (JTable) e.getSource();
                int row = source.rowAtPoint(e.getPoint());
                int column = source.columnAtPoint(e.getPoint());
                TableModel tableModel = source.getModel();
                CompaniesTableModel theModel = (CompaniesTableModel) tableModel;
                selectedRow = row;

                if (!source.isRowSelected(row)) {
                    source.changeSelection(row, column, false, false);
                }

                row = source.convertRowIndexToModel(row);
                selectedCompany = theModel.getCompany(row);

                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public CompaniesTableModel getModel() {
        return model;
    }

    private DBHelper getHelper() {
        if (theData == null) {
            setHelper();
        }

        return theData;
    }

    private void setHelper() {
        showListButton.setEnabled(false);
        SwingWorker worker = new SwingWorker<String, Void>() {
            @Override
            public String doInBackground() {
                theData = new DBHelper();
                return "";
            }

            @Override
            public void done() {
                showListButton.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void search() {
        showListButton.setEnabled(false);
        SwingWorker worker = new SwingWorker<List<CustomerCompany>, Void>() {
            @Override
            public List<CustomerCompany> doInBackground() {
                DBHelper theHelper = getHelper();
                String companyNameBit = searchStringField.getText();
                List<CustomerCompany> companies = null;

                String searchType = (String) searchTypeCombo.getSelectedItem();

                if (searchType.equalsIgnoreCase("All")) {
                    companies = getHelper().getCompanies(null);
                } else {
                    companies = getHelper().getCompanies(companyNameBit);
                }

                return companies;
            }

            @Override
            public void done() {
                try {
                    List<CustomerCompany> companies = get();
                    setData(companies);
                    showListButton.setEnabled(true);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CompaniesListWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(CompaniesListWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        worker.execute();
    }
}

class InsertRowsActionAdapter implements ActionListener {

    CompaniesListWindow adaptee;

    InsertRowsActionAdapter(CompaniesListWindow adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.editRow(e);
    }
}
