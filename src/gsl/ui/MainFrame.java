package gsl.ui;

import gsl.ExcelExporter;
import gsl.TableReporter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;


/**
 *
 * @author alan
 */
public class MainFrame extends JFrame implements ActionListener {

    private MainWindow mainWindow = null;
    private JMenuItem exportMenuItem = null;
    private JMenuItem reportMenuItem = null;

    public MainFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        mainWindow = new MainWindow(this);
        add(mainWindow, BorderLayout.CENTER);
        setTitle("GSL");
        ImageIcon theIcon = new ImageIcon();
        this.setIconImage(theIcon.getImage());

        //Display the window.
        pack();

        setJMenuBar(createMenuBar());
        setVisible(true);
    }

    final JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
        JCheckBoxMenuItem cbMenuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription(
                "File menu");
        menuBar.add(menu);

        Action exportAction = new ExportAction("Export...", null,
                "Export to spreadsheet.",
                new Integer(KeyEvent.VK_E));
        exportMenuItem = new JMenuItem(exportAction);
        menu.add(exportMenuItem);

        //Build second menu in the menu bar.
        menu = new JMenu("Reports");
        menu.setMnemonic(KeyEvent.VK_R);
        menu.getAccessibleContext().setAccessibleDescription(
                "Report menu");
        menuBar.add(menu);

        Action reportAction = new ReportEngineeredAction("Percentage engineered..", null,
                "Report engineered percentage.",
                new Integer(KeyEvent.VK_G));
        reportMenuItem = new JMenuItem(reportAction);
        menu.add(reportMenuItem);

        //Build help menu in the menu bar.
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription(
                "Help menu");
        menuBar.add(menu);

        Action aboutAction = new AboutAction("About...", null,
                "This is the About dialog.",
                new Integer(KeyEvent.VK_A));
        menuItem = new JMenuItem(aboutAction);
        menu.add(menuItem);


        return menuBar;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = MainFrame.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    JMenuItem getReportMenuItem() {
        return reportMenuItem;
    }

    class AboutAction extends AbstractAction {

        public AboutAction(String text, ImageIcon icon,
                String desc, Integer mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(rootPane, "this is about", "About title", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    class ReportEngineeredAction extends AbstractAction {

        public ReportEngineeredAction(String text, ImageIcon icon,
                String desc, Integer mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            report();
        }
    }

    class ExportAction extends AbstractAction {

        public ExportAction(String text, ImageIcon icon,
                String desc, Integer mnemonic) {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            File theDir = new File(".");

            ExtensionFileFilter filter = new ExtensionFileFilter(null, "csv");

            JFileChooser fileChooser = new JFileChooser(theDir);
            fileChooser.setFileFilter(filter);
            int returnVal = fileChooser.showOpenDialog(rootPane);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                export(fileChooser.getSelectedFile());
                //                System.out.println("You chose to open this file: "
//                        + fileChooser.getSelectedFile().getName());
            }
        }
    }

    private void export(final File selectedFile) {
        exportMenuItem.setEnabled(false);
        SwingWorker worker = new SwingWorker<String, Void>() {
            @Override
            public String doInBackground() {
                ExcelExporter theExporter = new ExcelExporter();
                JTable currentTable = mainWindow.getCurrentTable();
                try {
                    theExporter.exportTable(currentTable, selectedFile);
                } catch (IOException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                return "";
            }

            @Override
            public void done() {
                exportMenuItem.setEnabled(true);
                JOptionPane.showMessageDialog(rootPane, "Export complete");
            }
        };

        worker.execute();
    }
    
    private void report() {
        reportMenuItem.setEnabled(false);
        SwingWorker worker = new SwingWorker<String, Void>() {
            @Override
            public String doInBackground() {
                TableReporter theReporter = new TableReporter();
                JTable currentTable = mainWindow.getCurrentTable();
                String reportEngineered = theReporter.reportEngineered(currentTable);

                return reportEngineered;
            }

            @Override
            public void done() {
                try {
                    String reportEngineered = get();
                    JOptionPane.showMessageDialog(rootPane, "Percentage is: " + reportEngineered + "%.", "Percentage of Engineered", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                reportMenuItem.setEnabled(true);
            }
        };

        worker.execute();
    }
}

class ExtensionFileFilter extends FileFilter {

    String description;
    String extensions[];

    public ExtensionFileFilter(String description, String extension) {
        this(description, new String[]{extension});
    }

    public ExtensionFileFilter(String description, String extensions[]) {
        if (description == null) {
            this.description = extensions[0];
        } else {
            this.description = description;
        }
        this.extensions = (String[]) extensions.clone();
        toLower(this.extensions);
    }

    private void toLower(String array[]) {
        for (int i = 0, n = array.length; i < n; i++) {
            array[i] = array[i].toLowerCase();
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        } else {
            String path = file.getAbsolutePath().toLowerCase();
            for (int i = 0, n = extensions.length; i < n; i++) {
                String extension = extensions[i];
                if ((path.endsWith(extension) && (path.charAt(path.length() - extension.length() - 1)) == '.')) {
                    return true;
                }
            }
        }
        return false;
    }
}