package materials.ui;

import data.Material;

public enum MaterialColumn { 
    ITEM_TYPE("Material Type") { 
    @Override public Object valueIn(Material material) { 
      return material.getMaterialType(); 
    } 
  }, 
  DESCRIPTION("Description") { 
    @Override public Object valueIn(Material material) { 
      return material.getDescription();   
    }  
  }; 
  
  abstract public Object valueIn(Material snapshot); 
  
  public final String name;
  
  private MaterialColumn(String name) { 
    this.name = name; 
  } 

  public static MaterialColumn at(int offset) { return values()[offset]; } 
}
