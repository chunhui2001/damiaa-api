package net.snnmo.dao;

import net.snnmo.entity.AddressEntity;
import net.snnmo.entity.UserEntity;
import net.snnmo.exception.DbException;
import org.hibernate.*;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by TTong on 16-1-12.
 */
public class AddrDaoImpl implements IAddrDAO {
    private SessionFactory sessionFactory;

    public AddrDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }


    @Override
    @Transactional
    public AddressEntity add(UserEntity user, AddressEntity addr) {
        addr.setUser(user);
        this.sessionFactory.getCurrentSession().save(addr);
        return addr;
    }

    @Override
    @Transactional
    public int delete(String userid, long addrid) {

        Session session = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery("DELETE AddressEntity "  +
                "WHERE id = :addrid and user.id = :userid");

        query.setParameter("addrid", addrid);
        query.setParameter("userid", userid);

        return query.executeUpdate();
    }

    @Override
    @Transactional
    public void update(String userid, AddressEntity addr) {
        this.sessionFactory.getCurrentSession().update(addr);
    }

    @Override
    @Transactional
    public AddressEntity get(long addrid) {
        return (AddressEntity)this.sessionFactory.getCurrentSession().get(AddressEntity.class, addrid);
    }

    @Override
    @Transactional
    public AddressEntity get(long addrid, String userid) {

        Session session = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery("from AddressEntity "  +
                "WHERE id = :addrid and user.id = :userid");


        query.setParameter("addrid", addrid);
        query.setParameter("userid", userid);

        return (AddressEntity) query.uniqueResult();
    }

    @Override
    @Transactional
    public List<AddressEntity> userAddrList(String userid) {
        Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(AddressEntity.class);
        criteria.add(Restrictions.eq("user.id", userid));
        criteria.addOrder(Order.desc("id"));

        return criteria.list();
    }


    @Override
    @Transactional
    public void setDefault(String userid, long addrid) throws DbException {

        Session session = this.sessionFactory.getCurrentSession();

        String hql = "UPDATE AddressEntity set defaults = :defaults "  +
                "WHERE id = :addrid and user.id = :userid";

        Query query = session.createQuery(hql);

        query.setParameter("defaults", true);
        query.setParameter("userid", userid);
        query.setParameter("addrid", addrid);

        int result = query.executeUpdate();

        if (result == 0) {
            throw new DbException("设置默认地址失败!");
        }

        Criteria criteria = session.createCriteria(AddressEntity.class);
        criteria.add(Restrictions.eq("defaults", true));

        List<AddressEntity> list  = criteria.list();

        System.out.println(list.size());

        if (list.size() == 1) {
            return;
        }



        hql = "UPDATE AddressEntity set defaults = :defaults "  +
                "WHERE id != :addrid and user.id = :userid";

        query = session.createQuery(hql);

        query.setParameter("defaults", false);
        query.setParameter("userid", userid);
        query.setParameter("addrid", addrid);

        result = query.executeUpdate();

        if (result == 0) {
            throw new DbException("设置默认地址失败!");
        }

       // Transaction tx = session.beginTransaction();

//        AddressEntity addr = null;
//
//        for (Iterator it = list.iterator(); it.hasNext(); ) {
//            addr = (AddressEntity)it.next();
//
//            if (addr.getId() != addrid) {
//                addr.setDefaults(false);
//System.out.println(addrid);
//                session.save(addr);
//                //session.flush();
//               // session.clear();
//            }
//        }

        //tx.commit();
       // session.close();
    }
}
