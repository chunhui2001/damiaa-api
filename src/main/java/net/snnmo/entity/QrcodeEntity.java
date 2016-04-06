package net.snnmo.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by cc on 16/4/6.
 */
@Entity
@Table(name="QRCODES")
public class QrcodeEntity implements Serializable {

    public  QrcodeEntity() {

    }

    @Id
    @Column(name="QRCODE_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="QRCODE_NO", nullable = false, length=10)
    private long no;

    @Column(name = "TICKET", nullable = false, length=135, unique = true)
    private String ticket;          // 获取二维码的票据

    @Column(name = "QRCODE_URI", nullable = false, length=65)
    private String qrcodeURI;       // 二维码的网络地址

    @Column(name = "GEN_URI", nullable = false, length=265)
    private String gen;             // 生成二维码的 endpoints

    @Column(name = "DESCRIPTION", nullable = true, length=1024)
    private String description;     // 该二维码的描述信息

    @Column(name = "BELONG_TO", nullable = true, length=65)
    private String belongTo;        // 该二维码属于谁, 可能是一个 openid, 也可能是用户id, 默认是用户id, 如果是 openid 以 openid: 开头



    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNo() {
        return no;
    }

    public void setNo(long no) {
        this.no = no;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getQrcodeURI() {
        return qrcodeURI;
    }

    public void setQrcodeURI(String qrcodeURI) {
        this.qrcodeURI = qrcodeURI;
    }

    public String getGen() {
        return gen;
    }

    public void setGen(String gen) {
        this.gen = gen;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBelongTo() {
        return belongTo;
    }

    public void setBelongTo(String belongTo) {
        this.belongTo = belongTo;
    }
}
