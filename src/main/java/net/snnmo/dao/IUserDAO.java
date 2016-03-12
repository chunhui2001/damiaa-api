package net.snnmo.dao;

import net.snnmo.assist.UserRole;
import net.snnmo.entity.UserEntity;

import java.util.Collection;

/**
 * Created by TTong on 16-1-8.
 */
public interface IUserDAO {
    public Collection<UserEntity> list();
    public UserEntity get(String userid);
    public void delete(String userid);
    public void create(UserEntity user);
    public void saveOrUpdate(UserEntity user);
    public UserEntity findByName(String username);
    public void addRoles(String userid, String[] listOfRoles) throws Exception;
    public void removeRoles(String userid, String[] listOfRoles) throws Exception;
    public String resetPassword(String username, String oldPwd, String newPwd) throws Exception;
    public boolean hasAnyRole(UserEntity user, UserRole[] roles);

    public boolean updatePhoto(String openid, String unionid, String photoUri);
}
