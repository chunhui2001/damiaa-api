package net.snnmo.dao;

import net.snnmo.entity.GoodsEntity;
import org.hibernate.SessionFactory;

import javax.transaction.Transactional;

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
}
