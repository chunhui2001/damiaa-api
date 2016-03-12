package net.snnmo.initialize;

import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by keesh on 3/6/16.
 */
public class SystemPropertyDefaultsInitializer
    implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext)
            throws ServletException {
        TimeZone tzone = TimeZone.getTimeZone("Asia/Shanghai");
        tzone.setDefault(tzone);

        Date myDate = Calendar.getInstance(TimeZone.getDefault()).getTime();

    }
}
