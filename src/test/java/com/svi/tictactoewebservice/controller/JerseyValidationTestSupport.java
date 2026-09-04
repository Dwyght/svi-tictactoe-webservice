package com.svi.tictactoewebservice.controller;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.mapper.ConstraintViolationExceptionMapper;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jsonb.JsonBindingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.validation.ValidationFeature;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class JerseyValidationTestSupport extends JerseyTest {

    @BeforeEach
    void startJerseyTestContainer() throws Exception {
        super.setUp();
    }

    @AfterEach
    void stopJerseyTestContainer() throws Exception {
        super.tearDown();
    }

    <T> ResourceConfig validationApplication(
            Class<?> resourceType,
            T dependency,
            Class<T> dependencyType) {
        return new ResourceConfig()
                .register(resourceType)
                .register(new AbstractBinder() {
                    @Override
                    protected void configure() {
                        bind(dependency).to(dependencyType);
                    }
                })
                .register(ValidationFeature.class)
                .register(JsonBindingFeature.class)
                .register(ConstraintViolationExceptionMapper.class);
    }

    void assertGetRejected(String path, String expectedMessage) {
        try (Response response = target(path).request().get()) {
            assertBadRequest(response, expectedMessage);
        }
    }

    void assertPostRejected(String path, String requestBody, String expectedMessage) {
        try (Response response = target(path)
                .request()
                .post(Entity.entity(requestBody, MediaType.APPLICATION_JSON_TYPE))) {
            assertBadRequest(response, expectedMessage);
        }
    }

    private void assertBadRequest(Response response, String expectedMessage) {
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        MessageResponse body = response.readEntity(MessageResponse.class);
        assertEquals(expectedMessage, body.getMsg());
    }
}
