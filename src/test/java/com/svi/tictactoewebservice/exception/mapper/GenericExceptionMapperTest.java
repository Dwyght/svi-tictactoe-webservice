package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

@DisplayName("GenericExceptionMapper")
class GenericExceptionMapperTest {

    private final GenericExceptionMapper mapper = new GenericExceptionMapper();

    @Test
    @DisplayName("maps an unexpected exception to internal server error")
    void mapsUnexpectedException() {
        Response response = mapper.toResponse(new RuntimeException("sensitive detail"));

        MessageResponse body = (MessageResponse) response.getEntity();
        assertAll(
                () -> assertEquals(
                        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                        response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType()),
                () -> assertEquals(
                        "The server ran into an unexpected exception.",
                        body.getMsg()));
    }

    @Test
    @DisplayName("preserves the response from a WebApplicationException")
    void preservesWebApplicationExceptionResponse() {
        MessageResponse body = new MessageResponse("resource unavailable");
        Response originalResponse = Response
                .status(Response.Status.SERVICE_UNAVAILABLE)
                .type(MediaType.APPLICATION_JSON)
                .entity(body)
                .build();

        Response response = mapper.toResponse(new WebApplicationException(originalResponse));

        assertAll(
                () -> assertSame(originalResponse, response),
                () -> assertEquals(
                        Response.Status.SERVICE_UNAVAILABLE.getStatusCode(),
                        response.getStatus()),
                () -> assertSame(body, response.getEntity()));
    }
}
