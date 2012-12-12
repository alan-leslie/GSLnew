package stock.ui;

import data.JobItem;
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
import gsl.DBHelper;
import gsl.ui.TableWindow;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;
import jobs.JobItemController;
import jobs.JobNumber;
import jobs.ui.JobDialog;

public class StockListWindow extends JPanel implements TableWindow {

    public static final String APPLICATION_TITLE = "Stock";
    private static final String JOB_TABLE_NAME = "Stock Table";
    public static final String MAIN_WINDOW_NAME = "Stock List";
    public static final String SHOW_LIST_BUTTON_NAME = "show list button";
    static Font textFont = new java.awt.Font("Tahoma", 0, 12);
    private StockTableModel model = new StockTableModel();
    private JPopupMenu popupMenu = new JPopupMenu();
    private JobItem selectedItem;
    private final String[] searchTypeStrings = {"All", "Company Name", "Material Type", "Material Name", "In Progress"};
    private DBHelper theData;
    private int selectedRow = -1;
    private JButton showListButton = null;
    private JComboBox searchTypeCombo = null;
    private JTextField newSearchStringField = null;
    private JFrame theFrame;
    private JTable theJobTable = null;

    public StockListWindow(JFrame theFrame) {
//        super("Materials List");
        this.theFrame = theFrame;

        setName(MAIN_WINDOW_NAME);
        fillPanel();
//        pack();
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        setHelper();
    }

    private void fillPanel() {
        theJobTable = makeJobTable();
        JPanel controls = makeControls();
        this.setLayout(new BorderLayout());
        this.add(controls, BorderLayout.NORTH);
        this.add(new JScrollPane(theJobTable), BorderLayout.CENTER);
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

        showListButton = new JButton("Show list");
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
        JPanel controls = new JPanel(new FlowLayout());
        return controls;
    }

    private JTextField searchStringField() {
        JTextField searchStringField = new JTextField();
        searchStringField.setFont(textFont);
        searchStringField.setColumns(10);
        searchStringField.setName("search string");
        return searchStringField;
    }

    void editRow(ActionEvent e) {
        JobDialog jobDialog = new JobDialog(theFrame, "Stock", theData);
        JobItemController theController = new JobItemController(theData, selectedItem);
        theController.setUI(jobDialog);
//        theController.setParentTableModel(getModel(), -1);

        jobDialog.setController(theController);
        jobDialog.setJob(selectedItem);
        jobDialog.pack();

        jobDialog.setLocationRelativeTo(this);
        jobDialog.setVisible(true);
    }

    private JTable makeJobTable() {
        final TableCellRenderer dateRenderer = new DateRenderer();
        
        JTable jobTable = new JTable(model){
            @Override
            public TableCellRenderer getCellRenderer(int row, int column){
                if(column == StockColumn.TEMPLATE_DATE.ordinal()){
                    return dateRenderer;
                } else {
                    return super.getCellRenderer(row, column);
                }
            }          
        };
        
        jobTable.setName(JOB_TABLE_NAME);
        TableRowSorter theSorter = new TableRowSorter<>(model);
        
        Comparator<JobNumber> jobNoComparator = new Comparator<JobNumber>(){
            @Override
            public int compare(JobNumber n1, JobNumber n2){
                return n1.compareTo(n2);      
            }
        };
        
        theSorter.setComparator(StockColumn.JOB_ID.ordinal(), jobNoComparator);
        jobTable.setRowSorter(theSorter);
        
        TableColumn column = null;
        for(int i = 0; i < 9; ++i){
            column = jobTable.getColumnModel().getColumn(i);
            
            if(i== 1 || i == 5 || i == 6){
                column.setPreferredWidth(300);
            } else {
                column.setPreferredWidth(150);                
            }   
        }

        JMenuItem menuItem = new JMenuItem("Edit Job...");
        menuItem.addActionListener(new EditRowActionAdapter(this));
        popupMenu.add(menuItem);

//        // add the listener to the jtable
        MouseListener popupListener = new PopupListener();
//        // add the listener specifically to the header
        jobTable.addMouseListener(popupListener);

        return jobTable;
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
                StockTableModel theModel = (StockTableModel) tableModel;

                if (!source.isRowSelected(row)) {
                    source.changeSelection(row, column, false, false);
                }

                row = source.convertRowIndexToModel(row);
                selectedItem = theModel.getItem(row);

                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

        private void showPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    @Override
    public JTable getTable() {
        return theJobTable;
    }

    public StockTableModel getModel() {
        return model;
    }

    public void setData(List<JobItem> jobs) {
        model.setData(jobs);
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
        SwingWorker worker = new SwingWorker<List<JobItem>, Void>() {
            @Override
            public List<JobItem> doInBackground() {
                DBHelper theHelper = getHelper();
                List<JobItem> items = null;
                String searchStringBit = newSearchStringField.getText();
                String searchType = (String) searchTypeCombo.getSelectedItem();

                if (searchType.equalsIgnoreCase("Material Type")) {
                    items = theHelper.getJobsForMaterialType(searchStringBit);
                }

                if (searchType.equalsIgnoreCase("Material Name")) {
                    items = theHelper.getJobsForMaterialDesc(searchStringBit);
                }

                if (searchType.equalsIgnoreCase("Company Name")) {
                    items = theHelper.getJobsForCompanyName(searchStringBit);
                }

                if (searchType.equalsIgnoreCase("In Progress")) {
                    items = theHelper.getOpenJobs();
                }

                if (searchType.equalsIgnoreCase("All")) {
                    items = theHelper.getJobs();
                }

                return items;
            }

            @Override
            public void done() {
                try {
                    List<JobItem> items = get();
                    setData(items);
                    showListButton.setEnabled(true);
                } catch (InterruptedException ex) {
                    Logger.getLogger(StockListWindow.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(StockListWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        worker.execute();
    }
    
    static class DateRenderer extends DefaultTableCellRenderer {
        DateFormat formatter;
        public DateRenderer(){
            super();
            formatter = new SimpleDateFormat("d-MMM");
        }
        
        @Override
        public void setValue(Object value){
            setText((value == null) ? "" : formatter.format(value));       
        }
    }
}

class EditRowActionAdapter implements ActionListener {

    StockListWindow adaptee;

    EditRowActionAdapter(StockListWindow adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        adaptee.editRow(e);
    }
}
