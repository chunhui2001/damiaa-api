package net.snnmo.dao;

import net.snnmo.assist.OrderStatus;
import net.snnmo.assist.PayMethod;
import net.snnmo.entity.*;
import net.snnmo.exception.DbException;

import java.util.Collection;
import java.util.Map;

/**
 * Created by cc on 16/2/15.
 */
public interface IOrderDAO {
    public OrderEntity create(UserEntity user, PayMethod payMethod, AddressEntity addr
            , Map<GoodsEntity, Integer> goodsList) throws DbException;

    public void update(OrderEntity order);


    public OrderEntity updateStatus(String orderid, UserEntity user, OrderStatus status) throws DbException;

    public int paymentCompleted(String orderid, String userid, String openid, String paymentInfo)throws DbException;

    public Collection<OrderEntity> list(String userid);

    public long count(String userid);

    public OrderEntity get(String orderid, UserEntity user);

    public OrderEntity get(String orderid, String userid);

    public Collection<OrderItemsEntity> items(String orderid);

    public OrderEventEntity addEvent(OrderStatus eventType, OrderEntity order, String message) throws DbException;


    public boolean exists(String userid, String openid, String prepayid);
}
