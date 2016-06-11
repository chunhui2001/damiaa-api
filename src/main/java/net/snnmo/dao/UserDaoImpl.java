package net.snnmo.dao;

import net.snnmo.assist.UserRole;
import net.snnmo.entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by TTong on 16-1-8.
 */
public class UserDaoImpl implements IUserDAO {

    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    private PasswordEncoder passwordEncoder;

    private SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Collection<UserEntity> list() {
        return this.sessionFactory.getCurrentSession()
                .createCriteria(UserEntity.class)
                .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY).list();
    }

    @Override
    public UserEntity get(String userid) {
        return (UserEntity)this.sessionFactory.getCurrentSession().get(UserEntity.class, userid);
    }

    @Override
    public UserEntity getUser(String openid) {
        Session session = this.sessionFactory.getCurrentSession();
        Query query = session.createQuery("from UserEntity where openId=:openid");
        query.setParameter("openid", openid);
        return (UserEntity)query.uniqueResult();
    }

    @Override
    @Transactional
    public void delete(String userid) {
        this.sessionFactory.getCurrentSession().delete(this.get(userid));
    }

    @Override
    @Transactional
    public void create(UserEntity user) {
        user.setPasswd(passwordEncoder.encode(user.getPasswd()));
        this.sessionFactory.getCurrentSession().save(user);
    }

    @Override
    @Transactional
    public void saveOrUpdate(UserEntity user) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(user);
    }


    @Override
    @Transactional
    public UserEntity findByName(String username) {
        Criteria criteria = this.sessionFactory.getCurrentSession().createCriteria(UserEntity.class);
        criteria.add(Restrictions.eq("name", username));

        return (UserEntity)criteria.uniqueResult();
    }


    @Override
    @Transactional
    public void addRoles(String userid, String[] listOfRoles) throws Exception {
        if (listOfRoles == null || listOfRoles.length == 0) return;

        UserEntity user = this.get(userid);

        if (user == null) {
            throw new Exception("userid not valid: " + userid);
        }

        String userRoles = user.getRoles();

        if (userRoles == null) userRoles = "";

        for (String role : listOfRoles) {

            role    = UserRole.valueOf(role).toString();

            if (userRoles.indexOf(role) == -1) {
                userRoles = userRoles + "," + role;
            }
        }

        if (userRoles.charAt(0) == ',') {
            userRoles = userRoles.substring(1);
        }

        user.setRoles(userRoles);

        this.saveOrUpdate(user);
    }


    @Override
    @Transactional
    public void removeRoles(String userid, String[] listOfRoles) throws Exception {
        if (listOfRoles == null || listOfRoles.length ==0) return;
        if (userid == null || userid.isEmpty()) return;

        UserEntity user = this.get(userid);

        if (user == null) {
            throw new Exception("userid not valid: " + userid);
        }

        String userRoles = user.getRoles();

        if (userRoles == null || userRoles.isEmpty()) return;

        for (String role : listOfRoles) {
            userRoles = userRoles.replace(role+",", "");
            userRoles = userRoles.replace(","+role, "");
            userRoles = userRoles.replace(role, "");
        }

        if (userRoles.isEmpty()) userRoles = null;

        user.setRoles(userRoles);

        this.saveOrUpdate(user);
    }

    @Override
    @Transactional
    public String resetPassword(String username, String oldPwd, String newPwd)  throws Exception {
        String errorMessage = null;

        UserEntity user = this.findByName(username);

        if (user == null) {
            return errorMessage = "用户名不存在!";
        }

        String hashedPasswd     = passwordEncoder.encode(oldPwd);
        System.out.println(oldPwd);
        System.out.println(hashedPasswd);
        System.out.println(user.getPasswd());
        System.out.println(passwordEncoder.matches(oldPwd, user.getPasswd()));

        //passwordEncoder.matches(oldPwd, user.getPasswd())
        if(!passwordEncoder.matches(oldPwd, user.getPasswd())) {
            return errorMessage = "原始密码不正确!";
        }

        if(newPwd == null || newPwd.isEmpty() || (newPwd != null && (newPwd.length() < 6 || newPwd.length() > 16))) {
            return errorMessage = "新密码不能为空, 且新密码长度6-16位, 首尾不能有空格！";
        }

        user.setPasswd(passwordEncoder.encode(newPwd));

        this.saveOrUpdate(user);

        return errorMessage;
    }

    @Override
    public boolean hasAnyRole(UserEntity user, UserRole[] roles) {

        for (UserRole r : roles) {
            if (user.getRoles().indexOf(r.toString()) != -1) return true;
        }


        return false;
    }

    @Override
    @Transactional
    public boolean updatePhoto(String openid, String unionid, String photoUri) {

        Session session = this.sessionFactory.getCurrentSession();

        Query query = session.createQuery(
                "update UserEntity set photo=:photo " +
                        "where openId=:openid" +
                        ((unionid != null && !unionid.isEmpty()) ? " and unionId=:unionid" : ""));

        query.setParameter("photo", photoUri);
        query.setParameter("openid", openid);

        if (unionid != null && !unionid.isEmpty())
            query.setParameter("unionid", unionid);


        return query.executeUpdate() > 0;
    }
}
