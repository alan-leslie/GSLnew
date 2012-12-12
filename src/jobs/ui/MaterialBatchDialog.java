/*
 */
package jobs.ui;

import data.Material;
import data.MaterialBatch;
import data.MaterialSheet;
import data.StockItem;
import gsl.ui.ComponentPair;
import gsl.ui.UIUtilities;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import gsl.DBHelper;

class MaterialBatchDialog extends JDialog
        implements ActionListener {

    private JFrame dd;
    static Font textFont = new java.awt.Font("Tahoma", 0, 12);
    private final JPanel mainPanel;
    private final JPanel descriptionPanel;
    private JComboBox typeField;
    private JComboBox descriptionField;
    private JComboBox thicknessField;
    private JTextField quantityField;
    private JButton cancelButton = null;
    private JButton okButton = null;
    private final DBHelper theHelper;
    private final Object[] typeStrings;
    private boolean dataIsValid = false;
    private boolean settingType = false;
    private StockItem parentItem = null;

    /**
     * Creates the reusable dialog.
     */
    public MaterialBatchDialog(JFrame aFrame, String aWord,
            DBHelper theHelper, List<String> theTypes, StockItem parentItem) {
        super(aFrame, true);
        dd = aFrame;
        this.setResizable(false);
        this.theHelper = theHelper;
        typeStrings = theTypes.toArray();
        settingType = false;
        this.parentItem = parentItem;
        setLayout(new BorderLayout());

        setTitle("Material Batch - New");

        mainPanel = new JPanel();
        Dimension mainDim = new Dimension(300, 150);
        mainPanel.setPreferredSize(mainDim);
        mainPanel.setLayout(new BorderLayout());

        descriptionPanel = createDescriptionPanel();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        JPanel buttonEastPanel = new JPanel();
        buttonEastPanel.setLayout(new BorderLayout());
        JPanel buttonWestPanel = new JPanel();
        buttonWestPanel.setLayout(new BorderLayout());

        okButton = new JButton("OK");
        cancelButton = new JButton("Close");
        
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);

        buttonEastPanel.add(cancelButton, BorderLayout.WEST);
        buttonWestPanel.add(okButton, BorderLayout.WEST);

        buttonPanel.add(buttonEastPanel, BorderLayout.EAST);
        buttonPanel.add(buttonWestPanel, BorderLayout.WEST);

        mainPanel.add(descriptionPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                typeField.requestFocusInWindow();
            }
        });
    }

    private JPanel createDescriptionPanel() {
        typeField = new JComboBox(typeStrings);
        typeField.setFont(textFont);
        typeField.setSelectedIndex(0);
        typeField.addActionListener(this);
        String theType = (String) typeField.getSelectedItem();

        List<Material> theMaterials = theHelper.getMaterials(theType);
        Object[] theMaterialModel = theMaterials.toArray();
        Material theFirstMaterial = theMaterials.get(0);
        String theFirstDescription = theFirstMaterial.getDescription();

        descriptionField = new JComboBox(theMaterialModel);
        descriptionField.setFont(textFont);
        descriptionField.setRenderer(new MaterialRenderer());
        descriptionField.setSelectedIndex(0);
        descriptionField.setFont(textFont);
        descriptionField.addActionListener(this);

        Material theMaterial = (Material) descriptionField.getSelectedItem();
        String desc = theMaterial.getDescription();

        List<MaterialSheet> theSheets = theHelper.getMaterialSheets(theType, desc, null);
        Object[] theThicknessModel = theSheets.toArray();

        thicknessField = new JComboBox(theThicknessModel);
        thicknessField.setFont(textFont);
        thicknessField.setRenderer(new ThicknessRenderer());
        thicknessField.setSelectedIndex(0);

        NumberFormat amountFormat = NumberFormat.getNumberInstance();
        quantityField = new JFormattedTextField(amountFormat);
        quantityField.setFont(textFont);
        quantityField.setText("1");

        ComponentPair typeComponent = new ComponentPair("Type", typeField);
        ComponentPair descriptionComponent = new ComponentPair("Description", descriptionField);
        ComponentPair thicknessComponent = new ComponentPair("Thickness", thicknessField);
        ComponentPair quantityComponent = new ComponentPair("Quantity", quantityField);

        List<ComponentPair> components = new ArrayList<>();
        components.add(typeComponent);
        components.add(descriptionComponent);
        components.add(thicknessComponent);
        components.add(quantityComponent);

        JPanel thePanel = UIUtilities.createCompactForm("", components);

        return thePanel;
    }

//    @Override
    public void clear() {
        clearAndHide();
    }

    /**
     * This method clears the dialog and hides it.
     */
    public void clearAndHide() {
        dispose();
    }

    MaterialBatch getBatch() {
        MaterialBatch newBatch = createNewBatch(parentItem);
        newBatch.setMaterialSheet((MaterialSheet) thicknessField.getSelectedItem());
        newBatch.setQuantity(quantityField.getText());
        return newBatch;
    }

    private MaterialBatch createNewBatch(StockItem theStockItem) {
        Date dateNow = new Date();
        String userId = "alan";
        MaterialBatch newBatch = new MaterialBatch();
        newBatch.setCreateDate(dateNow);
        newBatch.setUserId(userId);
        newBatch.setLastUpdate(dateNow);       
        newBatch.setStockItem(theStockItem);

        return newBatch;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == typeField) {
            settingType = true;
            String theType = (String) typeField.getSelectedItem();
            List<Material> theMaterials = theHelper.getMaterials(theType);

            DefaultComboBoxModel materialModel = (DefaultComboBoxModel) descriptionField.getModel();
            materialModel.removeAllElements();

            for (Material theMaterial : theMaterials) {
                materialModel.addElement(theMaterial);
            }

            Material theSelectedMaterial = (Material) theMaterials.get(0);
            String theFirstDescription = theSelectedMaterial.getDescription();

            List<MaterialSheet> theSheets = theHelper.getMaterialSheets(theType, theFirstDescription, null);

            DefaultComboBoxModel thicknessModel = (DefaultComboBoxModel) thicknessField.getModel();
            thicknessModel.removeAllElements();

            for (MaterialSheet theMaterialSheet : theSheets) {
                thicknessModel.addElement(theMaterialSheet);
            }
            
            settingType = false;
        }

        if (e.getSource() == descriptionField && !settingType) {
            String theType = (String) typeField.getSelectedItem();
            Material theMaterial = (Material)descriptionField.getSelectedItem();
            String theDescription = theMaterial.getDescription();

            List<MaterialSheet> theSheets = theHelper.getMaterialSheets(theType, theDescription, null);

            DefaultComboBoxModel thicknessModel = (DefaultComboBoxModel) thicknessField.getModel();
            thicknessModel.removeAllElements();

            for (MaterialSheet theMaterialSheet : theSheets) {
                thicknessModel.addElement(theMaterialSheet);
            }
        }

        if (e.getSource() == okButton) {
            checkValid();

            if (isValid()) {
                dispose();
            }
        }

        if (e.getSource() == cancelButton) {
            dispose();
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

    public boolean isValid() {
        return dataIsValid;
    }

    private void checkValid() {
        String quantity = quantityField.getText();

        if (!quantity.isEmpty()) {
            Object theSheet = thicknessField.getSelectedItem();

            if (theSheet != null) {
                dataIsValid = true;
            } else {
                JOptionPane.showMessageDialog(this, "hello", "there", JOptionPane.ERROR_MESSAGE);// option pane
            }
        } else {
            JOptionPane.showMessageDialog(this, "Quantity cannot be 0", "Invalid input", JOptionPane.ERROR_MESSAGE);// option pane
        }
    }
    
    private class ThicknessRenderer extends BasicComboBoxRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            if (value != null) {
                MaterialSheet item = (MaterialSheet) value;
                setText(item.getThickness());
            }

            if (index == -1) {
                MaterialSheet item = (MaterialSheet) value;
//                setText( "" + item.getCustomerId() );
                setText(item.getThickness());
            }

            return this;
        }
    }
}