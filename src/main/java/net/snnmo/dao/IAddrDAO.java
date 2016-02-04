package net.snnmo.dao;

import net.snnmo.entity.AddressEntity;
import net.snnmo.entity.UserEntity;

import java.util.Collection;
import java.util.List;

/**
 * Created by TTong on 16-1-12.
 */
public interface IAddrDAO {
    public void add(UserEntity user, AddressEntity addr);
    public void delete(String userid, long addrid);
    public void update(String userid, AddressEntity addr);
    public AddressEntity get(long addrid);
    public List<AddressEntity> userAddrList(String userid);
}
