/*
 */
package jobs.ui;

import companies.CompanyController;
import companies.ui.CompanyDialog;
import data.Address;
import data.BatchSheet;
import data.City;
import data.Client;
import data.Country;
import data.CustomerCompany;
import data.JobItem;
import data.Material;
import data.MaterialBatch;
import data.MaterialSheet;
import data.StockItem;
import gsl.DBHelper;
import gsl.ui.ComponentPair;
import gsl.ui.UIUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.TableModel;
import jobs.JobItemController;
import materials.MaterialController;
import materials.ui.MaterialDialog;
import net.sourceforge.jdatepicker.DateModel;
import net.sourceforge.jdatepicker.JDateComponentFactory;
import net.sourceforge.jdatepicker.JDatePicker;

public class JobDialog extends JDialog
        implements JobItemDisplayInterface,
        ActionListener,
        javax.swing.event.DocumentListener,
        TableModelListener {

    private JFrame dd;
    static Font textFont = new java.awt.Font("Tahoma", 0, 12);
    private final JPanel mainPanel;
    private final JPanel eastMainPanel;
    private final JPanel westMainPanel;
    private final JPanel customerPanel;
    private JPanel materialPanel;
    private final JPanel miscPanel;
    private final JPanel notesPanel;
    private JobItemController theController;
    private JTextField addressField1;
    private JTextField addressField2;
    private JTextField cityField;
    private JTextField countryField;
    private JTextField postCodeField;
    private JComboBox companyNameField;
    private JTextField clientNameField;
    private JTextField materialTypeField;
    private JTextField materialNameField;
    private DatePickerPair theOrderDatePicker = new DatePickerPair();
    private DatePickerPair theReceiptDatePicker = new DatePickerPair();
    private DatePickerPair theTemplateDatePicker = new DatePickerPair();
    private DatePickerPair theEstimateDatePicker = new DatePickerPair();
    private DatePickerPair theInvoiceDatePicker = new DatePickerPair();
    private JobItem originalJob = null;
    private JButton closeButton = null;
    private JButton saveButton = null;
    private JButton deleteButton = null;
    private ActionListener closeListener = null;
    private ActionListener deleteListener = null;
    private ActionListener saveListener = null;
    private JButton companyDetailsButton = null;
    private JButton materialDetailsButton = null;
    private List<Material> theMaterials = null;
    private List<MaterialSheet> theSheets = null;
    private List<CustomerCompany> theCompanies = null;
    private final DBHelper theHelper;
    private SheetReceiptTableModel theReceiptModel;
    private SheetOrderTableModel theOrderModel;
    private JTable orderTable = null;
    private JTable receiptTable = null;
    private AddBatchListener addBatchListener;
    private DeleteBatchListener deleteBatchListener;
    private boolean settingFields;
    private List<String> theTypes;
    private JTextArea notesField = null;

    /**
     * Creates the reusable dialog.
     */
    public JobDialog(JFrame aFrame, String aWord, DBHelper theHelper) {
        super(aFrame, true);
        dd = aFrame;
        setLayout(new BorderLayout());
        this.setResizable(false);
        this.theHelper = theHelper;
        theTypes = theHelper.getTypes();

        mainPanel = new JPanel();
        Dimension mainDim = new Dimension(800, 395);
        mainPanel.setPreferredSize(mainDim);
        mainPanel.setLayout(new BorderLayout());

        eastMainPanel = new JPanel();
        eastMainPanel.setLayout(new BorderLayout());

        customerPanel = createCustomerPanel();
        materialPanel = createMaterialPanel();
        miscPanel = createMiscPanel();
        notesPanel = createNotesPanel();

        eastMainPanel.add(customerPanel, BorderLayout.CENTER);
        westMainPanel = createEastPanel();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        JPanel buttonEastPanel = new JPanel();
        buttonEastPanel.setLayout(new BorderLayout());
        JPanel buttonWestPanel = new JPanel();
        buttonWestPanel.setLayout(new BorderLayout());

        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        saveButton.addActionListener(saveListener);
        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(true);
        deleteButton.addActionListener(deleteListener);
        closeButton = new JButton("Close");

        buttonEastPanel.add(closeButton, BorderLayout.WEST);
        buttonWestPanel.add(deleteButton, BorderLayout.EAST);
        buttonWestPanel.add(saveButton, BorderLayout.WEST);

        buttonPanel.add(buttonEastPanel, BorderLayout.EAST);
        buttonPanel.add(buttonWestPanel, BorderLayout.WEST);

        mainPanel.add(eastMainPanel, BorderLayout.WEST);
        mainPanel.add(westMainPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                companyNameField.requestFocusInWindow();
            }
        });
    }

    void updateTitle() {
        if (originalJob == null) {
            setTitle("Job No. New");
        } else {
            setTitle("Job No. " + originalJob.getJobName());
        }
    }

    @Override
    public void disableSave() {
        saveButton.setEnabled(false);
    }

    @Override
    public void enableSave() {
        saveButton.setEnabled(true);
    }

    @Override
    public void disableDelete() {
        deleteButton.setEnabled(false);
    }

    @Override
    public void enableDelete() {
        deleteButton.setEnabled(true);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        enableSaveDisableDelete();
    }

    private static class MaterialListener extends MouseAdapter {

        private JTextField typeField;
        private JTextField descField;
        private JButton materialButton;

        private MaterialListener(JTextField materialTypeField, JTextField materialNameField, JButton materialDetailsButton) {
            typeField = materialTypeField;
            descField = materialNameField;
            materialButton = materialDetailsButton;
        }

        @Override
        public void mousePressed(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            JTable source = (JTable) e.getSource();
            int row = source.rowAtPoint(e.getPoint());
            int column = source.columnAtPoint(e.getPoint());
            TableModel tableModel = source.getModel();
            SheetOrderTableModel theModel = (SheetOrderTableModel) tableModel;

            if (!source.isRowSelected(row)) {
                source.changeSelection(row, column, false, false);
            }

            row = source.convertRowIndexToModel(row);

            if (row == -1) {
                typeField.setText("");
                descField.setText("");
                materialButton.setEnabled(false);
            } else {
                MaterialBatch selectedMaterial = theModel.getItemAt(row);
                Material theMaterial = selectedMaterial.getMaterialSheet().getMaterial();
                typeField.setText(theMaterial.getMaterialType());
                descField.setText(theMaterial.getDescription());
                materialButton.setEnabled(true);
            }
        }
    }

    private class DatePickerPair {

        public JDatePicker thePicker;
        public JTextField theRef;
    }

    @Override
    public void clear() {
        clearAndHide();
    }

    @Override
    public JobItem getJob() {
        JobItem updatedJob = originalJob;

        Client theClient = updatedJob.getClient();
        Address theClientAddress = theClient.getAddress();
        City theClientCity = theClientAddress.getCity();
        Country theClientCountry = theClientCity.getCountry();
        StockItem theStockItem = updatedJob.getStockItem();

        theClient.setClientName(clientNameField.getText());
        theClientAddress.setAddress(addressField1.getText());
        theClientAddress.setAddress2(addressField2.getText());
        theClientCity.setCity(cityField.getText());
        theClientCountry.setCountry(countryField.getText());
        theClientAddress.setPostalCode(postCodeField.getText());

        DateModel<?> orderModel = theOrderDatePicker.thePicker.getModel();
        Date orderDate = new Date(orderModel.getYear() - 1900, orderModel.getMonth(), orderModel.getDay());
        theStockItem.setOrderedStockDate(orderDate);
        theStockItem.setOrderRef(theOrderDatePicker.theRef.getText());
        DateModel<?> receiptModel = theReceiptDatePicker.thePicker.getModel();
        Date receiptDate = new Date(receiptModel.getYear() - 1900, receiptModel.getMonth(), receiptModel.getDay());
        theStockItem.setReceivedStockDate(receiptDate);
        DateModel<?> templateModel = theTemplateDatePicker.thePicker.getModel();
        Date templateDate = new Date(templateModel.getYear() - 1900, templateModel.getMonth(), templateModel.getDay());
        updatedJob.setTemplateDate(templateDate);
        updatedJob.setTemplateRef(theTemplateDatePicker.theRef.getText());
        DateModel<?> estimateModel = theEstimateDatePicker.thePicker.getModel();
        Date estimateDate = new Date(estimateModel.getYear() - 1900, estimateModel.getMonth(), estimateModel.getDay());
        updatedJob.setEstimateDate(estimateDate);
        updatedJob.setEstimateRef(theEstimateDatePicker.theRef.getText());
        DateModel<?> invoiceModel = theInvoiceDatePicker.thePicker.getModel();
        Date invoiceDate = new Date(invoiceModel.getYear() - 1900, invoiceModel.getMonth(), invoiceModel.getDay());
        updatedJob.setInvoiceDate(invoiceDate);
        updatedJob.setInvoiceRef(theInvoiceDatePicker.theRef.getText());

        List<MaterialBatch> theBatches = theOrderModel.getOrderData();
        Set batchSet = new HashSet();

        for (MaterialBatch theBatch : theBatches) {
            batchSet.add(theBatch);
        }

        updatedJob.getStockItem().setMaterialBatchs(batchSet);

        updatedJob.setCustomerCompany((CustomerCompany) companyNameField.getSelectedItem());
        updatedJob.setNotes(notesField.getText());

        return updatedJob;
    }

    @Override
    public void reportRrror(String theError) {
        JOptionPane.showMessageDialog(
                JobDialog.this,
                theError,
                "Try again",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setJob(JobItem job) {
        settingFields = true;
        originalJob = job;

        Client theClient = originalJob.getClient();
        Address theClientAddress = theClient.getAddress();
        City theClientCity = theClientAddress.getCity();
        Country theClientCountry = theClientCity.getCountry();
        StockItem theStockItem = originalJob.getStockItem();

        clientNameField.setText(theClient.getClientName());
        addressField1.setText(theClientAddress.getAddress());
        addressField2.setText(theClientAddress.getAddress2());
        cityField.setText(theClientCity.getCity());
        countryField.setText(theClientCountry.getCountry());
        postCodeField.setText(theClientAddress.getPostalCode());

        if (originalJob.getCustomerCompany() != null) {
            CustomerCompany tmpCompany = null;
            CustomerCompany target = originalJob.getCustomerCompany();

            // todo change these to finds 
            int i = 0;
            for (CustomerCompany theCompany : theCompanies) {
                if (theCompany.getCustomerId() == target.getCustomerId()) {
                    tmpCompany = theCompanies.get(i);
                }
                ++i;
            }

            if (tmpCompany != null) {
                companyNameField.setSelectedItem(tmpCompany);
            }
        }

        if (theStockItem != null) {
            Set materialBatchs = theStockItem.getMaterialBatchs();
            Iterator batchIterator = materialBatchs.iterator();
            List<MaterialBatch> batchList = new ArrayList<>();
            List<BatchSheet> sheetList = new ArrayList<>();

            while (batchIterator.hasNext()) {
                MaterialBatch theBatch = (MaterialBatch) batchIterator.next();

                if (theBatch.isActive()) {
                    Iterator sheetIterator = theBatch.getBatchSheets().iterator();

                    while (sheetIterator.hasNext()) {
                        sheetList.add((BatchSheet) sheetIterator.next());
                    }

                    batchList.add(theBatch);
                }
            }

            theOrderModel.setData(batchList);
            theReceiptModel.setData(sheetList);

            if (batchList.size() > 0) {
                orderTable.changeSelection(0, 0, false, false);
                MaterialBatch theFirstBatch = batchList.get(0);
                Material theFirstMaterial = theFirstBatch.getMaterialSheet().getMaterial();
                materialTypeField.setText(theFirstMaterial.getMaterialType());
                materialNameField.setText(theFirstMaterial.getDescription());
                materialDetailsButton.setEnabled(true);
                receiptTable.changeSelection(0, 0, false, false);
            } else {
                materialTypeField.setText("");
                materialNameField.setText("");
                materialDetailsButton.setEnabled(false);
            }
        }

        if (theStockItem != null) {
            if (theStockItem.getOrderedStockDate() != null) {
                setDatePickerDate(theStockItem.getOrderedStockDate(), theOrderDatePicker.thePicker);
            }

            theOrderDatePicker.theRef.setText(theStockItem.getOrderRef());

            if (theStockItem.getReceivedStockDate() != null) {
                setDatePickerDate(theStockItem.getReceivedStockDate(), theReceiptDatePicker.thePicker);
            }
        }

        if (originalJob.getTemplateDate() != null) {
            setDatePickerDate(originalJob.getTemplateDate(), theTemplateDatePicker.thePicker);
        }

        theTemplateDatePicker.theRef.setText(originalJob.getTemplateRef());

        if (originalJob.getEstimateDate() != null) {
            setDatePickerDate(originalJob.getEstimateDate(), theEstimateDatePicker.thePicker);
        }

        theEstimateDatePicker.theRef.setText(originalJob.getEstimateRef());

        if (originalJob.getInvoiceDate() != null) {
            setDatePickerDate(originalJob.getInvoiceDate(), theInvoiceDatePicker.thePicker);
        }

        theInvoiceDatePicker.theRef.setText(originalJob.getInvoiceRef());

        notesField.setText(originalJob.getNotes());
        settingFields = false;
    }

    public void setController(JobItemController theController) {
        this.theController = theController;
        closeListener = new CloseListener(this.theController);
        closeButton.addActionListener(closeListener);
        saveListener = new SaveListener(this.theController, this);
        saveButton.addActionListener(saveListener);
        deleteListener = new DeleteListener(this.theController, this);
        deleteButton.addActionListener(deleteListener);
    }

    private JPanel createCustomerPanel() {
        theCompanies = theHelper.getCompanies(null);
        Object[] theModel = theCompanies.toArray();

        companyNameField = new JComboBox(theModel);
        companyNameField.setFont(textFont);
        companyNameField.setRenderer(new CustomerCompanyRenderer());
        companyNameField.addActionListener(this);

        companyDetailsButton = new JButton("Details...");
        companyDetailsButton.addActionListener(this);
        companyDetailsButton.setFont(textFont);

        clientNameField = new JTextField(10);
        clientNameField.setFont(textFont);
        clientNameField.getDocument().addDocumentListener(this);

        JPanel thePanel = new JPanel();
        Dimension theDim = new Dimension(280, 280);
        thePanel.setPreferredSize(theDim);
        thePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "customer", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        JPanel theCompanyPanel = new JPanel();
        Dimension theCompanyDim = new Dimension(270, 85);
        theCompanyPanel.setPreferredSize(theCompanyDim);
        theCompanyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "company", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        JPanel theClientPanel = new JPanel();
        Dimension theClientDim = new Dimension(270, 195);
        theClientPanel.setPreferredSize(theClientDim);
        theClientPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "end customer", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        theCompanyPanel.add(companyNameField);
        theCompanyPanel.add(companyDetailsButton);

        UIUtilities.addFormField("Name", clientNameField, theClientPanel, 0);

        JPanel addressPanel = createAddressPanel();
        Dimension theAddressDim = new Dimension(260, 150);
        addressPanel.setPreferredSize(theAddressDim);

        theClientPanel.add(addressPanel);

        thePanel.add(theCompanyPanel);
        thePanel.add(theClientPanel);

        return thePanel;
    }

    private JPanel createMiscPanel() {
        JPanel thePanel = new JPanel();
        Dimension theDim = new Dimension(250, 335);
        thePanel.setPreferredSize(theDim);
        thePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "misc", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        JPanel theTemplatesPanel = createDatePanel("template", theTemplateDatePicker, true);
        theTemplateDatePicker.thePicker.addActionListener(this);
        theTemplateDatePicker.theRef.getDocument().addDocumentListener(this);
        thePanel.add(theTemplatesPanel);

        JPanel theInvoicesPanel = createDatePanel("invoice", theInvoiceDatePicker, true);
        theInvoiceDatePicker.thePicker.addActionListener(this);
        theInvoiceDatePicker.theRef.getDocument().addDocumentListener(this);
        thePanel.add(theInvoicesPanel);

        JPanel theEstimatesPanel = createDatePanel("estimate", theEstimateDatePicker, true);
        theEstimateDatePicker.thePicker.addActionListener(this);
        theEstimateDatePicker.theRef.getDocument().addDocumentListener(this);
        thePanel.add(theEstimatesPanel);

        return thePanel;
    }

    private JPanel createMaterialPanel() {
        materialTypeField = new JTextField(30);
        materialTypeField.setFont(textFont);
        materialTypeField.setEditable(false);

        // todo - these shold be in another thread??
        // should use the data selected for the type and desc
        materialNameField = new JTextField(30);
        materialNameField.setEditable(false);
        materialNameField.setFont(textFont);

        ComponentPair materialTypeComponent = new ComponentPair("Type", materialTypeField);
        ComponentPair materialNameComponent = new ComponentPair("Name", materialNameField);

        List<ComponentPair> components = new ArrayList<>();
        components.add(materialTypeComponent);
        components.add(materialNameComponent);

        JPanel theSummaryPanel = UIUtilities.createCompactForm(null, components);

        JPanel thePanel = new JPanel();
        thePanel.setLayout(new BorderLayout());

        JPanel theButtonPanel = new JPanel();
        materialDetailsButton = new JButton("Material Details...");
        materialDetailsButton.addActionListener(this);
        materialDetailsButton.setFont(textFont);
        theButtonPanel.add(materialDetailsButton);

        thePanel.add(theSummaryPanel, BorderLayout.CENTER);
        thePanel.add(theButtonPanel, BorderLayout.EAST);

        return thePanel;
    }

    private JPanel createAddressPanel() {
        addressField1 = new JTextField(10);
        addressField1.setFont(textFont);
        addressField1.getDocument().addDocumentListener(this);
        addressField2 = new JTextField(10);
        addressField2.setFont(textFont);
        addressField2.getDocument().addDocumentListener(this);
        cityField = new JTextField(10);
        cityField.setFont(textFont);
        cityField.getDocument().addDocumentListener(this);
        countryField = new JTextField(10);
        countryField.setFont(textFont);
        countryField.getDocument().addDocumentListener(this);
        postCodeField = new JTextField(10);
        postCodeField.setFont(textFont);
        postCodeField.getDocument().addDocumentListener(this);

        ComponentPair address1Component = new ComponentPair("Line 1", addressField1);
        ComponentPair address2Component = new ComponentPair("Line 2", addressField2);
        ComponentPair cityComponent = new ComponentPair("City", cityField);
        ComponentPair countryComponent = new ComponentPair("Country", countryField);
        ComponentPair postCodeComponent = new ComponentPair("Post Code", postCodeField);

        List<ComponentPair> components = new ArrayList<>();
        components.add(address1Component);
        components.add(address2Component);
        components.add(cityComponent);
        components.add(countryComponent);
        components.add(postCodeComponent);

        JPanel thePanel = UIUtilities.createCompactForm("address", components);

        return thePanel;
    }

    private JPanel createDatePanel(String title, DatePickerPair thePair, boolean includeText) {
        thePair.thePicker = JDateComponentFactory.createJDatePicker();
        thePair.thePicker.setTextEditable(false);
        thePair.thePicker.setShowYearButtons(true);

        JComponent theDate = (JComponent) thePair.thePicker;
        Dimension theDateDim = new Dimension(120, 28);
        theDate.setPreferredSize(theDateDim);

        ComponentPair dateComponent = new ComponentPair("Date", theDate);

        List<ComponentPair> components = new ArrayList<>();

        if (includeText) {
            thePair.theRef = new JTextField(10);
            thePair.theRef.setFont(textFont);

            ComponentPair noComponent = new ComponentPair("Ref", thePair.theRef);
            components.add(noComponent);
        }

        components.add(dateComponent);

        JPanel thePanel = UIUtilities.createCompactForm(title, components);

        return thePanel;
    }

    private JPanel createStockPanel() {
        JPanel thePanel = new JPanel();
        Dimension theTemplatesDim = new Dimension(250, 250);
        thePanel.setLayout(new BorderLayout());
        thePanel.setPreferredSize(theTemplatesDim);

        JPanel theStockPanel = new JPanel();
        theStockPanel.setLayout(new GridLayout(1, 1));

        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = createImageIcon("images/middle.gif");

        JComponent orderPanel = createOrderPanel();
        tabbedPane.addTab("Order", icon, orderPanel,
                "Order details for job");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent receiptPanel = createReceiptPanel();
        tabbedPane.addTab("Receipt", icon, receiptPanel,
                "Receipt details for job");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        theStockPanel.add(tabbedPane);

        thePanel.add(theStockPanel, BorderLayout.CENTER);
        thePanel.add(materialPanel, BorderLayout.SOUTH);

        return thePanel;
    }

    private JPanel createOrderPanel() {
        theOrderDatePicker = new DatePickerPair();
        JPanel theDatePanel = createDatePanel("", theOrderDatePicker, true);
        theOrderDatePicker.theRef.getDocument().addDocumentListener(this);
        theOrderDatePicker.thePicker.addActionListener(this);

        JPanel theDateContainer = new JPanel();
        theDateContainer.setLayout(new BorderLayout());
        theDateContainer.add(theDatePanel, BorderLayout.WEST);

        theOrderModel = new SheetOrderTableModel();
        orderTable = new JTable(theOrderModel);
        orderTable.setFont(textFont);
        orderTable.setAutoCreateRowSorter(true);

        JButton addOrderButton = new JButton("Add");
        JButton deleteOrderButton = new JButton("Delete");

        addBatchListener = new AddBatchListener(this);
        deleteBatchListener = new DeleteBatchListener(this);
        addOrderButton.addActionListener(addBatchListener);
        deleteOrderButton.addActionListener(deleteBatchListener);

        JPanel thePanel = new JPanel();
        Dimension theDim = new Dimension(250, 200);
        thePanel.setPreferredSize(theDim);
        thePanel.setLayout(new BorderLayout());

        JScrollPane theScrollPane = new JScrollPane(orderTable);

        MouseListener materialListener = new MaterialListener(materialTypeField, materialNameField, materialDetailsButton);
        orderTable.addMouseListener(materialListener);

        thePanel.add(theDateContainer, BorderLayout.NORTH);
        thePanel.add(theScrollPane, BorderLayout.CENTER);

        JPanel theButtonPanel = new JPanel();
        theButtonPanel.add(addOrderButton);
        theButtonPanel.add(deleteOrderButton);

        thePanel.add(theButtonPanel, BorderLayout.SOUTH);

        return thePanel;
    }

    private JPanel createReceiptPanel() {
        theReceiptDatePicker = new DatePickerPair();
        JPanel theDatePanel = createDatePanel("", theReceiptDatePicker, false);
        theReceiptDatePicker.thePicker.addActionListener(this);

        theReceiptModel = new SheetReceiptTableModel();
        receiptTable = new JTable(theReceiptModel);
        theReceiptModel.addTableModelListener(this);
        receiptTable.setFont(textFont);
        receiptTable.setAutoCreateRowSorter(true);

        JPanel thePanel = new JPanel();
        Dimension theDim = new Dimension(250, 100);
        thePanel.setPreferredSize(theDim);
        thePanel.setLayout(new BorderLayout());
//        thePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "receipt", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        JScrollPane theScrollPane = new JScrollPane(receiptTable);

        thePanel.add(theDatePanel, BorderLayout.NORTH);
        thePanel.add(theScrollPane, BorderLayout.CENTER);

        JPanel theButtonPanel = new JPanel();
        thePanel.add(theButtonPanel, BorderLayout.SOUTH);

        return thePanel;
    }

    private JPanel createNotesPanel() {
        notesField = new JTextArea(15, 15);
        notesField.setFont(textFont);
        notesField.setEditable(true);
        notesField.getDocument().addDocumentListener(this);

        JScrollPane scrollPane = new JScrollPane(notesField);

        JPanel thePanel = new JPanel();
        Dimension theTemplatesDim = new Dimension(250, 250);
        thePanel.setPreferredSize(theTemplatesDim);
        thePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "notes", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        thePanel.add(scrollPane);

        return thePanel;
    }

    private void setDatePickerDate(Date theDate, JDatePicker thePicker) {
        if (theDate != null) {
            DateModel<?> theModel = thePicker.getModel();
            theModel.setSelected(true);
            Calendar theDateCal = new GregorianCalendar(TimeZone.getTimeZone("Europe/London"));
            theDateCal.setTime(theDate);
            theModel.setDate(theDateCal.get(Calendar.YEAR),
                    theDateCal.get(Calendar.MONTH),
                    theDateCal.get(Calendar.DAY_OF_MONTH));
        }
    }

    /**
     * This method clears the dialog and hides it.
     */
    public void clearAndHide() {
        dispose();
    }

    void addBatch() {
        StockItem stockItem = originalJob.getStockItem();
        MaterialBatchDialog addMaterialDialog = new MaterialBatchDialog(dd, "Material", theHelper, theTypes, stockItem);
        addMaterialDialog.pack();

        addMaterialDialog.setLocationRelativeTo(this);
        addMaterialDialog.setVisible(true);

        if (addMaterialDialog.isValid()) {
            MaterialBatch newBatch = addMaterialDialog.getBatch();
            double doubleQty = Double.parseDouble(newBatch.getQuantity());
            double dCeil = Math.ceil(doubleQty);
            long quantity = Math.round(dCeil);
            Date dateNow = new Date();
            String userId = "alan";
            newBatch.setActive(true);

            for (long i = 0; i < quantity; ++i) {
                BatchSheet newBatchSheet = new BatchSheet();
                newBatchSheet.setActive(true);
                newBatchSheet.setCreateDate(dateNow);
                newBatchSheet.setUserId(userId);
                newBatchSheet.setLastUpdate(dateNow);
                newBatchSheet.setMaterialBatch(newBatch);
                newBatchSheet.setBarCode("");

                newBatch.getBatchSheets().add(newBatchSheet);
                theReceiptModel.addReceipt(newBatchSheet);
            }

            theOrderModel.addBatch(newBatch);
            enableSaveDisableDelete();
        }
    }

    void deleteBatch() {
        int selectedRow = orderTable.getSelectedRow();

        if (selectedRow != -1) {
            MaterialBatch theBatch = theOrderModel.getItemAtRow(selectedRow);
            theOrderModel.deleteBatch(theBatch);
            boolean existsInDB = false;

            if (theBatch.getMaterialBatchId() != null) {
                theBatch.setActive(false);
                existsInDB = true;
            }

            Iterator sheetIter = theBatch.getBatchSheets().iterator();

            while (sheetIter.hasNext()) {
                BatchSheet theSheet = (BatchSheet) sheetIter.next();

                theReceiptModel.deleteReceipt(theSheet);

                if (existsInDB) {
                    theSheet.setActive(false);
                }
            }

            enableSaveDisableDelete();
            materialTypeField.setText("");
            materialNameField.setText("");
            materialDetailsButton.setEnabled(false);
        }
    }

    public void enableSaveDisableDelete() {
        if (!settingFields) {
            deleteButton.setEnabled(false);
            saveButton.setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == materialDetailsButton) {
            MaterialBatch selectedBatch = theOrderModel.getItemAt(orderTable.getSelectedRow());
            Material selectedMaterial = selectedBatch.getMaterialSheet().getMaterial();
            MaterialDialog materialDialog = new MaterialDialog(dd, "Material", theHelper);
            MaterialController theMaterialController = new MaterialController(selectedMaterial);

            theMaterialController.setUI(materialDialog);
            theMaterialController.setSessionHelper(null);
//            theMaterialController.setParentTableModel(getModel(), selectedRow);

            materialDialog.setController(theMaterialController);
            materialDialog.setMaterial(selectedMaterial);
            materialDialog.setReadOnly();
            materialDialog.pack();

            materialDialog.setLocationRelativeTo(this);
            materialDialog.setVisible(true);
        }

        if (e.getSource() == companyDetailsButton) {
            CustomerCompany selectedCompany = (CustomerCompany) companyNameField.getSelectedItem();
            CompanyDialog companyDialog = new CompanyDialog(dd, "Company", theHelper);
            CompanyController theCompanyController = new CompanyController(selectedCompany);
            theCompanyController.setUI(companyDialog);
            theCompanyController.setSessionHelper(null);
//        theController.setParentTableModel(getModel(), -1);
            companyDialog.setController(theCompanyController);
            companyDialog.setCompany(selectedCompany);
            companyDialog.pack();

            companyDialog.setReadOnly();
            companyDialog.setLocationRelativeTo(this);
            companyDialog.setVisible(true);
        }

        if (e.getSource() == companyNameField) {
            enableSaveDisableDelete();
        }

        if (e.getSource().getClass().getCanonicalName().contains("JDatePanelImpl")) {
            enableSaveDisableDelete();
        }
    }

    private class CustomerCompanyRenderer extends BasicComboBoxRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                CustomerCompany item = (CustomerCompany) value;
                String theName = item.getCompanyName();
                setText(theName);
            }

            if (index == -1) {
                CustomerCompany item = (CustomerCompany) value;
//                setText( "" + item.getCustomerId() );
                String theName = item.getCompanyName();
                setText(theName);
            }

            return this;
        }
    }

    private class MaterialRenderer extends BasicComboBoxRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                Material item = (Material) value;
                String desc = item.getDescription();

                if (desc.length() > 25) {
                    desc = desc.substring(0, 25);
                }

                setText(desc);
            }

            if (index == -1) {
                Material item = (Material) value;
//                setText( "" + item.getCustomerId() );
                String desc = item.getDescription();

                if (desc.length() > 25) {
                    desc = desc.substring(0, 25);
                }

                setText(desc);
            }

            return this;
        }
    }

    private JPanel createEastPanel() {
        JPanel thePanel = new JPanel();
        Dimension theTemplatesDim = new Dimension(250, 250);
        thePanel.setPreferredSize(theTemplatesDim);

        JPanel theStockPanel = new JPanel();
        theStockPanel.setLayout(new GridLayout(1, 1));

        JTabbedPane tabbedPane = new JTabbedPane();
        ImageIcon icon = createImageIcon("images/middle.gif");

        JPanel bigPanel = new JPanel();
        bigPanel.setLayout(new BorderLayout());
        bigPanel.add(miscPanel, BorderLayout.WEST);
        bigPanel.add(notesPanel, BorderLayout.EAST);

        JComponent orderPanel = bigPanel;
        tabbedPane.addTab("Misc", icon, orderPanel,
                "Miscellaneous details for Job");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

        JComponent receiptPanel = createStockPanel();
        tabbedPane.addTab("Stock", icon, receiptPanel,
                "Stock details for Job");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

        theStockPanel.add(tabbedPane);

        thePanel.add(theStockPanel, BorderLayout.CENTER);

        return thePanel;
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = JobDialog.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        enableSaveDisableDelete();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        enableSaveDisableDelete();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        enableSaveDisableDelete();
    }
}
