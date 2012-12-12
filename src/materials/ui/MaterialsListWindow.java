package materials.ui;

import data.Material;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import materials.MaterialController;
import gsl.DBHelper;
import gsl.ui.TableWindow;

public class MaterialsListWindow extends JPanel implements TableWindow {

    public static final String APPLICATION_TITLE = "Materials";
    private static final String MATERIALS_TABLE_NAME = "Materials Table";
    public static final String MAIN_WINDOW_NAME = "Materials List";
    public static final String SHOW_LIST_BUTTON_NAME = "show list button";
    static Font textFont = new java.awt.Font("Tahoma", 0, 12);
    private MaterialsTableModel model = new MaterialsTableModel();
    private JPopupMenu popupMenu = new JPopupMenu();
    private Material selectedMaterial;
    private final String[] searchTypeStrings = {"All", "Material Type", "Material Name"};
    private DBHelper theData = null;
    private int selectedRow = -1;
    private final JButton showListButton;
    private JFrame theFrame;
    private JTextField newSearchStringField = null;
    private JComboBox searchTypeCombo = null;
    private JTable theMaterialsTable = null;

    public MaterialsListWindow(JFrame theFrame) {
        setName(MAIN_WINDOW_NAME);
        this.theFrame = theFrame;

        showListButton = new JButton("Show list");
        showListButton.setName(SHOW_LIST_BUTTON_NAME);
        showListButton.setEnabled(false);

        fillPanel();
        setVisible(true);

        setHelper();
    }

    private DBHelper getHelper() {
        if (theData == null) {
            setHelper();
        }

        return theData;
    }

    private void fillPanel() {
        theMaterialsTable = makeMaterialsTable();
        JPanel controls = makeControls();
        this.setLayout(new BorderLayout());
        this.add(controls, BorderLayout.NORTH);
        this.add(new JScrollPane(theMaterialsTable), BorderLayout.CENTER);
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

        newSearchStringField = searchStringField();

        controls.add(newSearchStringField);

//        JButton showListButton = new JButton("Show list");
//        showListButton.setName(SHOW_LIST_BUTTON_NAME);

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
        final JButton addButton = new JButton("Add Material...");
        addButton.setFont(textFont);

        JPanel controls = new JPanel(new FlowLayout());

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMaterial(e);
            }
        });

        controls.add(addButton);

        return controls;
    }

    private JTextField searchStringField() {
        JTextField searchStringField = new JTextField();
        searchStringField.setFont(textFont);
        searchStringField.setColumns(10);
        searchStringField.setName("search string");
        return searchStringField;
    }

    synchronized void editRow(ActionEvent e) {
        MaterialDialog materialDialog = new MaterialDialog(theFrame, "Material", theData);
        MaterialController theController = new MaterialController(selectedMaterial);

        theController.setUI(materialDialog);
        theController.setSessionHelper(getHelper());
        theController.setParentTableModel(getModel(), selectedRow);

        materialDialog.setController(theController);
        materialDialog.setMaterial(selectedMaterial);
        materialDialog.pack();

        materialDialog.setLocationRelativeTo(this);
        materialDialog.setVisible(true);
    }

    synchronized void addMaterial(ActionEvent e) {
        MaterialDialog materialDialog = new MaterialDialog(theFrame, "Material", theData);
        MaterialController theController = new MaterialController(selectedMaterial);

        theController.setUI(materialDialog);
        theController.setSessionHelper(getHelper());
        theController.setParentTableModel(getModel(), -1);

        materialDialog.setController(theController);
        materialDialog.setMaterial(theController.getMaterial());
        materialDialog.pack();

        materialDialog.setLocationRelativeTo(this);
        materialDialog.setVisible(true);
    }

    private JTable makeMaterialsTable() {
        JTable materialsTable = new JTable(model);
        materialsTable.setName(MATERIALS_TABLE_NAME);
        materialsTable.setAutoCreateRowSorter(true);

        JMenuItem menuItem = new JMenuItem("Edit Material...");
        menuItem.addActionListener(new InsertRowsActionAdapter(this));
        popupMenu.add(menuItem);

        MouseListener popupListener = new PopupListener();
        materialsTable.addMouseListener(popupListener);

        return materialsTable;
    }

    @Override
    public JTable getTable() {
        return theMaterialsTable;
    }

    public void setData(List<Material> materials) {
        model.setData(materials);
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
        SwingWorker worker = new SwingWorker<List<Material>, Void>() {
            @Override
            public List<Material> doInBackground() {
                DBHelper theHelper = getHelper();
                List<Material> materials = null;
                String searchStringBit = newSearchStringField.getText();
                String searchType = (String) searchTypeCombo.getSelectedItem();

                if (searchType.equalsIgnoreCase("All")) {
                    materials = theHelper.getMaterialsFromType("");
                }

                if (searchType.equalsIgnoreCase("Material Type")) {
                    materials = theHelper.getMaterialsFromType(searchStringBit);
                }

                if (searchType.equalsIgnoreCase("Material Name")) {
                    materials = theHelper.getMaterialsFromDesc(searchStringBit);
                }

                return materials;
            }

            @Override
            public void done() {
                try {
                    List<Material> items = get();
                    setData(items);
                    showListButton.setEnabled(true);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MaterialsListWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(MaterialsListWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        worker.execute();
    }

    class PopupListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            showPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                JTable source = (JTable) e.getSource();
                int row = source.rowAtPoint(e.getPoint());
                int column = source.columnAtPoint(e.getPoint());
                TableModel tableModel = source.getModel();
                MaterialsTableModel theModel = (MaterialsTableModel) tableModel;
                selectedRow = row;

                if (!source.isRowSelected(row)) {
                    source.changeSelection(row, column, false, false);
                }

                row = source.convertRowIndexToModel(row);
                selectedMaterial = theModel.getMaterial(row);

                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public MaterialsTableModel getModel() {
        return model;
    }
}

class InsertRowsActionAdapter implements ActionListener {

    MaterialsListWindow adaptee;

    InsertRowsActionAdapter(MaterialsListWindow adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.editRow(e);
    }
}
