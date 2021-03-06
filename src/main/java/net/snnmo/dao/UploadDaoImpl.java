package net.snnmo.dao;

import net.snnmo.entity.UserUploadEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by TTong on 16-3-11.
 */
public class UploadDaoImpl implements IUploadDAO {

    private SessionFactory sessionFactory;

    public UploadDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    @Transactional
    public UserUploadEntity add(UserUploadEntity upload) {
        this.sessionFactory.getCurrentSession().save(upload);
        return upload;
    }
}
