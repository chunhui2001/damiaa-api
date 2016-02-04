package net.snnmo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.snnmo.assist.UserStatus;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.hibernate.annotations.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by TTong on 16-1-8.
 */
@Entity
@Table(name="USERS")
public class UserEntity implements Serializable {
    @Id @Column(name="USER_ID")
//    @GenericGenerator(name="system-uuid", strategy = "uuid")
//    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name="ObjectIdGenerator",
          strategy = "net.snnmo.assist.ObjectIdGenerator",
          parameters = {@org.hibernate.annotations.Parameter(name="prefix", value="UU-")}
    )
    @GeneratedValue(generator = "ObjectIdGenerator")
    private String id;

    @Column(name="USER_NAME", nullable = false, unique = true, length=65)
    @Size(min = 5, max = 65, message = "用户名必须在{min}-{max}之间")
    private String name;

    @Column(name="PASSWD", nullable = false, length=100)
    @JsonIgnore
    private String passwd;

    @Column(name="EMAIL", unique = true, length=65)
    private String email;

    @Column(name="PHONE", length=65)
    private String phone;


    @OneToMany(mappedBy = "user")
    //@LazyCollection(LazyCollectionOption.FALSE)
    private Collection<AddressEntity> listOfAddresses = new ArrayList<>();

    @Column(name="DOB")
    @Temporal(TemporalType.DATE)
    private Date birthday;

    @Column(name="CREATED_TIME", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date createdTime = new Date();

    @Column(name="USER_STATUS", nullable = false, length=15)
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Column(name="ROLES", nullable = false, length=955)
    private String roles = "ROLE_USER";

    @Column(name="GENDER", length=5)
    private String gender;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


//    public Collection<AddressEntity> getListOfAddresses() {
//        return listOfAddresses;
//    }

//    public void setListOfAddresses(Collection<AddressEntity> listOfAddresses) {
//        this.listOfAddresses = listOfAddresses;
//    }


    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
