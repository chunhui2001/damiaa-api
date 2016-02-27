package net.snnmo.dao;

import net.snnmo.entity.OrderEventEntity;

/**
 * Created by cc on 16/2/27.
 */
public interface IOrderEventDAO {
    public OrderEventEntity addEvent(OrderEventEntity event);
    public void deleteEvent(long eventid);
    public OrderEventEntity get(long eventid);
}
