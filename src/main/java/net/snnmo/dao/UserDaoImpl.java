package net.snnmo.dao;

import net.snnmo.entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Created by TTong on 16-1-8.
 */
public class UserDaoImpl implements IUserDAO {
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
    @Transactional
    public void delete(String userid) {
        this.sessionFactory.getCurrentSession().delete(this.get(userid));
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
    public void addRoles(String userid, String[] listOfRoles) {
        if (listOfRoles == null || listOfRoles.length == 0) return;

        UserEntity user = this.get(userid);

        if (user == null) {
            return;
        }

        String userRoles = user.getRoles();

        if (userRoles == null) userRoles = "";

        for (String role : listOfRoles) {
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
    public void removeRoles(String userid, String[] listOfRoles) {
        if (listOfRoles == null || listOfRoles.length ==0) return;
        if (userid == null || userid.isEmpty()) return;

        UserEntity user = this.get(userid);

        if (user == null) {
            return;
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
}
