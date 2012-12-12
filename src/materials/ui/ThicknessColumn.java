
package materials.ui;

import data.MaterialSheet;

/**
 *
 * @author alan
 */

public enum ThicknessColumn { 
    THICKNESS("Thickness") { 
    @Override public Object valueIn(MaterialSheet theSheet) { 
      return theSheet.getThickness(); 
    } 
    
    @Override public void setValueAt(Object value, MaterialSheet theSheet) {
        theSheet.setThickness((String)value);
    } 
  }, 
  PRICE("Price") { 
    @Override public Object valueIn(MaterialSheet theSheet) { 
      return theSheet.getPrice();   
    }
    
    @Override public void setValueAt(Object value, MaterialSheet theSheet) {
        theSheet.setPrice((String)value);
    } 
  }; 
  
  abstract public Object valueIn(MaterialSheet snapshot); 
  abstract public void setValueAt(Object value, MaterialSheet snapshot); 
  
  public final String name;
  
  private ThicknessColumn(String name) { 
    this.name = name; 
  } 

  public static ThicknessColumn at(int offset) { return values()[offset]; } 
}
