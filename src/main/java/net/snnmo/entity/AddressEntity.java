package net.snnmo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by TTong on 16-1-11.
 */
@Entity
@Table(name="USER_ADDR")
public class AddressEntity implements Serializable {

    public  AddressEntity() {

    }

    @Id @Column(name="ADDR_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "LINK_MAN", nullable = false, length=35)
    private String linkMan;

    @Column(name = "LINK_PHONE", nullable = false, length=35)
    private String linkPone;

    @Column(name = "PROVINCE", nullable = false, length=35)
    private String province;

    @Column(name = "CITY", nullable = false, length=65)

    private String city;

    @Column(name = "AREA", nullable = false, length=35)
    private String area;

    @Column(name = "STREET", nullable = true, length=65)
    private String street;

    @Column(name = "DETAIL", nullable = false, length=165)
    private String detail;

    @Column(name = "PINCODE", nullable = true, length=15)
    private String pincode;

    @Column(name = "IS_DEFAULT", nullable = false)
    private Boolean defaults = false;

    @ManyToOne
    @JoinColumn(name="USER_ID")
    private UserEntity user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLinkMan() {
        return linkMan;
    }

    public void setLinkMan(String linkMan) {
        this.linkMan = linkMan;
    }

    public String getLinkPone() {
        return linkPone;
    }

    public void setLinkPone(String linkPone) {
        this.linkPone = linkPone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    @JsonIgnore
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public Boolean getDefaults() {
        return defaults;
    }

    public void setDefaults(Boolean defaults) {
        this.defaults = defaults;
    }

    public String getAddressString() {
        return this.getProvince().split("\\(")[0] +
                " " + this.getCity().split("\\(")[0] +
                " " + this.getArea().split("\\(")[0] +
                " " + this.getStreet() + " " + this.getDetail();
    }
}
