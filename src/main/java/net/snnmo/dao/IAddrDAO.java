package net.snnmo.dao;

import net.snnmo.entity.AddressEntity;
import net.snnmo.entity.UserEntity;
import net.snnmo.exception.DbException;

import java.util.Collection;
import java.util.List;

/**
 * Created by TTong on 16-1-12.
 */
public interface IAddrDAO {
    public AddressEntity add(UserEntity user, AddressEntity addr);
    public int delete(String userid, long addrid);
    public void update(String userid, AddressEntity addr);
    public AddressEntity get(long addrid);
    public List<AddressEntity> userAddrList(String userid);
    public void setDefault(String userid, long addrid) throws DbException;
}
