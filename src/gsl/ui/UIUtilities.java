
package gsl.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

/**
 *
 * @author alan
 */
public class UIUtilities {
   public static JPanel createCompactForm(String title, java.util.List<ComponentPair> components) {
        JPanel thePanel = new JPanel();
        thePanel.setLayout(new SpringLayout());
        thePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)), title, 0, 0, new java.awt.Font("Aharoni", 0, 12))); // NOI18N

        for (ComponentPair thePair : components) {
            JLabel theLabel = new JLabel(thePair.getLabel(), JLabel.TRAILING);
            thePanel.add(theLabel);
            theLabel.setLabelFor(thePair.getField());
            thePanel.add(thePair.getField());
        }

        SpringUtilities.makeCompactGrid(thePanel,
                components.size(), 2, //rows, cols
                6, 6, //initX, initY
                6, 6);       //xPad, yPad

        return thePanel;
    }   
   
    public static void addFormField(String fieldName, Component field, JPanel thePanel, int yIndex) {
        JLabel theLabel = new JLabel(fieldName);

        GridBagConstraints gridBagConstraintsx01 = new GridBagConstraints();
        gridBagConstraintsx01.gridx = 0;
        gridBagConstraintsx01.gridy = yIndex;
        gridBagConstraintsx01.insets = new Insets(5, 5, 5, 5);
        thePanel.add(theLabel, gridBagConstraintsx01);

        GridBagConstraints gridBagConstraintsx02 = new GridBagConstraints();
        gridBagConstraintsx02.gridx = 1;
        gridBagConstraintsx02.gridy = yIndex;
        gridBagConstraintsx02.insets = new Insets(5, 5, 5, 5);
        gridBagConstraintsx02.gridwidth = 2;
        gridBagConstraintsx02.fill = GridBagConstraints.BOTH;
        thePanel.add(field, gridBagConstraintsx02);
    }
}
