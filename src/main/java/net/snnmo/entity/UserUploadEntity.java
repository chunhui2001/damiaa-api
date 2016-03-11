package net.snnmo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by TTong on 16-3-11.
 */
@Entity
@Table(name="USER_UPLOAD")
public class UserUploadEntity implements Serializable {

    public  UserUploadEntity() {

    }

    @Id
    @Column(name="ADDR_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "STORAGE_PATH", nullable = true, length=266)
    private String path;            // 存储路径

    @Column(name = "URI", nullable = true, length=625)
    private String uri;             // 网址

    @Column(name = "CONTENT", nullable = true, length=625)
    private byte[] content;         // 文件内容

    @Column(name = "UPLOAD_TYPE", nullable = true, length=10)
    private String uploadType;      // 上传类型: headimg, file, image, video

    @Column(name = "UNIONID", nullable = true, length=128)
    private String unionid;

    @Column(name = "OPENID", nullable = true, length=128)
    private String openid;

    @Column(name = "CREATED_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private Date createdDate = new Date();       // 上传时间

    public long getId() {
        return id;
    }

//    public void setId(long id) {
//        this.id = id;
//    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getUploadType() {
        return uploadType;
    }

    public void setUploadType(String uploadType) {
        this.uploadType = uploadType;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
