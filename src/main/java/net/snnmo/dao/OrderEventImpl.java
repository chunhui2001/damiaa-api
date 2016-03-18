package net.snnmo.dao;

import net.snnmo.assist.OrderStatus;
import net.snnmo.entity.OrderEventEntity;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * Created by cc on 16/2/27.
 */
public class OrderEventImpl implements IOrderEventDAO {

    private SessionFactory sessionFactory;

    public OrderEventImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public OrderEventEntity addEvent(OrderEventEntity event) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(event);
        return event;
    }

    @Override
    @Transactional
    public void deleteEvent(long eventid) {
        this.sessionFactory.getCurrentSession().delete(this.get(eventid));
    }

    @Override
    @Transactional
    public OrderEventEntity get(long eventid) {
        return (OrderEventEntity)this.sessionFactory.getCurrentSession().get(OrderEventEntity.class, eventid);
    }

    @Override
    @Transactional
    public Date getPaymentTime(String orderid) {
        Session session = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery(
                "from OrderEventEntity where order.id=:orderid and type=:cashed");

        query.setParameter("orderid", orderid);
        query.setParameter("cashed", OrderStatus.CASHED);

        OrderEventEntity event = (OrderEventEntity)query.uniqueResult();

        if (event == null)
            return null;

        return event.getEventTime();
    }
}
