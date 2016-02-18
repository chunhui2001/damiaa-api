package net.snnmo.dao;

import net.snnmo.assist.OrderStatus;
import net.snnmo.assist.PayMethod;
import net.snnmo.entity.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by cc on 16/2/15.
 */
public class OrderDaoImpl implements IOrderDAO {
    private SessionFactory sessionFactory;

    public OrderDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public Collection<OrderEntity> list(String userid) {
        Session s = this.sessionFactory.getCurrentSession();

        Query query = s.createQuery("from OrderEntity where userId=:userId");

        query.setParameter("userId", userid);

        return query.list();
    }

    @Override
    @Transactional
    public long count(String userid) {
        long orderCount = 0;

        Session s = this.sessionFactory.getCurrentSession();

        Query query = s.createQuery("select count(*) from OrderEntity where userId=:userId");

        query.setParameter("userId", userid);

        orderCount = (long)query.uniqueResult();

        return orderCount;
    }

    @Override
    @Transactional
    public OrderEntity create(UserEntity user, PayMethod payMethod, AddressEntity addr
            , Map<GoodsEntity, Integer> goodsList) {

        OrderEntity order = new OrderEntity();

        order.setUserId(user.getId());
        order.setPayMethod(payMethod);
        order.setStatus(OrderStatus.PENDING);
        order.setAddress(addr.getProvince().split("\\(")[0] +
                " " + addr.getCity().split("\\(")[0] +
                " " + addr.getArea().split("\\(")[0] +
                " " + addr.getStreet() + " " + addr.getDetail());
        order.setPhone(addr.getLinkPone());
        order.setReceiveMan(addr.getLinkMan());

        double DELIVERY_SINGLE_COSTS = 5.00;//deliverySingleCosts

        double itemMoney        = 0.00;
        double freightMoney     = 0.00;
        double orderMoney       = 0.00;
        double exemptionMoney   = 0.00;

        Collection<OrderItemsEntity> listOfOrderItems = new ArrayList<>();

        for (Map.Entry<GoodsEntity, Integer> goodsEntityIntegerEntry : goodsList.entrySet()) {

            GoodsEntity currentGoods    = goodsEntityIntegerEntry.getKey();         // 购买的商品
            Integer currentCount        = goodsEntityIntegerEntry.getValue();       // 购买数量

            double price    = 0;
            double freight  = 0;

            if (user.getRoles().indexOf("ROLE_VIP") != -1) {
                price   = currentGoods.getVipPrice();
            }

            if (user.getRoles().indexOf("ROLE_SUPER_VIP") != -1) {
                price   = currentGoods.getSuperVIPPrice();
            }

            if (price == 0)
                price = currentGoods.getMarketPrice();

            itemMoney += price * currentCount;

            //count >=3 ? (0).toFixed(2) : (count + singlePrice - 1).toFixed(2);

            if (currentCount >= 3) freight = 0;
            else freight = currentCount + DELIVERY_SINGLE_COSTS - 1;

            freightMoney += freight;

            OrderItemsEntity orderItem = new OrderItemsEntity();

            orderItem.setOrder(order);
            orderItem.setGoods(currentGoods);
            orderItem.setCount(currentCount);
            orderItem.setFreight(freight);
            orderItem.setGoodsName(currentGoods.getName());
            orderItem.setSinglePrice(price);
            orderItem.setTotalPrice(price * currentCount);
            orderItem.setSpecialAttribute(null);

            listOfOrderItems.add(orderItem);


            //this.sessionFactory.getCurrentSession().save(orderItem);
        }

        orderMoney = itemMoney + freightMoney - exemptionMoney;

        order.setExemptionMoney(exemptionMoney);         // 减免金额
        order.setFreightMoney(freightMoney);             // 运费
        order.setItemMoney(itemMoney);                   // 商品总金额
        order.setOrderMoney(orderMoney);                 // 订单总金额

        order.setListOfItems(listOfOrderItems);

        this.sessionFactory.getCurrentSession().save(order);

        return order;
    }

    @Override
    @Transactional
    public OrderEntity get(String orderid, String userid) {
        Session session = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery("from OrderEntity where id=:orderid and userId=:userid");

        query.setParameter("orderid", orderid);
        query.setParameter("userid", userid);

        return (OrderEntity)query.uniqueResult();
    }
}
