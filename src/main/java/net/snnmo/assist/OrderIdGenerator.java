package net.snnmo.assist;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by cc on 16/2/14.
 */
public class OrderIdGenerator implements IdentifierGenerator, Configurable {

    private String prefix = "";

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {
        String orderid  = Common.randomNumbers(8) + Common.randomNumbers(4);

        long unixTime = System.currentTimeMillis() / 1000L;


        Serializable result = (unixTime + orderid).substring(3);

        return result;
    }

    @Override
    public void configure(org.hibernate.type.Type type, Properties properties, Dialect dialect)
            throws MappingException {

        if(properties.getProperty("prefix") != null)
            setPrefix(properties.getProperty("prefix"));
    }
}