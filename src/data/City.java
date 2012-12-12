package data;
// Generated 11-Dec-2012 01:34:01 by Hibernate Tools 3.2.1.GA


import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * City generated by hbm2java
 */
public class City  implements java.io.Serializable {


     private Short cityId;
     private Country country;
     private String city;
     private byte versionNo;
     private String userId;
     private Date createDate;
     private boolean active;
     private Date lastUpdate;
     private Set addresses = new HashSet(0);

    public City() {
    }

	
    public City(Country country, String city, byte versionNo, String userId, Date createDate, boolean active, Date lastUpdate) {
        this.country = country;
        this.city = city;
        this.versionNo = versionNo;
        this.userId = userId;
        this.createDate = createDate;
        this.active = active;
        this.lastUpdate = lastUpdate;
    }
    public City(Country country, String city, byte versionNo, String userId, Date createDate, boolean active, Date lastUpdate, Set addresses) {
       this.country = country;
       this.city = city;
       this.versionNo = versionNo;
       this.userId = userId;
       this.createDate = createDate;
       this.active = active;
       this.lastUpdate = lastUpdate;
       this.addresses = addresses;
    }
   
    public Short getCityId() {
        return this.cityId;
    }
    
    public void setCityId(Short cityId) {
        this.cityId = cityId;
    }
    public Country getCountry() {
        return this.country;
    }
    
    public void setCountry(Country country) {
        this.country = country;
    }
    public String getCity() {
        return this.city;
    }
    
    public void setCity(String city) {
        this.city = city;
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
    public Set getAddresses() {
        return this.addresses;
    }
    
    public void setAddresses(Set addresses) {
        this.addresses = addresses;
    }




}

