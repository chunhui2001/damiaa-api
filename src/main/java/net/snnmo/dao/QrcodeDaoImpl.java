package net.snnmo.dao;

import net.snnmo.entity.PartnerEntity;
import net.snnmo.entity.QrcodeEntity;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by cc on 16/4/6.
 */
public class QrcodeDaoImpl implements IQrcodeDAO {

    private IPartnerDAO partnerDao;

    public IPartnerDAO getPartnerDao() {
        return partnerDao;
    }

    public void setPartnerDao(IPartnerDAO partnerDao) {
        this.partnerDao = partnerDao;
    }

    private SessionFactory sessionFactory;

    public QrcodeDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public int save(Collection<QrcodeEntity> qrcodeList) {

        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        for (QrcodeEntity entity : qrcodeList) {
            entity.setGen("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + entity.getTicket());
            session.save(entity);

            session.flush();
            session.clear();
        }

        tx.commit();
        session.close();

        return qrcodeList.size();
    }

    @Override
    public QrcodeEntity list() {
        return null;
    }

    @Override
    @Transactional
    public QrcodeEntity get(long qrcodeId) {

        Session session     = this.sessionFactory.getCurrentSession();

        return (QrcodeEntity) session.get(QrcodeEntity.class, qrcodeId);
    }

    @Override
    @Transactional
    public QrcodeEntity get(String openid) {
        Session session     = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery("from QrcodeEntity where belongTo=:belongTo");

        query.setParameter("belongTo", "openid:" + openid);

        return (QrcodeEntity)query.uniqueResult();
    }

    @Override
    @Transactional
    public int set(long qrcodeId, String belongTo) {

//        QrcodeEntity qrcodeEntity   = this.get(qrcodeId);
//        PartnerEntity partnerEntity = null;

//        if (belongTo.startsWith("unionid:")) {
//            partnerEntity =
//                    partnerDao.get(belongTo.replace("unionid:", ""));
//
//            if (partnerEntity == null) {
//                throw new OAuth2Exception("合作伙伴不存在!");
//            }
//
//            if (partnerEntity.getQrcode() != null) {
//                throw new OAuth2Exception("该合作伙伴已经有二维码了!");
//            }
//        }

//        if (partnerEntity != null) {
//            if (partnerEntity.getQrcode() == null) {
//                partnerEntity.setQrcode(qrcodeEntity.getTicket());
//
//                partnerDao.saveOrUpdate(partnerEntity);
//            }
//        }

//        if (belongTo == "userid") {
//            // 取得用户并查看该用户的身份: 仓买 or 超市,
//        }

        Session session     = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery("update QrcodeEntity set belongTo=:belongTo where id=:id");

        query.setParameter("belongTo", belongTo);
        query.setParameter("id", qrcodeId);

        return query.executeUpdate();
    }

    @Override
    @Transactional
    public Collection<QrcodeEntity> random(int count) {

        Session session = this.sessionFactory.getCurrentSession();
//
//        Criteria criteria = session.createCriteria(QrcodeEntity.class);
//
//        criteria.add(Restrictions.or(Restrictions.isNull("belongTo"), Restrictions.isEmpty("belongTo")));

        //criteria.setFetchSize(count);

        Query query = session.createQuery("from QrcodeEntity where belongTo is null");
        query.setMaxResults(count);

        return query.list();
    }
}
