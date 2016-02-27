package net.snnmo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import net.snnmo.assist.OrderEventType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by cc on 16/2/27.
 */
@Entity
@Table(name="ORDER_EVENTS")
public class OrderEventEntity implements Serializable {

    public  OrderEventEntity() {

    }

    @Id
    @Column(name="EVENT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="ORDER_ID")
    private OrderEntity order;      // 订单编号

    @Column(name="EVENT_TYPE", nullable = false, length=25)
    @Enumerated(EnumType.STRING)
    private OrderEventType type;    // 事件名字

    @Column(name="EVENT_TIME", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy年MM月dd日 HH点mm分", timezone="GMT+8")
    private Date eventTime = new Date();

    @Column(name="EVENT_MESSAGE", nullable = false, length=1024)
    private String message;



    public long getId() {
        return id;
    }
//
//    public void setId(long id) {
//        this.id = id;
//    }

//    public OrderEntity getOrder() {
//        return order;
//    }

    public void setOrder(OrderEntity order) {
        this.order = order;
    }

    public OrderEventType getType() {
        return type;
    }

    public void setType(OrderEventType type) {
        this.type = type;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
