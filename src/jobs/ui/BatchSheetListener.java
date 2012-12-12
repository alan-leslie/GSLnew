package jobs.ui;

import java.util.EventListener;
import data.BatchSheet;

public interface BatchSheetListener extends EventListener { 
  void batchSheetStateChanged(BatchSheet theSheet);
}
