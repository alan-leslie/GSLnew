package jobs;

import data.JobItem;
import java.util.EventListener;

public interface JobListener extends EventListener { 
  void jobStateChanged(JobItem item);
}