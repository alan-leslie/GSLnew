/*
 */
package companies.ui;

import companies.CompanyController;
import companies.CompanyControllerInterface;
import gsl.DBHelper;
import data.Address;
import data.City;
import data.Contact;
import data.Country;
import data.CustomerCompany;
import gsl.ui.ComponentPair;
import gsl.ui.UIUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;

public class CompanyDialog extends JDialog
        implements CompanyDisplayInterface,
        javax.swing.event.DocumentListener {

    private CompanyControllerInterface theController = null;
    private JFrame dd;
    static Font textFont = new java.awt.Font("Tahoma", 0, 12);
    private final JPanel mainPanel;
    private final JPanel companyPanel;
    private final JPanel namePanel;
    private final JPanel addressPanel;
    private final JPanel contactPanel;
    private final JPanel notesPanel;
    private JTextField companyNameField;
    private JTextField firstNameField = null;
    private JTextField lastNameField = null;
    private JTextField phoneNumberField = null;
    private JTextField emailField = null;
    private JTextField addressField1 = null;
    private JTextField addressField2 = null;
    private JTextField cityField = null;
    private JTextField countryField = null;
    private JTextField postCodeField = null;
    private JTextArea notesField = null;
    private CustomerCompany originalCompany = null;
    private JButton closeButton = null;
    private JButton saveButton = null;
    private JButton deleteButton = null;
    private ActionListener closeListener = null;
    private ActionListener deleteListener = null;
    private ActionListener saveListener = null;
    private final DBHelper theHelper;
    private boolean settingFields = false;
    private boolean isReadOnly;

    @Override
    public void clear() {
        clearAndHide();
    }

    /**
     * Creates the reusable dialog.
     */
    public CompanyDialog(JFrame parent, String aWord, DBHelper theHelper) {
        super(parent, true);
        dd = parent;
        setLayout(new BorderLayout());
        this.setResizable(false);
        this.theHelper = theHelper;
        isReadOnly = false;

        setTitle("Company - ");  // todo set the company name
        setResizable(false);

        mainPanel = new JPanel();
        Dimension mainDim = new Dimension(450, 340);
        mainPanel.setPreferredSize(mainDim);
        mainPanel.setLayout(new BorderLayout());

        namePanel = createNamePanel();
        contactPanel = createContactPanel();
        addressPanel = createAddressPanel();
        notesPanel = createNotesPanel();

        companyPanel = new JPanel();
        Dimension theDim = new Dimension(200, 300);
//        thePanel.setPreferredSize(theDim);
        companyPanel.setLayout(new BorderLayout());
        companyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "company", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        companyPanel.add(namePanel, BorderLayout.NORTH);
        companyPanel.add(contactPanel, BorderLayout.CENTER);
        companyPanel.add(addressPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        JPanel buttonEastPanel = new JPanel();
        buttonEastPanel.setLayout(new BorderLayout());
        JPanel buttonWestPanel = new JPanel();
        buttonWestPanel.setLayout(new BorderLayout());

        saveButton = new JButton("Save");
        saveButton.addActionListener(saveListener);
        saveButton.setEnabled(false);
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(deleteListener);
        deleteButton.setEnabled(true);
        closeButton = new JButton("Close");

        buttonEastPanel.add(closeButton, BorderLayout.WEST);
        buttonWestPanel.add(deleteButton, BorderLayout.EAST);
        buttonWestPanel.add(saveButton, BorderLayout.WEST);

        buttonPanel.add(buttonEastPanel, BorderLayout.EAST);
        buttonPanel.add(buttonWestPanel, BorderLayout.WEST);

        mainPanel.add(companyPanel, BorderLayout.WEST);
        mainPanel.add(notesPanel, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        //Ensure the name field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                companyNameField.requestFocusInWindow();
            }
        });
    }

    public void setController(CompanyController theController) {
        this.theController = theController;
        closeListener = new CloseListener(this.theController);
        closeButton.addActionListener(closeListener);
        saveListener = new SaveListener(this.theController, this);
        saveButton.addActionListener(saveListener);
        deleteListener = new DeleteListener(this.theController, this);
        deleteButton.addActionListener(deleteListener);
    }

    public void setReadOnly() {
        isReadOnly = true;
        saveButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private JPanel createNamePanel() {
        companyNameField = new JTextField(20);
        companyNameField.setFont(textFont);        
        companyNameField.getDocument().addDocumentListener(this);

        JPanel thePanel = new JPanel();
        Dimension theDim = new Dimension(250, 40);
        thePanel.setPreferredSize(theDim);
        thePanel.setLayout(new GridLayout(1, 1));
        thePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "name", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        thePanel.add(companyNameField);

        return thePanel;
    }

    private JPanel createContactPanel() {
        firstNameField = new JTextField(10);
        firstNameField.setFont(textFont);
        firstNameField.getDocument().addDocumentListener(this);
        lastNameField = new JTextField(10);
        lastNameField.setFont(textFont);
        lastNameField.getDocument().addDocumentListener(this);
        phoneNumberField = new JTextField(10);
        phoneNumberField.setFont(textFont);
        phoneNumberField.getDocument().addDocumentListener(this);
        emailField = new JTextField(10);
        emailField.setFont(textFont);
        emailField.getDocument().addDocumentListener(this);

        ComponentPair firstNameComponent = new ComponentPair("First Name", firstNameField);
        ComponentPair lastNameComponent = new ComponentPair("Last Name", lastNameField);
        ComponentPair phoneNumberComponent = new ComponentPair("Phone", phoneNumberField);
        ComponentPair emailComponent = new ComponentPair("e-mail", emailField);

        List<ComponentPair> components = new ArrayList<>();
        components.add(firstNameComponent);
        components.add(lastNameComponent);
        components.add(phoneNumberComponent);
        components.add(emailComponent);

        JPanel thePanel = UIUtilities.createCompactForm("contact", components);

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

        ComponentPair address1Component = new ComponentPair("Address Line 1", addressField1);
        ComponentPair address2Component = new ComponentPair("Address Line 2", addressField2);
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

    private JPanel createNotesPanel() {
        notesField = new JTextArea(15, 15);
        notesField.getDocument().addDocumentListener(this);
        notesField.setFont(textFont);
        notesField.setEditable(true);

        JScrollPane scrollPane = new JScrollPane(notesField);

        JPanel thePanel = new JPanel();
        Dimension theTemplatesDim = new Dimension(190, 80);
        thePanel.setPreferredSize(theTemplatesDim);
        thePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "notes", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        thePanel.add(scrollPane);

        return thePanel;
    }

    /**
     * This method clears the dialog and hides it.
     */
    public void clearAndHide() {
        dispose();
    }
    
    @Override
    public CustomerCompany getCompany() {
        CustomerCompany updatedCompany = originalCompany;

        Contact theContact = updatedCompany.getContact();
        Address theAddress = updatedCompany.getAddress();
        City theCity = theAddress.getCity();
        Country theCountry = theCity.getCountry();

        updatedCompany.setCompanyName(companyNameField.getText());
        theContact.setContactFirstName(firstNameField.getText());
        theContact.setContactLastName(lastNameField.getText());
        theContact.setContactPhoneNumber(phoneNumberField.getText());
        theContact.setContactEmail(emailField.getText());
        theAddress.setAddress(addressField1.getText());
        theAddress.setAddress2(addressField2.getText());
        theCity.setCity(cityField.getText());
        theCountry.setCountry(countryField.getText());
        theAddress.setPostalCode(postCodeField.getText());
        updatedCompany.setNotes(notesField.getText());

        return updatedCompany;
    }

    @Override
    public void reportRrror(String theError) {
        JOptionPane.showMessageDialog(
                CompanyDialog.this,
                theError,
                "Try again",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setCompany(CustomerCompany company) {
        settingFields = true;
        
        if(company.getCustomerId() == null){
            setTitle("Company - New");            
        } else {
            String title = "Company - " + company.getCompanyName();
            setTitle(title);                        
        }

        originalCompany = company;

        companyNameField.setText(originalCompany.getCompanyName());
        firstNameField.setText(originalCompany.getContact().getContactFirstName());
        lastNameField.setText(originalCompany.getContact().getContactLastName());
        phoneNumberField.setText(originalCompany.getContact().getContactPhoneNumber());
        emailField.setText(originalCompany.getContact().getContactEmail());
        addressField1.setText(originalCompany.getAddress().getAddress());
        addressField2.setText(originalCompany.getAddress().getAddress2());
        cityField.setText(originalCompany.getAddress().getCity().getCity());
        countryField.setText(originalCompany.getAddress().getCity().getCountry().getCountry());
        postCodeField.setText(originalCompany.getAddress().getPostalCode());
        notesField.setText(originalCompany.getNotes());
        settingFields = false;
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
    
    private void enableSaveDisableDelete() {
       if(!settingFields && !isReadOnly){
            deleteButton.setEnabled(false);
            saveButton.setEnabled(true);
        }
    }    

    @Override
    public void disableSave() {
        saveButton.setEnabled(false);
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
    public void enableSave() {
        saveButton.setEnabled(true);
    }
}
