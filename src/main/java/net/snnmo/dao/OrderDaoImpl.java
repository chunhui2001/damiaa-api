package net.snnmo.dao;

import net.snnmo.assist.*;
import net.snnmo.entity.*;
import net.snnmo.exception.DbException;
import net.snnmo.exception.UserNotExistsException;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Expression;
import java.text.SimpleDateFormat;
import java.util.*;
import net.snnmo.assist.Common;

/**
 * Created by cc on 16/2/15.
 */
public class OrderDaoImpl implements IOrderDAO {

    private IOrderEventDAO orderEventDao;
    private IUserDAO userDao;
    private IAddrDAO addrDao;
    private IGoodsDAO goodsDao;

    public IAddrDAO getAddrDao() {
        return addrDao;
    }

    public void setAddrDao(IAddrDAO addrDao) {
        this.addrDao = addrDao;
    }

    public IGoodsDAO getGoodsDao() {
        return goodsDao;
    }

    public void setGoodsDao(IGoodsDAO goodsDao) {
        this.goodsDao = goodsDao;
    }

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

        Query query = s.createQuery("from OrderEntity where userId=:userId and status != :status order by createTime desc");

        query.setParameter("userId", userid);
        query.setParameter("status", OrderStatus.DELETED);

        return query.list();
    }


    @Override
    @Transactional
    public Collection<OrderEntity> list(OrderStatus[] statusArr) {
        Session s = this.sessionFactory.getCurrentSession();

        Query query = s.createQuery("from OrderEntity where status in (:status) order by createTime desc");

        query.setParameterList("status", statusArr);

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

//    @Override
//    @Transactional
//    public OrderEntity create(UserEntity user, PayMethod payMethod, AddressEntity addr
//            , Map<GoodsEntity, Integer> goodsList) throws DbException {
//
//
//        OrderEntity order = new OrderEntity();
//
//        order.setUserId(user.getId());
//        order.setPayMethod(payMethod);
//        order.setStatus(OrderStatus.PENDING);
//        order.setAddress(addr.getProvince().split("\\(")[0] +
//                " " + addr.getCity().split("\\(")[0] +
//                " " + addr.getArea().split("\\(")[0] +
//                " " + addr.getStreet() + " " + addr.getDetail());
//        order.setPhone(addr.getLinkPone());
//        order.setReceiveMan(addr.getLinkMan());
//
//        double DELIVERY_SINGLE_COSTS = 5.00;//deliverySingleCosts
//
//        double itemMoney        = 0.00;
//        double freightMoney     = 0.00;
//        double orderMoney       = 0.00;
//        double exemptionMoney   = 0.00;
//
//        Collection<OrderItemsEntity> listOfOrderItems = new ArrayList<>();
//
//        for (Map.Entry<GoodsEntity, Integer> goodsEntityIntegerEntry : goodsList.entrySet()) {
//
//            GoodsEntity currentGoods    = goodsEntityIntegerEntry.getKey();         // 购买的商品
//            Integer currentCount        = goodsEntityIntegerEntry.getValue();       // 购买数量
//
//            double price    = 0;
//            double freight  = 0;
//
//            if (user.getRoles().indexOf(UserRole.ROLE_VIP.toString()) != -1) {
//                price   = currentGoods.getVipPrice();
//            }
//
//            if (user.getRoles().indexOf(UserRole.ROLE_SUPER_VIP.toString()) != -1) {
//                price   = currentGoods.getSuperVIPPrice();
//            }
//
//            if (price == 0)
//                price = currentGoods.getMarketPrice();
//
//            itemMoney += price * currentCount;
//
//            //count >=3 ? (0).toFixed(2) : (count + singlePrice - 1).toFixed(2);
//
//            if (currentCount >= 3) freight = 0;
//            else freight = currentCount + DELIVERY_SINGLE_COSTS - 1;
//
//            freightMoney += freight;
//
//            OrderItemsEntity orderItem = new OrderItemsEntity();
//
//            orderItem.setOrder(order);
//            orderItem.setGoods(currentGoods);
//            orderItem.setCount(currentCount);
//            orderItem.setFreight(freight);
//            orderItem.setGoodsName(currentGoods.getName());
//            orderItem.setSinglePrice(price);
//            orderItem.setTotalPrice(price * currentCount);
//            orderItem.setSpecialAttribute(null);
//
//            listOfOrderItems.add(orderItem);
//
//
//            //this.sessionFactory.getCurrentSession().save(orderItem);
//        }
//        freightMoney = 0;
//        orderMoney = itemMoney + freightMoney - exemptionMoney;
//
//        order.setExemptionMoney(exemptionMoney);         // 减免金额
//        order.setFreightMoney(freightMoney);             // 运费
//        order.setItemMoney(itemMoney);                   // 商品总金额
//        order.setOrderMoney(orderMoney);                 // 订单总金额
//        order.setOpenId(user.getOpenId());
//
//        order.setListOfItems(listOfOrderItems);
//
//
//        this.addEvent(OrderStatus.PENDING, order, null, null);
//        this.sessionFactory.getCurrentSession().save(order);
//
//        return order;
//    }



    @Override
    @Transactional
    public OrderEntity create(UserEntity user, Map<String, String> params) throws Exception {

        PayMethod payMethod                 = null;
        AddressEntity addrEntity            = null;
        Map<GoodsEntity, Integer> goodsList = new HashMap<GoodsEntity, Integer>();
        Boolean auto_create                 = false;
        String[] paramsBody                 = {"paymethod", "addrid", "auto_create", "openid", "ticket"};
        String ticket                       = null;

        if (params.containsKey("auto_create") && params.get("auto_create") == "true") {
            auto_create     = true;
        }

        if (auto_create) {
            user = userDao.getUser(params.get("openid"));
            if (user == null)
                throw new UserNotExistsException("用户未注册!");
        }


        String userId                       = user == null ? null : user.getId();
        String userOpenid                   = user == null ? null : user.getOpenId();

        for (Map.Entry<String, String> param : params.entrySet()) {

            if (Arrays.asList(paramsBody).indexOf(param.getKey().toLowerCase()) != -1) {
                if (param.getKey().toLowerCase().equals("paymethod")) {
                    if (param.getValue().equals("1")) {
                        payMethod = PayMethod.WCHAT;
                    } else if (param.getValue().equals("2")) {
                        payMethod = PayMethod.ARRIVED;
                    } else if (param.getValue().equals("3")) {
                        payMethod = PayMethod.ZHIFUBAO;
                    } else {
                        throw new DbException("支付方式有误!");
                    }
                } else if (param.getKey().toLowerCase().equals("addrid") && user != null) {
                    // 根据收获地址id去的收货地址
                    addrEntity   = addrDao.get(Integer.valueOf(param.getValue()), user.getId());

                    if (addrEntity == null)
                        throw new DbException("收货地址有误!");
                }

                if (param.getKey().toLowerCase().equals("ticket")) {
                    ticket  = param.getValue();

                    // 根据 ticket 到 QRCODES 表中找 openid
                    // 根据 openid 找 partner id
                    // TODO
                }

            } else {
                // 根据商品id取得商品
                GoodsEntity goods   = goodsDao.get(param.getKey());

                if (goods == null) {
                    throw new DbException("提供的商品id有误("+param.getKey()+")!");
                }

                goodsList.put(goods, Integer.valueOf(param.getValue()));
            }
        }

        if (payMethod == null)
            throw new DbException("请提供支付方式(paymethod)!");

        if (addrEntity == null && !auto_create)
            throw new DbException("请提供收获地址(addrid)!");

        if (goodsList.size() == 0)
            throw new DbException("请提供商品列表");

        OrderEntity order = new OrderEntity();

        order.setUserId(userId);
        order.setPayMethod(payMethod);
        order.setStatus(OrderStatus.PENDING);

        if (addrEntity != null) {
            order.setAddress(addrEntity.getAddressString());
            order.setPhone(addrEntity.getLinkPone());
            order.setReceiveMan(addrEntity.getLinkMan());
        }


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

            if (user != null) {
                if (user.getRoles().indexOf(UserRole.ROLE_VIP.toString()) != -1)
                    price   = currentGoods.getVipPrice();
                if (user.getRoles().indexOf(UserRole.ROLE_SUPER_VIP.toString()) != -1)
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

        }

        orderMoney = itemMoney + freightMoney - exemptionMoney;

        order.setExemptionMoney(exemptionMoney);         // 减免金额
        order.setFreightMoney(freightMoney);             // 运费
        order.setItemMoney(itemMoney);                   // 商品总金额
        order.setOrderMoney(orderMoney);                 // 订单总金额
        order.setOpenId(userOpenid);

        order.setListOfItems(listOfOrderItems);

        this.addEvent(OrderStatus.PENDING, order, null, null);
        this.sessionFactory.getCurrentSession().save(order);

        // TODO
        String prePayId     = Common.UnifiedOrder(order);
        order.setPrePayId(prePayId);
        this.sessionFactory.getCurrentSession().update(order);

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
        this.addEvent(status, order, null, null);
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

        OrderEventEntity event = this.addEvent(OrderStatus.CASHED, this.get(orderid, userid), null, null);

        Query query2 = session.createQuery(
                    "update OrderEntity " +
                            "set status=:cashedStatus" +
                            ", lastEventTime=:lastEventTime, lastEvent=:lastEvent" +
                            ", paymentInfo=:paymentInfo" +
                            " where id=:orderid and status=:pendingStatus");

        query2.setParameter("cashedStatus", OrderStatus.CASHED);
        query2.setParameter("orderid", orderid);
        query2.setParameter("pendingStatus", OrderStatus.PENDING);
        query2.setParameter("paymentInfo", paymentInfo);
        query2.setParameter("lastEvent", event.getMessage());
        query2.setParameter("lastEventTime", new Date());

        int affectRowsCount     = query2.executeUpdate();

        return affectRowsCount;
    }

    @Override
    @Transactional
    public OrderEventEntity addEvent(OrderStatus eventType, OrderEntity order, String message, Date eventTime) throws DbException {

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

        if (OrderStatus.SENDED == eventType) {
            Date date = orderEventDao.getPaymentTime(order.getId());

            if (date != null) {
                // 付款后　60　分钟发货
                now = Common.addMinutesToDate(60, date);
            }

            eventEntity.setMessage("您的订单已于 " + format.format(now) + " 交付 "
                    + DeliverySupport.getDeliveryName(order.getDeliveryCompany()).toString());
        }


        if (OrderStatus.SIGNED == eventType)
            eventEntity.setMessage("该订单于 " + format.format(now)
                        + " 由 " + order.getReceiveMan() + " 签收");

        if (message != null) eventEntity.setMessage(message);

        orderEventDao.addEvent(eventEntity);

        order.setLastEvent(eventEntity.getMessage());
        order.setLastEventTime(now);

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
                        + (userid.isEmpty() ? "" : " and userId=:userid"));

        query.setParameter("orderid", orderid);

        if (!userid.isEmpty())
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

    @Override
    @Transactional
    public Collection<OrderEntity> get(OrderStatus status) {

        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery(
                "from OrderEntity where status=:status");

        query.setParameter("status", status);

        return query.list();
    }

    @Override
    public OrderEntity addEvents(OrderEntity order, Collection<OrderEventEntity> eventList) {
        if (eventList == null || eventList.size() == 0) return null;

        Collection<OrderEventEntity> newEventList = new ArrayList<>();

        for (OrderEventEntity event : eventList) {
            if (order.getLastEventTime() == null || event.getEventTime().after(order.getLastEventTime()))
                newEventList.add(event);
        }

        if (newEventList.size() == 0) return null;


        Session session         = this.sessionFactory.openSession();
        Transaction tx          = session.beginTransaction();
        OrderEventEntity last   = null;

        for (OrderEventEntity event : newEventList) {
            orderEventDao.addEvent(event);
            last = event;

            session.flush();
            session.clear();
        }

        tx.commit();
        session.close();

        if (last != null) {
            order.setLastEvent(last.getMessage());
            order.setLastEventTime(last.getEventTime());
        }

        return order;
    }

    @Override
    public int cancelSended(String orderid, String userid) {

        OrderEntity orderEntity = null;
        Session session         = this.sessionFactory.openSession();

        // select EVENT_ID from ORDER_EVENTS
        // where ORDER_ID='8903611222876419605' and EVENT_TYPE='SENDED' limit 1
        Criteria criteria   = session.createCriteria(OrderEventEntity.class);

        criteria.setProjection(
                Projections.projectionList()
                        .add(Projections.property("id"), "id")
        );

        criteria.add(Restrictions.eq("order.id", orderid));
        criteria.add(Restrictions.eq("type", OrderStatus.SENDED));
        criteria.setMaxResults(1);


        Object lastEventId    = criteria.uniqueResult();

        if (lastEventId == null) {
            return 0;
        }

        Criteria criteria2  = session.createCriteria(OrderEventEntity.class);

        criteria2.add(Restrictions.eq("order.id", orderid));
        criteria2.add(Restrictions.lt("id", lastEventId));
        criteria2.addOrder(Order.desc("id"));

        criteria2.setMaxResults(1);

        OrderEventEntity lastEvent  = (OrderEventEntity) criteria2.uniqueResult();

        // 更新订单状态到上一个
//        update ORDERS set ORDER_STATUS='CASHED', LAST_EVENT='该订单于 2016年03月28日 07点57分 完成支付',
//                LAST_EVENT_TIME='2016-03-28 07:57:41', DELIVERY_COMPANY=null, DELIVERY_NO=null
//        where ORDER_ID='9123046991918944063'

        int affectRowCount  = 0;

        Transaction tx = session.beginTransaction();

        Query query = session.createQuery(
                        "update OrderEntity set status=:status" +
                                ", lastEvent=:lastEvent" +
                                ", lastEventTime=:lastEventTime" +
                                ", deliveryCompany=:deliveryCompany" +
                                ", deliveryNo=:deliveryNo where id=:orderid");

        query.setParameter("status", lastEvent.getType());
        query.setParameter("lastEvent", lastEvent.getMessage());
        query.setParameter("lastEventTime", lastEvent.getEventTime());
        query.setParameter("deliveryCompany", null);
        query.setParameter("deliveryNo", null);
        query.setParameter("orderid", orderid);


        Query query2    = session.createQuery("delete OrderEventEntity where order.id=:orderid and id>:lastId");

        query2.setParameter("lastId", lastEvent.getId());
        query2.setParameter("orderid", orderid);

        affectRowCount = query.executeUpdate() + query2.executeUpdate();

        //orderEntity = this.get(orderid, userid);

        tx.commit();

        if (affectRowCount > 0) {
            return affectRowCount;
        }

        return 0;
    }


    @Override
    @Transactional
    public Collection<OrderEventEntity> events(String orderid) {

        Session session     = this.sessionFactory.getCurrentSession();
        Criteria criteria   = session.createCriteria(OrderEventEntity.class);

        criteria.add(Restrictions.eq("order.id", orderid));
        criteria.addOrder(Order.desc("id"));

        return criteria.list();
    }
}
