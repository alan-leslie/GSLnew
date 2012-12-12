package companies;

import data.CustomerCompany;
import java.util.EventListener;

public interface CompanyListener extends EventListener { 
  void companyStateChanged(CustomerCompany company);
}