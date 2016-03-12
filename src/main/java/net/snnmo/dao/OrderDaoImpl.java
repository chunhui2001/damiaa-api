package net.snnmo.dao;

import net.snnmo.assist.OrderStatus;
import net.snnmo.assist.PayMethod;
import net.snnmo.assist.UserRole;
import net.snnmo.entity.*;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by cc on 16/2/15.
 */
public class OrderDaoImpl implements IOrderDAO {

    private IOrderEventDAO orderEventDao;
    private IUserDAO userDao;

    public IUserDAO getUserDao() {
        return userDao;
    }

    public void setUserDao(IUserDAO userDao) {
        this.userDao = userDao;
    }

    public IOrderEventDAO getOrderEventDao() {
        return orderEventDao;
    }

    public void setOrderEventDao(IOrderEventDAO orderEventDao) {
        this.orderEventDao = orderEventDao;
    }

    private SessionFactory sessionFactory;

    public OrderDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public Collection<OrderEntity> list(String userid) {
        Session s = this.sessionFactory.getCurrentSession();

        Query query = s.createQuery("from OrderEntity where userId=:userId and status != :status order by id desc");

        query.setParameter("userId", userid);
        query.setParameter("status", OrderStatus.DELETED);

        return query.list();
    }

    @Override
    @Transactional
    public long count(String userid) {
        long orderCount = 0;

        Session s = this.sessionFactory.getCurrentSession();

        Query query = s.createQuery("select count(*) from OrderEntity where userId=:userId and status != :status");

        query.setParameter("userId", userid);
        query.setParameter("status", OrderStatus.DELETED);

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

            if (user.getRoles().indexOf(UserRole.ROLE_VIP.toString()) != -1) {
                price   = currentGoods.getVipPrice();
            }

            if (user.getRoles().indexOf(UserRole.ROLE_SUPER_VIP.toString()) != -1) {
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
        order.setOpenId(user.getOpenId());

        order.setListOfItems(listOfOrderItems);


        this.addEvent(OrderStatus.CREATE, order, null);
        this.sessionFactory.getCurrentSession().save(order);

        return order;
    }


    @Override
    @Transactional
    public void update(OrderEntity order) {
        this.sessionFactory.getCurrentSession().update(order);
    }

    @Override
    public void addEvent(OrderStatus eventType, OrderEntity order, String message) {

        OrderEventEntity eventEntity = new OrderEventEntity();

        eventEntity.setEventTime(new Date());
        eventEntity.setOrder(order);
        eventEntity.setType(eventType);


        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH点mm分");

        //OrderStatus
        if (OrderStatus.CREATE == eventType)
            eventEntity.setMessage("该订单创建于 " + format.format(order.getCreateTime()));

        if (OrderStatus.CANCEL == eventType)
            eventEntity.setMessage("该订单于 " + format.format(order.getCreateTime()) + " 被取消");

        if (OrderStatus.DELETED == eventType)
            eventEntity.setMessage("该订单于 " + format.format(order.getCreateTime()) + " 被删除");

        if (OrderStatus.INFO == eventType)
            eventEntity.setMessage(message);

        if (OrderStatus.PAYMENT == eventType)
            eventEntity.setMessage("该订单于 " + format.format(order.getCreateTime()) + " 完成支付");

        if (OrderStatus.SENDED == eventType)
            eventEntity.setMessage("该订单于 " + format.format(order.getCreateTime()) + " 发货");

        if (OrderStatus.SIGNED == eventType)
            eventEntity.setMessage("该订单于 " + format.format(order.getCreateTime())
                        + " 由 " + order.getReceiveMan() + " 签收");

        if (message != null) eventEntity.setMessage(message);

        orderEventDao.addEvent(eventEntity);

        order.setLastEvent(eventEntity.getMessage());
    }

    @Override
    @Transactional
    public OrderEntity get(String orderid, UserEntity user) {
        Session session = this.sessionFactory.getCurrentSession();

        boolean isAdmin = userDao.hasAnyRole(user
                    , new UserRole[]{ UserRole.ROLE_ADMIN, UserRole.ROLE_SUPERADMIN });

        Query query = session.createQuery(
                "from OrderEntity where id=:orderid"
                        + (!isAdmin ? " and userId=:userid" : ""));

        query.setParameter("orderid", orderid);

        if (!isAdmin) query.setParameter("userid", user.getId());

        return (OrderEntity)query.uniqueResult();
    }

    @Override
    @Transactional
    public Collection<OrderItemsEntity> items(String orderid) {

        Session session = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery("from OrderItemsEntity where order.id = :orderid");

        query.setParameter("orderid", orderid);

        return query.list();
    }
}
