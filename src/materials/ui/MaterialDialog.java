/*
 */
package materials.ui;

import data.Material;
import data.MaterialSheet;
import gsl.DBHelper;
import gsl.ui.ComponentPair;
import gsl.ui.UIUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import materials.MaterialController;

public class MaterialDialog extends JDialog
        implements MaterialDisplayInterface,
        javax.swing.event.DocumentListener,
        ActionListener,
        TableModelListener {


    private JFrame dd;
    static Font textFont = new java.awt.Font("Tahoma", 0, 12);
    private final JPanel mainPanel;
    private final JPanel materialPanel;
    private final JPanel descriptionPanel;
    private final JPanel thicknessPanel;
    private final JPanel notesPanel;
    private Material originalMaterial = null;
    private JComboBox typeField;
    private JTextField descriptionField;
    private ThicknessTableModel theModel;
    private JTable thicknessTable;
    private MaterialController theController;
    private JButton closeButton = null;
    private JButton saveButton = null;
    private JButton deleteButton = null;
    private ActionListener closeListener = null;
    private ActionListener deleteListener = null;
    private ActionListener saveListener = null;
    private JButton addThicknessButton = null;
    private JButton deleteThicknessButton = null;
    private ActionListener addThicknessListener = null;
    private ActionListener deleteThicknessListener = null;
    private final DBHelper theHelper;
    private boolean settingFields;
    private boolean isReadOnly;
    private JTextArea notesField = null;
        
    /**
     * Creates the reusable dialog.
     */
    public MaterialDialog(JFrame aFrame, String aWord, DBHelper theHelper) {
        super(aFrame, true);
        dd = aFrame;
        this.setResizable(false);
        this.theHelper = theHelper;
        setLayout(new BorderLayout());
        isReadOnly = false;

        setTitle("Material - "); // todo set the company name

        mainPanel = new JPanel();
        Dimension mainDim = new Dimension(500, 320);
        mainPanel.setPreferredSize(mainDim);
        mainPanel.setLayout(new BorderLayout());

        descriptionPanel = createDescriptionPanel();
        thicknessPanel = createThicknessPanel();
        notesPanel = createNotesPanel();

        materialPanel = new JPanel();
        Dimension theDim = new Dimension(200, 300);
//        thePanel.setPreferredSize(theDim);
        materialPanel.setLayout(new BorderLayout());
        materialPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "material", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        materialPanel.add(descriptionPanel, BorderLayout.NORTH);
        materialPanel.add(thicknessPanel, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        JPanel buttonEastPanel = new JPanel();
        buttonEastPanel.setLayout(new BorderLayout());
        JPanel buttonWestPanel = new JPanel();
        buttonWestPanel.setLayout(new BorderLayout());

        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        deleteButton = new JButton("Delete");
        deleteButton.setEnabled(true);
        closeButton = new JButton("Close");

        buttonEastPanel.add(closeButton, BorderLayout.WEST);
        buttonWestPanel.add(deleteButton, BorderLayout.EAST);
        buttonWestPanel.add(saveButton, BorderLayout.WEST);

        buttonPanel.add(buttonEastPanel, BorderLayout.EAST);
        buttonPanel.add(buttonWestPanel, BorderLayout.WEST);

        mainPanel.add(materialPanel, BorderLayout.WEST);
        mainPanel.add(notesPanel, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                descriptionField.requestFocusInWindow();
            }
        });
    }
    
    private JPanel createDescriptionPanel() {
        List<String> typeStringsList = theHelper.getTypes();
        typeField = new JComboBox(typeStringsList.toArray());
        typeField.setFont(textFont);
        typeField.addActionListener(this);
        descriptionField = new JTextField(20);
        descriptionField.setFont(textFont);
        descriptionField.getDocument().addDocumentListener(this);

        ComponentPair typeComponent = new ComponentPair("Type", typeField);
        ComponentPair descriptionComponent = new ComponentPair("Description", descriptionField);

        List<ComponentPair> components = new ArrayList<>();
        components.add(typeComponent);
        components.add(descriptionComponent);

        JPanel thePanel = UIUtilities.createCompactForm("", components);

        return thePanel;
    }

    private JPanel createThicknessPanel() {
        theModel = new ThicknessTableModel();  
        theModel.addTableModelListener(this);
       
        thicknessTable = new JTable(theModel);
        thicknessTable.setFont(textFont);
        thicknessTable.setEnabled(true);
        thicknessTable.setAutoCreateRowSorter(true);
        
        TableColumn thicknessColumn = thicknessTable.getColumnModel().getColumn(0);
        JComboBox comboBox = new JComboBox();
        // todo - it should be an editable combo so that peeps can add to it
        List<String> theThicknesses = theHelper.getAllThicknesses();
        
        for(String theThickness: theThicknesses){      
            comboBox.addItem(theThickness);
        }
        
        thicknessColumn.setCellEditor(new DefaultCellEditor(comboBox));

        JPanel thePanel = new JPanel();
        Dimension theDim = new Dimension(220, 100);
        thePanel.setPreferredSize(theDim);
        thePanel.setLayout(new BorderLayout());
        thePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "thickneses", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        JScrollPane theScrollPane = new JScrollPane(thicknessTable);
        Dimension theScrollPaneDim = new Dimension(150, 165);
        theScrollPane.setPreferredSize(theScrollPaneDim);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), "", 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N
        
        addThicknessButton = new JButton("Add");
        addThicknessListener = new AddThicknessListener(this);
        addThicknessButton.addActionListener(addThicknessListener);

        deleteThicknessButton = new JButton("Delete");
        deleteThicknessListener = new DeleteThicknessListener(this);
        deleteThicknessButton.addActionListener(deleteThicknessListener);
        
        buttonPanel.add(addThicknessButton, BorderLayout.WEST);
        buttonPanel.add(deleteThicknessButton, BorderLayout.EAST);

        thePanel.add(theScrollPane, BorderLayout.NORTH);
        thePanel.add(buttonPanel, BorderLayout.SOUTH);

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

    @Override
    public void clear() {
        clearAndHide();
    }

    @Override
    public Material getMaterial() {
        Material updatedMaterial = originalMaterial;

        updatedMaterial.setMaterialType((String)typeField.getSelectedItem());    // todo need to find the index from the value
        updatedMaterial.setDescription(descriptionField.getText());

        Date dateNow = new Date();
        String userId = "alan";

        List<MaterialSheet> thicknessData = theModel.getData();
        Set materialSheets = new HashSet();

        for (MaterialSheet theSheet : thicknessData) {
            theSheet.setUserId(userId);
            theSheet.setLastUpdate(dateNow);
            materialSheets.add(theSheet);
        }

        updatedMaterial.setMaterialSheets(materialSheets);
        
        updatedMaterial.setUserId(userId);
        updatedMaterial.setLastUpdate(dateNow);
        
        updatedMaterial.setNotes(notesField.getText());

        return updatedMaterial;
    }

    @Override
    public void reportRrror(String theError) {
        JOptionPane.showMessageDialog(
                MaterialDialog.this,
                theError,
                "Try again",
                JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setMaterial(Material material) {
        settingFields = true;
        
        if(material.getMaterialId() == null){
            setTitle("Material - New");            
        } else {
            String title = "Material - " + material.getDescription();
            setTitle(title);                        
        }
        
        originalMaterial = material;

        Set materialSheets = originalMaterial.getMaterialSheets();

        typeField.setSelectedItem(originalMaterial.getMaterialType());    
        descriptionField.setText(originalMaterial.getDescription());

        List<MaterialSheet> thicknessData = new ArrayList<>();
        Iterator it = materialSheets.iterator();

        while (it.hasNext()) {
            MaterialSheet theSheet = (MaterialSheet) it.next();
            thicknessData.add(theSheet);
        }

        theModel.setData(thicknessData);
        notesField.setText(originalMaterial.getNotes());
        
        settingFields = false;
    }
    
    public void setController(MaterialController theController) {
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
        addThicknessButton.setEnabled(false);
        deleteThicknessButton.setEnabled(false);
    }

    void addThickness() {
        MaterialSheet newSheet = createNewSheet(originalMaterial);
        newSheet.setActive(true);
        theModel.addThickness(newSheet);
        enableSaveDisableDelete();
    }

    void deleteThickness() {
        int selectedRow = thicknessTable.getSelectedRow();
        
        if(selectedRow != -1){
            MaterialSheet theSheet = theModel.getItemAtRow(selectedRow);
            theModel.deleteThickness(theSheet);
            
            if (theSheet.getMaterialSheetId() != null) {
                theSheet.setActive(false);
            }
            
            enableSaveDisableDelete();
        }
    }

    /**
     * This method clears the dialog and hides it.
     */
    public void clearAndHide() {
        dispose();
    }
    
    private MaterialSheet createNewSheet(Material theMaterial) {
        Date dateNow = new Date();
        String userId = "alan";
        MaterialSheet newSheet = new MaterialSheet();
        newSheet.setCreateDate(dateNow);
        newSheet.setUserId(userId);
        newSheet.setLastUpdate(dateNow);
        newSheet.setMaterial(theMaterial);

        return newSheet;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == typeField){
            enableSaveDisableDelete();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        enableSaveDisableDelete();
    }
}