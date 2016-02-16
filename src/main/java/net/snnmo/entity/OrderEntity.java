package net.snnmo.entity;

import net.snnmo.assist.OrderStatus;
import net.snnmo.assist.PayMethod;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by cc on 16/2/14.
 */
@Entity
@Table(name="ORDERS")
public class OrderEntity implements Serializable {
    @Id
    @Column(name="ORDER_ID")
    @GenericGenerator(name="OrderIdGenerator",
            strategy = "net.snnmo.assist.OrderIdGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name="prefix", value="R")}
    )
    @GeneratedValue(generator = "OrderIdGenerator")
    private String id;


    @Column(name="CREATED_TIME", nullable = false)
    private Date createTime = new Date();

    @Column(name="ORDER_STATUS", nullable = false, length=15)
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name="USER_ID", nullable = false, length=100)
    private String userId;

    @Column(name="ITEM_MONEY", nullable = false)
    private double itemMoney;       // 商品总金额

    @Column(name="FREIGHT_MONEY", nullable = false)
    private double freightMoney;    // 运费

    @Column(name="EXEMPTION_MONEY", nullable = true)
    private double exemptionMoney;  // 减免金额

    @Column(name="ORDER_MONEY", nullable = false)
    private double orderMoney;      // 订单总金额 (itemMoney + freightMonth - orderMoney)
//
//    @Column(name="ITEM_COUNT", nullable = false)
//    private int itemCount = 0;          // 商品总数量

    @Column(name="PAY_METHOD", nullable = false, length=15)
    @Enumerated(EnumType.STRING)
    private PayMethod payMethod;

    @Column(name="ADDRESS", nullable = false, length=255)
    private String address;     // 订单地址

    @Column(name="PHONE", nullable = false, length=25)
    private String phone;       // 联系电话

    @Column(name="RECEIVE_MEN", nullable = false, length=55)
    private String receiveMan;  // 收获人

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order", cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private Collection<OrderItemsEntity> listOfItems = new ArrayList<>();

    public String getId() {
        return id;
    }

//    public void setId(String id) {
//        this.id = id;
//    }

    public Date getCreateTime() {
        return createTime;
    }

//    public void setCreateTime(Date createTime) {
//        this.createTime = createTime;
//    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getItemMoney() {
        return itemMoney;
    }

    public void setItemMoney(double itemMoney) {
        this.itemMoney = itemMoney;
    }

    public double getFreightMoney() {
        return freightMoney;
    }

    public void setFreightMoney(double freightMoney) {
        this.freightMoney = freightMoney;
    }

    public double getExemptionMoney() {
        return exemptionMoney;
    }

    public void setExemptionMoney(double exemptionMoney) {
        this.exemptionMoney = exemptionMoney;
    }

    public double getOrderMoney() {
        return orderMoney;
    }

    public void setOrderMoney(double orderMoney) {
        this.orderMoney = orderMoney;
    }

    public PayMethod getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(PayMethod payMethod) {
        this.payMethod = payMethod;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReceiveMan() {
        return receiveMan;
    }

    public void setReceiveMan(String receiveMan) {
        this.receiveMan = receiveMan;
    }

//    public Collection<OrderItemsEntity> getListOfItems() {
//        return listOfItems;
//    }

    public void setListOfItems(Collection<OrderItemsEntity> listOfItems) {
        this.listOfItems = listOfItems;
    }
}
