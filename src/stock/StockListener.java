package stock;

import data.JobItem;
import java.util.EventListener;

public interface StockListener extends EventListener { 
  void itemStateChanged(JobItem item);
}