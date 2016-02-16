package net.snnmo.dao;

import net.snnmo.entity.GoodsEntity;
import net.snnmo.entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

import javax.transaction.Transactional;
import java.util.Collection;

/**
 * Created by cc on 16/2/16.
 */
public class GoodsDaoImpl implements IGoodsDAO {

    private SessionFactory sessionFactory;

    public GoodsDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public GoodsEntity saveOrUpdate(GoodsEntity goods) {

        this.sessionFactory.getCurrentSession().saveOrUpdate(goods);

        return goods;
    }

    @Override
    @Transactional
    public GoodsEntity get(String goodsid) {
        return (GoodsEntity)this.sessionFactory.getCurrentSession().get(GoodsEntity.class, goodsid);

//        Object g = this.sessionFactory.getCurrentSession().get(GoodsEntity.class, goodsid);
//
//        return g == null ? null : (GoodsEntity)g;
    }

    @Override
    @Transactional
    public Collection<GoodsEntity> list() {
        return this.sessionFactory.getCurrentSession()
                .createCriteria(GoodsEntity.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }
}
