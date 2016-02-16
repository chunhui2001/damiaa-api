package net.snnmo.dao;

import net.snnmo.entity.GoodsEntity;

import java.util.Collection;

/**
 * Created by cc on 16/2/15.
 */
public interface IGoodsDAO {
    public GoodsEntity saveOrUpdate(GoodsEntity goods);
    public GoodsEntity get(String goodsid);
    public Collection<GoodsEntity> list();
}
