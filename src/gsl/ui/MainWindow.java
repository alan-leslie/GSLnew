package gsl.ui;

/*
 * MainWindow.java requires one additional file:
 *   images/middle.gif.
 */
import companies.ui.CompaniesListWindow;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import jobs.ui.JobListWindow;
import materials.ui.MaterialsListWindow;
import stock.ui.StockListWindow;

public class MainWindow extends JPanel {

    private TabChangeListener theTabChangeListener = null;
    private List<TableWindow> theTabs = null;
    private final MainFrame mainFrame;

    public MainWindow(JFrame theFrame) {
        super(new GridLayout(1, 1));

        theTabs = new ArrayList<>();

        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = createImageIcon("images/middle.gif");

        JobListWindow theJobsWindow = new JobListWindow(theFrame);
        tabbedPane.addTab("Jobs", icon, theJobsWindow,
                "Shows Jobs Listing");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
        theTabs.add(theJobsWindow);

        StockListWindow theStockWindow = new StockListWindow(theFrame);
        tabbedPane.addTab("Stock", icon, theStockWindow,
                "Shows Stock Listing");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        theTabs.add(theStockWindow);

        MaterialsListWindow theMaterialsWindow = new MaterialsListWindow(theFrame);
        tabbedPane.addTab("Materials", icon, theMaterialsWindow,
                "Shows Material Listing");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
        theTabs.add(theMaterialsWindow);

        CompaniesListWindow theCompanyWindow = new CompaniesListWindow(theFrame);
        tabbedPane.addTab("Companies", icon, theCompanyWindow, "Shows Companies Listing");
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
        theTabs.add(theCompanyWindow);

        //Add the tabbed pane to this panel.
        add(tabbedPane);
        theTabChangeListener = new TabChangeListener(tabbedPane);
        tabbedPane.addChangeListener(theTabChangeListener);
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        mainFrame = (MainFrame)theFrame;
    }

    private void enableReportMenuItem() {
        JMenuItem theReportMenuItem = mainFrame.getReportMenuItem();
        theReportMenuItem.setEnabled(true);
    }
    
    private void disableReportMenuItem() {
        JMenuItem theReportMenuItem = mainFrame.getReportMenuItem();
        theReportMenuItem.setEnabled(false);
    }
    
    public int getSelectedTabIndex() {
        return theTabChangeListener.selectedIndex;
    }

    public JTable getCurrentTable() {
        JTable currentTable = null;
        int selectedIndex = getSelectedTabIndex();

        if (selectedIndex != -1) {
            currentTable = theTabs.get(selectedIndex).getTable();
        }

        return currentTable;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MainWindow.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    class TabChangeListener implements ChangeListener {

        private JTabbedPane adaptee;
        public int selectedIndex = 0;

        TabChangeListener(JTabbedPane adaptee) {
            this.adaptee = adaptee;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            selectedIndex = adaptee.getSelectedIndex();
            if(selectedIndex == 0){
                enableReportMenuItem();
            } else {
                disableReportMenuItem();                
            }
        }
    }
}
