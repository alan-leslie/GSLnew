package jobs.ui;

import data.MaterialBatch;
import java.util.EventListener;

public interface MaterialBatchListener extends EventListener { 
  void batchStateChanged(MaterialBatch theBatch);
}
