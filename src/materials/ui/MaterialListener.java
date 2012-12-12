package materials.ui;

import data.Material;
import java.util.EventListener;

public interface MaterialListener extends EventListener { 
  void materialStateChanged(Material theMaterial);
}