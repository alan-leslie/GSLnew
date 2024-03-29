package data;
// Generated 11-Dec-2012 01:34:01 by Hibernate Tools 3.2.1.GA


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Address generated by hbm2java
 */
public class Address  implements java.io.Serializable {


     private Short addressId;
     private City city;
     private String address;
     private String address2;
     private String postalCode;
     private byte versionNo;
     private String userId;
     private Date createDate;
     private boolean active;
     private Date lastUpdate;
     private Set customerCompanies = new HashSet(0);
     private Set clients = new HashSet(0);

    public Address() {
    }

	
    public Address(City city, String address, byte versionNo, String userId, Date createDate, boolean active, Date lastUpdate) {
        this.city = city;
        this.address = address;
        this.versionNo = versionNo;
        this.userId = userId;
        this.createDate = createDate;
        this.active = active;
        this.lastUpdate = lastUpdate;
    }
    public Address(City city, String address, String address2, String postalCode, byte versionNo, String userId, Date createDate, boolean active, Date lastUpdate, Set customerCompanies, Set clients) {
       this.city = city;
       this.address = address;
       this.address2 = address2;
       this.postalCode = postalCode;
       this.versionNo = versionNo;
       this.userId = userId;
       this.createDate = createDate;
       this.active = active;
       this.lastUpdate = lastUpdate;
       this.customerCompanies = customerCompanies;
       this.clients = clients;
    }
   
    public Short getAddressId() {
        return this.addressId;
    }
    
    public void setAddressId(Short addressId) {
        this.addressId = addressId;
    }
    public City getCity() {
        return this.city;
    }
    
    public void setCity(City city) {
        this.city = city;
    }
    public String getAddress() {
        return this.address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    public String getAddress2() {
        return this.address2;
    }
    
    public void setAddress2(String address2) {
        this.address2 = address2;
    }
    public String getPostalCode() {
        return this.postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public byte getVersionNo() {
        return this.versionNo;
    }
    
    public void setVersionNo(byte versionNo) {
        this.versionNo = versionNo;
    }
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    public Date getLastUpdate() {
        return this.lastUpdate;
    }
    
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public Set getCustomerCompanies() {
        return this.customerCompanies;
    }
    
    public void setCustomerCompanies(Set customerCompanies) {
        this.customerCompanies = customerCompanies;
    }
    public Set getClients() {
        return this.clients;
    }
    
    public void setClients(Set clients) {
        this.clients = clients;
    }




}


