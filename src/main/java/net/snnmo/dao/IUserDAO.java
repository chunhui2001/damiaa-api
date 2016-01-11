package net.snnmo.dao;

import net.snnmo.entity.UserEntity;

import java.util.Collection;

/**
 * Created by TTong on 16-1-8.
 */
public interface IUserDAO {
    public Collection<UserEntity> list();
    public UserEntity get(String userid);
    public void delete(String userid);
    public void saveOrUpdate(UserEntity user);
}
