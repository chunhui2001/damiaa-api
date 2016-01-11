package net.snnmo.dao;

import net.snnmo.entity.UserEntity;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
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
}
