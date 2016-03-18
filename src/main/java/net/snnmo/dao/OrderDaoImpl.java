package net.snnmo.dao;

import net.snnmo.assist.DeliverySupport;
import net.snnmo.assist.OrderStatus;
import net.snnmo.assist.PayMethod;
import net.snnmo.assist.UserRole;
import net.snnmo.entity.*;
import net.snnmo.exception.DbException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
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
            , Map<GoodsEntity, Integer> goodsList) throws DbException {

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


        this.addEvent(OrderStatus.PENDING, order, null);
        this.sessionFactory.getCurrentSession().save(order);

        return order;
    }


    @Deprecated
    @Override
    @Transactional
    public void update(OrderEntity order) {
        this.sessionFactory.getCurrentSession().update(order);
    }

    @Override
    @Transactional
    public OrderEntity updateStatus(String orderid, UserEntity user, OrderStatus status) throws DbException {
        OrderEntity order = this.get(orderid, user);
        order.setStatus(status);
        this.addEvent(status, order, null);
        this.sessionFactory.getCurrentSession().update(order);
        return order;
    }

    @Override
    @Transactional
    // 如果更新返回的结果等于1: 则更新成功,
    // 如果等于0: 说明已经更新过, 本次通知属于重复通知,
    // 如果返回3: 说明该订单不存在
    public int paymentCompleted(String orderid, String userid, String openid, String paymentInfo)
            throws DbException {

        Session session = this.sessionFactory.getCurrentSession();


        Query query = session.createQuery(
                    "from OrderEntity where id=:orderid and userId=:userid and openId=:openid");

        query.setParameter("orderid", orderid);
        query.setParameter("userid", userid);
        query.setParameter("openid", openid);

        if (query.uniqueResult() == null) return 3;

        OrderEventEntity event = this.addEvent(OrderStatus.CASHED, this.get(orderid, userid), null);

        Query query2 = session.createQuery(
                    "update OrderEntity " +
                            "set status=:cashedStatus, lastEvent=:lastEvent" +
                            ", paymentInfo=:paymentInfo" +
                            " where id=:orderid and status=:pendingStatus");

        query2.setParameter("cashedStatus", OrderStatus.CASHED);
        query2.setParameter("orderid", orderid);
        query2.setParameter("pendingStatus", OrderStatus.PENDING);
        query2.setParameter("paymentInfo", paymentInfo);
        query2.setParameter("lastEvent", event.getMessage());

        int affectRowsCount     = query2.executeUpdate();

        return affectRowsCount;
    }

    @Override
    @Transactional
    public OrderEventEntity addEvent(OrderStatus eventType, OrderEntity order, String message) throws DbException {

        Date now                        = new Date();
        OrderEventEntity eventEntity    = new OrderEventEntity();
        SimpleDateFormat format         = new SimpleDateFormat("yyyy年MM月dd日 HH点mm分");

        eventEntity.setEventTime(new Date());
        eventEntity.setOrder(order);
        eventEntity.setType(eventType);



        //OrderStatus
        if (OrderStatus.PENDING == eventType)
            eventEntity.setMessage("该订单创建于 " + format.format(order.getCreateTime()));

        if (OrderStatus.CANCEL == eventType)
            eventEntity.setMessage("该订单于 " + format.format(now) + " 被取消");

        if (OrderStatus.DELETED == eventType)
            eventEntity.setMessage("该订单于 " + format.format(now) + " 被删除");

        if (OrderStatus.INFO == eventType)
            eventEntity.setMessage(message);

        if (OrderStatus.CASHED == eventType)
            eventEntity.setMessage("该订单于 " + format.format(now) + " 完成支付");

        if (OrderStatus.SENDED == eventType)
            eventEntity.setMessage("您的订单已于 " + format.format(now) + " 交付 "
                    + DeliverySupport.getDeliveryName(order.getDeliveryCompany()).toString());

        if (OrderStatus.SIGNED == eventType)
            eventEntity.setMessage("该订单于 " + format.format(now)
                        + " 由 " + order.getReceiveMan() + " 签收");

        if (message != null) eventEntity.setMessage(message);

        orderEventDao.addEvent(eventEntity);

        order.setLastEvent(eventEntity.getMessage());

        return eventEntity;
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
    public OrderEntity get(String orderid, String userid) {

        Session session = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery(
                "from OrderEntity where id=:orderid"
                        + " and userId=:userid");

        query.setParameter("orderid", orderid);

        query.setParameter("userid", userid);

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

    @Override
    @Transactional
    public boolean exists(String userid, String openid, String prepayid) {

        Session session = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery(
                "from OrderEntity where openId=:openid"
                        + " and userId=:userid and prePayId=:prepayid");

        query.setParameter("openid", openid);
        query.setParameter("userid", userid);
        query.setParameter("prepayid", prepayid);

        return query.uniqueResult() != null;
    }
}
