
package jobs;

/**
 *
 * @author alan
 */
public class JobNumber implements Comparable<JobNumber> {
    private String yearNumber;
    private String jobIndex;
    
    public JobNumber(String year, String index){
        yearNumber = year;
        jobIndex = index;
    }
    
    @Override
    public String toString(){
        return jobIndex + "/" + yearNumber;      
    }  

    @Override
    public int compareTo(JobNumber other) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
        if(this == other){
            return EQUAL;
        }
        
        if(Integer.parseInt(yearNumber) < Integer.parseInt(other.yearNumber)){
            return BEFORE;
        }
        
        if(Integer.parseInt(yearNumber) > Integer.parseInt(other.yearNumber)){
            return AFTER;
        }
        
        if(Integer.parseInt(jobIndex) < Integer.parseInt(other.jobIndex)){
            return BEFORE;
        } 
        
        if(Integer.parseInt(jobIndex) > Integer.parseInt(other.jobIndex)){
            return AFTER;
        } 
        
        return EQUAL;
    }
}
