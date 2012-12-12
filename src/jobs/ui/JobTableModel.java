package jobs.ui;

import data.JobItem;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import jobs.Defect;
import jobs.JobListener;

public class JobTableModel extends AbstractTableModel implements JobListener {

    private ArrayList<JobItem> jobs = new ArrayList<>();

    public void setData(List<JobItem> newData) {
        jobs = new ArrayList<>(newData);
        this.fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return JobColumn.values().length;
    }

    @Override
    public int getRowCount() {
        return jobs.size();
    }

    @Override
    public String getColumnName(int column) {
        return JobColumn.at(column).name;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return JobColumn.at(columnIndex).valueIn(jobs.get(rowIndex));
    }

    @Override
     public void jobStateChanged(JobItem item){
        for (int i = 0; i < jobs.size(); i++) {
            JobItem other = jobs.get(i);
            if (item.getJobId() == other.getJobId()) {
                jobs.set(i, item);
                fireTableRowsUpdated(i, i);
                return;
            }
        }
        throw new Defect("No existing state for " + item.getJobId());
    }
    
    public void addJob(JobItem newJob) {
        jobs.add(newJob);
        int row = jobs.size() - 1;
        fireTableRowsInserted(row, row);
    }
    
    JobItem getItem(int row) {
        return jobs.get(row);
    }

    public void deleteJob(JobItem theJob) {
        int theRow = jobs.indexOf(theJob);

        if (theRow > -1) {
            jobs.remove(theJob);
            fireTableRowsDeleted(theRow, theRow);
        }
    }
}
