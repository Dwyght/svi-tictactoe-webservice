package com.svi.tictactoewebservice.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides startup-loaded configuration values. The CORS allowlist originates here, but its
 * thread-safe, double-checked cache is owned and safely published by {@code CorsFilter}.
 */
public enum Config {

    PLAYERS_DIRECTORY,
    ROOMS_DIRECTORY,
    GAMES_DIRECTORY,
    FRONTEND_URLS;

    private static final Properties PROPERTIES = new Properties();

    public static void setContext(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalStateException(
                    "Configuration file could not be found."
            );
        }

        try {
            PROPERTIES.clear();
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Could not load application configuration.",
                    e
            );
        }
    }

    public String getValue() {
        String value = PROPERTIES.getProperty(name());

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                    "Missing configuration: " + name()
            );
        }

        return value.trim();
    }
}
