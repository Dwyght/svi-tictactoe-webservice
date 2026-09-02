package com.svi.tictactoewebservice.config;

import com.svi.tictactoewebservice.util.FileUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;

@WebListener
public class AppStartup implements ServletContextListener {

    private static final String CONFIG_INI_LOCATION =
            "CONFIG_INI_LOCATION";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        String configLocation =
                context.getInitParameter(CONFIG_INI_LOCATION);

        if (configLocation == null
                || configLocation.trim().isEmpty()) {

            throw new IllegalStateException(
                    "CONFIG_INI_LOCATION is not configured."
            );
        }

        try (InputStream inputStream =
                     context.getResourceAsStream(configLocation)) {

            Config.setContext(inputStream);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not close configuration resource.",
                    e
            );
        }

        // Create persistence directories during application startup.
        FileUtil.getPlayersDirectory();
        FileUtil.getRoomsDirectory();
        FileUtil.getGamesDirectory();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // No resources to close yet.
        // Database cleanup will be added here later.
    }
}