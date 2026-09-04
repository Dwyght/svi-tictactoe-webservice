package com.svi.tictactoewebservice.filter;

import com.svi.tictactoewebservice.config.Config;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class CorsFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Volatile publication makes the immutable allowlist visible to every request thread after
     * its one-time initialization in the synchronized portion of {@link #getAllowedOrigins()}.
     */
    private static volatile Set<String> allowedOrigins;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String origin = requestContext.getHeaderString("Origin");

        if (HttpMethod.OPTIONS.equals(requestContext.getMethod()) && isAllowedOrigin(origin)) {
            requestContext.abortWith(Response.ok()
                    .header("Access-Control-Allow-Origin", origin)
                    .header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                    .header("Access-Control-Allow-Headers", "Content-Type, Accept")
                    .build());
        }
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
            ContainerResponseContext responseContext) throws IOException {
        String origin = requestContext.getHeaderString("Origin");

        if (isAllowedOrigin(origin)) {
            responseContext.getHeaders().putSingle("Access-Control-Allow-Origin", origin);
        }
    }

    private boolean isAllowedOrigin(String origin) {
        return getAllowedOrigins().contains(origin);
    }

    /**
     * Uses double-checked locking so only first-time initialization takes the class lock; the
     * second null check prevents duplicate initialization, and {@code volatile} makes later
     * lock-free reads observe the fully constructed set.
     */
    private static Set<String> getAllowedOrigins() {
        Set<String> origins = allowedOrigins;

        if (origins == null) {
            synchronized (CorsFilter.class) {
                origins = allowedOrigins;

                if (origins == null) {
                    origins = parseAllowedOrigins();
                    allowedOrigins = origins;
                }
            }
        }

        return origins;
    }

    private static Set<String> parseAllowedOrigins() {
        Set<String> origins = new HashSet<>();

        for (String configuredOrigin : Config.FRONTEND_URLS.getValue().split(",")) {
            String origin = configuredOrigin.trim();

            if (!origin.isEmpty()) {
                origins.add(origin);
            }
        }

        return Collections.unmodifiableSet(origins);
    }
}
