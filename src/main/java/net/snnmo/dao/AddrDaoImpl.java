package net.snnmo.dao;

import net.snnmo.entity.AddressEntity;
import net.snnmo.entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
    public void add(UserEntity user, AddressEntity addr) {
        addr.setUser(user);
        this.sessionFactory.getCurrentSession().saveOrUpdate(addr);
    }

    @Override
    @Transactional
    public void delete(String userid, long addrid) {
        this.sessionFactory.getCurrentSession().delete(this.get(addrid));
    }

    @Override
    @Transactional
    public void update(String userid, AddressEntity addr) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(addr);
    }

    @Override
    @Transactional
    public AddressEntity get(long addrid) {
        return (AddressEntity)this.sessionFactory.getCurrentSession().get(AddressEntity.class, addrid);
    }


    @Override
    @Transactional
    public List<AddressEntity> userAddrList(String userid) {
        Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(AddressEntity.class);
        criteria.add(Restrictions.eq("user.id", userid));

        return criteria.list();
    }
}
