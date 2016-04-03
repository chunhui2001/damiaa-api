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

    private String prefix   = "";
    private int     len     = -1;

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Serializable generate(SessionImplementor sessionImplementor, Object o) throws HibernateException {

        String orderid          = Common.randomNumbers(this.len == -1 ? 5 : this.len);
        Serializable result     = null;

        if (this.len == -1) {
            long unixTime = System.currentTimeMillis() / 1000L;
            result = (unixTime + orderid).substring(3);
        } else {
            result = prefix.concat(orderid);
        }

        return result;
    }

    @Override
    public void configure(org.hibernate.type.Type type, Properties properties, Dialect dialect)
            throws MappingException {

        if(properties.getProperty("prefix") != null)
            setPrefix(properties.getProperty("prefix"));

        if(properties.getProperty("len") != null)
            setLen( Integer.parseInt(properties.getProperty("len")));
    }
}