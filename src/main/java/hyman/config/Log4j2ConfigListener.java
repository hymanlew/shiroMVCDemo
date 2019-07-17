package hyman.config;


import org.apache.log4j.spi.Configurator;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Enumeration;

public class Log4j2ConfigListener implements ServletContextListener {

    private static final String KEY = "log4j.configurationFile";

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        String fileName = getContextParam(arg0);
        //Configurator.initialize("Log4j2", "classpath:" + fileName);
    }

    private String getContextParam(ServletContextEvent event) {
        Enumeration<String> names = event.getServletContext().getInitParameterNames();
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String value = event.getServletContext().getInitParameter(name);
            if (name.trim().equals(KEY)) {
                return value;
            }
        }
        return null;
    }
}
