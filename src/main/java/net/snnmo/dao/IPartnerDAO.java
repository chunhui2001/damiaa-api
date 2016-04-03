package net.snnmo.dao;

import net.snnmo.entity.PartnerEntity;

import java.util.Collection;

/**
 * Created by cc on 16/4/3.
 */
public interface IPartnerDAO {
    public PartnerEntity saveOrUpdate(PartnerEntity partner);
    public Collection<PartnerEntity> list();
    public PartnerEntity get(String unionid);
}
