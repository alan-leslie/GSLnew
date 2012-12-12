package companies.ui;

import data.CustomerCompany;

public enum CompanyColumn { 
    ITEM_NAME("Company Name") { 
    @Override public Object valueIn(CustomerCompany company) { 
      return company.getCompanyName(); 
    } 
  }, 
  ADDRESS("Adress") { 
    @Override public Object valueIn(CustomerCompany company) { 
      return company.getAddress().getAddress();   
    } 
  }, 
  EMAIL("E-Mail") { 
    @Override public Object valueIn(CustomerCompany company) { 
      return company.getContact().getContactEmail(); 
    }    
  }, 
  PHONE_NUMBER("Phone Number") { 
    @Override public Object valueIn(CustomerCompany company) { 
      return company.getContact().getContactPhoneNumber(); 
    }    
  }; 
  
  abstract public Object valueIn(CustomerCompany company); 
  
  public final String name;
  
  private CompanyColumn(String name) { 
    this.name = name; 
  } 

  public static CompanyColumn at(int offset) { return values()[offset]; } 
}
