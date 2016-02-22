package net.snnmo.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by cc on 16/2/16.
 */

@Entity
@Table(name="ORDER_ITEMS")
public class OrderItemsEntity implements Serializable {

    public  OrderItemsEntity() {

    }

    @Id
    @Column(name="ITEM_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="ORDER_ID")
    private OrderEntity order;      // 订单编号

    @ManyToOne
    @JoinColumn(name="GOODS_ID")
    private GoodsEntity goods;         // 商品编号

    @Column(name="GOODS_NAME", nullable = true, length = 25)
    private String goodsName;       // 商品名字

    @Column(name="SINGLE_PRICE", nullable = false)
    private double singlePrice;     // 单价

    @Column(name="COUNT", nullable = false)
    private int count = 0;              // 购买数量

    @Column(name="TOTAL_PRICE", nullable = false)
    private double totalPrice = 0.00;      // 合计金额

    @Column(name="FREIGHT", nullable = false)
    private double freight = 0;         // 运费

    @Column(name="SPECIAL_ATTRIBUTE", nullable = true, length=2048)
    private String specialAttribute;    // 商品特殊附加属性

    public long getId() {
        return id;
    }

//    public void setId(long id) {
//        this.id = id;
//    }

//    public OrderEntity getOrder() {
//        return order;
//    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public GoodsEntity getGoods() {
        return goods;
    }

    public void setGoods(GoodsEntity goods) {
        this.goods = goods;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public double getSinglePrice() {
        return singlePrice;
    }

    public void setSinglePrice(double singlePrice) {
        this.singlePrice = singlePrice;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getFreight() {
        return freight;
    }

    public void setFreight(double freight) {
        this.freight = freight;
    }

    public String getSpecialAttribute() {
        return specialAttribute;
    }

    public void setSpecialAttribute(String specialAttribute) {
        this.specialAttribute = specialAttribute;
    }
}
