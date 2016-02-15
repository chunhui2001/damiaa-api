package net.snnmo.entity;

import net.snnmo.assist.GoodsUnit;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by cc on 16/2/15.
 */
@Entity
@Table(name="GOODS")
public class GoodsEntity implements Serializable {
    @Id
    @Column(name="GOODS_ID")
    @GenericGenerator(name="GoodsIdGenerator",
            strategy = "net.snnmo.assist.GoodsIdGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name="prefix", value="")}
    )
    @GeneratedValue(generator = "GoodsIdGenerator")
    private String id;

    @Column(name="GOODS_NAME", nullable = false, length=25)
    private String name;                // 商品名称

    @Column(name="GOODS_HTML_NAME", nullable = false, length=255)
    private String htmlName;            // 用html修饰过的名称

    @Column(name="MARKET_PRICE", nullable = false)
    private double marketPrice;         // 市场价

    @Column(name="TRADE_PRICE", nullable = false)
    private double tradePrice ;         // 批发价

    @Column(name="VIP_PRICE", nullable = false)
    private double vipPrice;            // vip价格

    @Column(name="SUPER_VIP_PRICE", nullable = false)
    private double superVIPPrice;       // 超级会员价

    @Column(name="UNIT", nullable = false, length = 10)
    private GoodsUnit unit;             // 单位

    @Column(name="WEIGHT", nullable = true, length = 10)
    private String weight;              // 重量

    @Column(name="PALCE", nullable = true, length = 30)
    private String place;               // 产地

    @Column(name="ONSALE", nullable = false)
    private boolean onSale = true;      // 是否下架

    @Column(name="DESCRIPTION", nullable = true, length = 655)
    private String description;         // 商品描述

    @Column(name="DETAIL", nullable = true, length = 2048)
    private String detail;              // 商品详情



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

    public String getHtmlName() {
        return htmlName;
    }

    public void setHtmlName(String htmlName) {
        this.htmlName = htmlName;
    }

    public double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public double getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(double vipPrice) {
        this.vipPrice = vipPrice;
    }

    public double getSuperVIPPrice() {
        return superVIPPrice;
    }

    public void setSuperVIPPrice(double superVIPPrice) {
        this.superVIPPrice = superVIPPrice;
    }

    public GoodsUnit getUnit() {
        return unit;
    }

    public void setUnit(GoodsUnit unit) {
        this.unit = unit;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
