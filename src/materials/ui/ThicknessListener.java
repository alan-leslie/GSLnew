package materials.ui;

import data.MaterialSheet;
import java.util.EventListener;

public interface ThicknessListener extends EventListener { 
  void thicknessStateChanged(MaterialSheet theMaterial);
}
