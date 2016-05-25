package net.snnmo.assist;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * Created by cc on 16/2/15.
 */
public class GoodsIdGenerator implements IdentifierGenerator, Configurable {

    private String prefix = "";

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {
        String goodsid  = Common.randomNumbers(6) + Common.randomNumbers(6);

        Serializable result = goodsid;

        return result;
    }

    @Override
    public void configure(org.hibernate.type.Type type, Properties properties, Dialect dialect)
            throws MappingException {

        if(properties.getProperty("prefix") != null)
            setPrefix(properties.getProperty("prefix"));
    }
}