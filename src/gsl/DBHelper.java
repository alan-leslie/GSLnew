package gsl;

import data.BatchSheet;
import data.CustomerCompany;
import data.JobItem;
import data.Material;
import data.MaterialBatch;
import data.MaterialSheet;
import data.StockItem;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import jobs.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author alan
 */
public class DBHelper {

    Session session = null;

    public DBHelper() {
//        this.session = HibernateUtil.getSessionFactory().getCurrentSession();
        this.session = HibernateUtil.getSessionFactory().openSession();
    }

    public String getNextJobName() {
        List<String> jobNames = null;
        Calendar theDateCal = new GregorianCalendar(TimeZone.getTimeZone("Europe/London"));
        int thisYear = theDateCal.get(Calendar.YEAR);
        String strYear = Integer.toString(thisYear).substring(2);
        String jobYear = "/" + strYear;
        String jobNameBit = "%" + jobYear;
        
        try {
            StringBuilder queryBuilder = new StringBuilder("select jobName ");
            queryBuilder.append("from JobItem as jobs ");
            queryBuilder.append("where jobs.jobName like :jobNameBit ");
            queryBuilder.append("order by jobs.jobName ");

            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString()).setParameter("jobNameBit", jobNameBit);
            jobNames = (List<String>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        int highestNo = 0;
        for(String theJobName:jobNames){
            if(theJobName.contains(jobYear)){
                String strJobNo = theJobName.substring(0, theJobName.length() - 3);
                int jobNo = Integer.parseInt(strJobNo);
                highestNo = Math.max(highestNo, jobNo);
            }       
        }
        
        String newJobNo = Integer.toString(highestNo + 1);
        
        return newJobNo + jobYear;
    }
        
    public List<JobItem> getJobs() {
        List<JobItem> jobs = null;
        try {
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery("from JobItem");
            jobs = (List<JobItem>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobs;
    }
    
    public List<JobItem> getOpenJobs() {
        List<JobItem> jobs = null;
        try {
            StringBuilder queryBuilder = new StringBuilder("from JobItem as jobs ");
            queryBuilder.append("where jobs.invoiceRef = '' ");
            queryBuilder.append("and jobs.active is true ");

            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString());
            jobs = (List<JobItem>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobs;
    }
    
    public List<JobItem> getJobsForCompanyName(String companyName) {
        List<JobItem> jobs = null;
        try {
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery("from JobItem");
            jobs = (List<JobItem>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        List<JobItem> matchingJobs = new ArrayList<>();
        
        for(JobItem theJob:jobs){
            if(theJob.getCustomerCompany().getCompanyName().contains(companyName)){
                matchingJobs.add(theJob);
            }
        }
        
        return matchingJobs;
    }  
    
    public List<JobItem> getJobsForMaterialType(String materialTypeBit) {
        List<JobItem> jobs = null;
        try {
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery("from JobItem");
            jobs = (List<JobItem>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        List<JobItem> matchingJobs = new ArrayList<>();
        
        for(JobItem theJob:jobs){
            StockItem theStockItem = (StockItem) (theJob.getStockItem());
            
            if(theStockItem != null){
                Set materialBatchs = theStockItem.getMaterialBatchs();
                Iterator batchIterator = materialBatchs.iterator();
                boolean found = false;

                while(batchIterator.hasNext() && !found){
                    MaterialBatch theFirstBatch = (MaterialBatch)batchIterator.next();
                    MaterialSheet theFirstMaterialSheet = theFirstBatch.getMaterialSheet();
                        
                    if(theFirstMaterialSheet != null){
                        Material theMaterial = theFirstMaterialSheet.getMaterial();
                        
                        if(theMaterial != null){
                            if(theMaterial.getMaterialType().contains(materialTypeBit)){
                                matchingJobs.add(theJob);
                                found = true;
                            }                          
                        }
                    }
                }
            }                     
        }
        
        return matchingJobs;
    }  

    public List<JobItem> getJobsForMaterialDesc(String materialDesc) {
        List<JobItem> jobs = null;
        try {
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery("from JobItem");
            jobs = (List<JobItem>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        List<JobItem> matchingJobs = new ArrayList<>();
        
        for(JobItem theJob:jobs){
            StockItem theStockItem = (StockItem) (theJob.getStockItem());
            
            if(theStockItem != null){
                Set materialBatchs = theStockItem.getMaterialBatchs();
                Iterator batchIterator = materialBatchs.iterator();
                boolean found = false;

                while(batchIterator.hasNext() && !found){
                    MaterialBatch theFirstBatch = (MaterialBatch)batchIterator.next();
                    MaterialSheet theFirstMaterialSheet = theFirstBatch.getMaterialSheet();
                        
                    if(theFirstMaterialSheet != null){
                        Material theMaterial = theFirstMaterialSheet.getMaterial();
                        
                        if(theMaterial != null){
                            if(theMaterial.getDescription().contains(materialDesc)){
                                found = true;
                                matchingJobs.add(theJob);
                            }                          
                        }
                    }
                }
            }                     
        }
        
        return matchingJobs;
    } 
    
    public void saveItem(JobItem theJob) {
        org.hibernate.Transaction tx = null;

        try {
            tx = session.beginTransaction();
//            session.update(theJob);
            session.save(theJob.getClient().getAddress().getCity().getCountry());
            session.save(theJob.getClient().getAddress().getCity());
            session.save(theJob.getClient().getAddress());
            session.save(theJob.getClient());

            session.save(theJob.getStockItem());

            Iterator it = theJob.getStockItem().getMaterialBatchs().iterator();

            while (it.hasNext()) {
                MaterialBatch theBatch = (MaterialBatch) it.next();
                Set batchSheets = theBatch.getBatchSheets();
                session.save(theBatch);

                Iterator sheetIt = batchSheets.iterator();

                while (sheetIt.hasNext()) {
                    BatchSheet theSheet = (BatchSheet) sheetIt.next();
                    session.save(theSheet);
                }
            }

            session.save(theJob);

            session.flush();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        }
    }
    
    public List<String> getTypes() {
        List<String> typeList = null;
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select distinct materialType ");
            queryBuilder.append("from Material as material ");
            queryBuilder.append("order by material.materialType " );

            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString());
            typeList = (List<String>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return typeList;
    }

    public List<Material> getMaterials(String type) {
        List<Material> materialList = null;
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("from Material as material ");
            queryBuilder.append("where material.materialType = :type ");
            queryBuilder.append("and material.active is true ");
            queryBuilder.append("order by material.description ");

            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString()).setParameter("type", type);
            materialList = (List<Material>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return materialList;
    }

    public List<MaterialSheet> getMaterialSheets(String type, String desc, String thickness) {
        List<MaterialSheet> materialSheetList = null;
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("from MaterialSheet as materialSheet ");
            queryBuilder.append("where materialSheet.material.materialType = :type ");
            queryBuilder.append("and materialSheet.material.description = :desc ");

            if (thickness != null) {
                queryBuilder.append("and materialSheet.thickness = :thickness ");
            }
            
            queryBuilder.append("order by materialSheet.thickness ");

            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString())
                    .setParameter("type", type).setParameter("desc", desc);

            if (thickness != null) {
                q.setParameter("thickness", thickness);
            }

            materialSheetList = (List<MaterialSheet>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return materialSheetList;
    }
    
    public List<CustomerCompany> getCompanies(String companyNameBit) {
        List<CustomerCompany> companyList = null;
        String theSearchTerm = companyNameBit;
        
        if(companyNameBit != null){
           theSearchTerm = "%" + companyNameBit + "%";
        }
        
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("from CustomerCompany as company ");
            queryBuilder.append("where company.active is true ");
            
            if(companyNameBit != null){
                queryBuilder.append("and company.companyName like :companyName ");
            }
            
            queryBuilder.append("order by company.companyName ");

            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString());
            
            if(companyNameBit != null){
                q.setParameter("companyName", theSearchTerm);
            }

            companyList = (List<CustomerCompany>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return companyList;
    }

    public void saveCompany(CustomerCompany theCompany) {
        try {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.save(theCompany.getContact());
            session.save(theCompany.getAddress().getCity().getCountry());
            session.save(theCompany.getAddress().getCity());
            session.save(theCompany.getAddress());
            session.save(theCompany);

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<String> getAllThicknesses() {
        List<String> thicknesses = null;
        
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("select distinct thickness ");
            queryBuilder.append("from MaterialSheet as sheet ");
            queryBuilder.append("order by sheet.thickness ");

            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString());
            
            thicknesses = (List<String>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return thicknesses;
    }
    
    public List<Material> getMaterialsFromType(String searcStringBit) {
        List<Material> materialList = null;
        String theSearchTerm = searcStringBit;
        
        if(searcStringBit != null){
           theSearchTerm = "%" + searcStringBit + "%";
        }
        
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("from Material as material ");
            queryBuilder.append("where material.active is true ");
            
            if(searcStringBit != null){
                queryBuilder.append("and material.materialType like :type ");
            }
            
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString());
            
            if(searcStringBit != null){
                q.setParameter("type", theSearchTerm);
            }
            
            materialList = (List<Material>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return materialList;
    }
    
    public List<Material> getMaterialsFromDesc(String materialDescBit) {
        List<Material> materialList = null;
        String theSearchTerm = materialDescBit;
        
        if(materialDescBit != null){
           theSearchTerm = "%" + materialDescBit + "%";
        }
        
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("from Material as material ");
            
            if(materialDescBit != null){
                queryBuilder.append("where material.description like :desc ");
            }
 
            queryBuilder.append("and material.active is true ");

            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString());
            
            if(materialDescBit != null){
                q.setParameter("desc", theSearchTerm);
            }
            
            materialList = (List<Material>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return materialList;
    }
    
    public Material getMaterial(String type, String desc) {
        Material material = null;
        List<Material> materialList = null;
        
        try {
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("from Material as material ");
            queryBuilder.append("where material.materialType = :type ");
            queryBuilder.append("and material.description = :desc ");
            queryBuilder.append("and material.active is true ");
            
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString())
                    .setParameter("type", type).setParameter("desc", desc);
            materialList = (List<Material>) q.list();

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        if(!materialList.isEmpty()){
            material = materialList.get(0);
            
            if(materialList.size() > 1){
                String warningMessage = "Multiple Materials:" + desc;
                Logger.getLogger(DBHelper.class.getName()).log(Level.WARNING, warningMessage);                                   
            }   
        }
        
        return material;
    }

    public List<MaterialSheet> getMaterialSheets(String type, String desc) {
        List<MaterialSheet> materialSheetList = null;
        try {            
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("from MaterialSheet as materialSheet ");
            queryBuilder.append("where materialSheet.material.materialType = :type");
            queryBuilder.append(" and materialSheet.material.description = :desc");
            
            org.hibernate.Transaction tx = session.beginTransaction();
            Query q = session.createQuery(queryBuilder.toString())
                    .setParameter("type", type).setParameter("desc", desc);
            materialSheetList = (List<MaterialSheet>) q.list();

            session.flush();
            
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return materialSheetList;
    } 
    
    public void saveMaterial(Material theMaterial) {
        try {
            org.hibernate.Transaction tx = session.beginTransaction();
            session.save(theMaterial);

            Set materialSheets = theMaterial.getMaterialSheets();

            Iterator it = materialSheets.iterator();

            while (it.hasNext()) {
                session.save((MaterialSheet) it.next());
            }

            session.flush();

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}