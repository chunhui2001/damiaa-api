package net.snnmo.dao;

import net.snnmo.entity.PartnerEntity;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by cc on 16/4/3.
 */
public class PartnerDaoImpl implements IPartnerDAO {

    private SessionFactory sessionFactory;

    public PartnerDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public PartnerEntity saveOrUpdate(PartnerEntity partner) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(partner);
        return partner;
    }

    @Override
    @Transactional
    public Collection<PartnerEntity> list() {
        Session session = this.sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(PartnerEntity.class);

        //criteria.add(Restrictions.)

        return criteria.list();
    }

    @Override
    @Transactional
    public PartnerEntity get(String unionid) {
        Session session = this.sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(PartnerEntity.class);

        criteria.add(Restrictions.eq("unionid", unionid));

        return (PartnerEntity)criteria.uniqueResult();
    }
}
