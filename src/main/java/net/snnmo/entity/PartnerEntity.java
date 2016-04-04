package net.snnmo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.snnmo.assist.PartnerType;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by cc on 16/4/3.
 */
@Entity
@Table(name="PARTNERS")
public class PartnerEntity implements Serializable {

    public PartnerEntity() {

    }

    @Id
    @Column(name="PARTNER_ID")
    @GenericGenerator(name="OrderIdGenerator",
            strategy = "net.snnmo.assist.OrderIdGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name="prefix", value="P")
                    , @org.hibernate.annotations.Parameter(name="len", value="7")
            }
    )
    @GeneratedValue(generator = "OrderIdGenerator")
    private String id;

    @Column(name="OPENID", nullable = false, length=35, unique = true)
    private String openid;

    @Column(name="UNIONID", nullable = false, length=35, unique = true)
    private String unionid;

    @Column(name="NICKNAME", nullable = false, length=35)
    private String nickname;

    @Column(name="PARTNER_TYPE", nullable = false, length=15)
    @Enumerated(EnumType.STRING)
    private PartnerType type;

    @Column(name="JOIN_TIME", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy年MM月dd日 HH点mm分", timezone="GMT+8")
    private Date joinTime = new Date();

    @Column(name="DESCRIPTION", nullable = true, length=625)
    private String description;

    @Column(name="PARTNER_NAME", nullable = true, length=25)
    private String partnerName;

    @Column(name="ADDRESS", nullable = true, length=255)
    private String address;

    @Column(name="PHONE", nullable = true, length=25)
    private String phone;

    @Column(name="PHOTO", nullable = true, length=2048)
    private String photo;

    @Column(name="QRCODE", nullable = true, length=2048)
    private String qrcode;

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getId() {
        return id;
    }

//    public void setId(String id) {
//        this.id = id;
//    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public PartnerType getType() {
        return type;
    }

    public void setType(PartnerType type) {
        this.type = type;
    }

    public String getPartnerTypeName() {
        String str = "";

        switch (this.getType().toString().toUpperCase()) {
            case "CANGMAI":
                str = "仓买";
                break;
            case "CHAOSHI":
                str = "超市";
                break;
            case "SHANGCHANG":
                str = "商场";
                break;
            default:
                str = this.getType().toString().toUpperCase();
        }

        return str;
    }

    public Date getJoinTime() {
        return joinTime;
    }

//    public void setJoinTime(Date joinTime) {
//        this.joinTime = joinTime;
//    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
