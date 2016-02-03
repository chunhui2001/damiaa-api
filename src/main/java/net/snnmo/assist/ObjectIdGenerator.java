package net.snnmo.assist;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.annotations.Type;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by TTong on 16-2-3.
 */
public class ObjectIdGenerator implements IdentifierGenerator, Configurable{

    private String prefix = "";

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {

        Serializable result = prefix.concat(UUID.randomUUID().toString());

        return result;
    }

    @Override
    public void configure(org.hibernate.type.Type type, Properties properties, Dialect dialect)
            throws MappingException {

        if(properties.getProperty("prefix") != null)
            setPrefix(properties.getProperty("prefix"));
    }
}
