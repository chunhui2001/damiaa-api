package net.snnmo.dao;

import net.snnmo.entity.PartnerEntity;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

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
        Collection<PartnerEntity> result    = criteria.list();
        return result;
    }

    @Override
    @Transactional
    public PartnerEntity get(String partnerIdOrUnionId) {
        Session session = this.sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria(PartnerEntity.class);

        criteria.add(
                Restrictions.or(
                        Restrictions.eq("id", partnerIdOrUnionId)
                        , Restrictions.eq("unionid", partnerIdOrUnionId)));

        return (PartnerEntity)criteria.uniqueResult();
    }

    @Override
    @Transactional
    public void removeQrcode(String partnerid, int qrcodeid) {

        Session session         = this.sessionFactory.openSession();

        Query query = session.createQuery(
                "update PartnerEntity set qrcode=null" + " where id=:partnerid and qrcode=:qrcodeid");

        query.setParameter("partnerid", partnerid);
        query.setParameter("qrcodeid", qrcodeid);

        query.executeUpdate();
    }
}
