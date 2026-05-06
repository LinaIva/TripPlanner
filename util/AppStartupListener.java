package util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import util.ActivityLoggerThread;

@WebListener
public class AppStartupListener implements ServletContextListener {

    private ActivityLoggerThread loggerThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        loggerThread = new ActivityLoggerThread();
        loggerThread.start();
        System.out.println("Logger thread started");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (loggerThread != null) {
            loggerThread.stopLogger();
        }
        System.out.println("Logger thread stopped");
    }
}