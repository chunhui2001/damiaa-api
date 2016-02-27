package net.snnmo.dao;

import net.snnmo.entity.OrderEventEntity;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

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
}
