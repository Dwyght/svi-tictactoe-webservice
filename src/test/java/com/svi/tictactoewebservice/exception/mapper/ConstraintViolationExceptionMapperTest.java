package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConstraintViolationExceptionMapper")
class ConstraintViolationExceptionMapperTest {

    @Mock
    private ConstraintViolation<Object> violation;

    private final ConstraintViolationExceptionMapper mapper =
            new ConstraintViolationExceptionMapper();

    @Test
    @DisplayName("maps a constraint violation to bad request with the violation message")
    void mapsConstraintViolation() {
        when(violation.getMessage()).thenReturn("Player ID is required.");
        ConstraintViolationException exception = new ConstraintViolationException(
                Collections.<ConstraintViolation<?>>singleton(violation));

        Response response = mapper.toResponse(exception);

        MessageResponse body = (MessageResponse) response.getEntity();
        assertAll(
                () -> assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType()),
                () -> assertEquals("Player ID is required.", body.getMsg()));
    }

    @Test
    @DisplayName("uses the default validation message when no violation is available")
    void mapsEmptyConstraintViolationSet() {
        ConstraintViolationException exception = new ConstraintViolationException(
                Collections.<ConstraintViolation<?>>emptySet());

        Response response = mapper.toResponse(exception);

        MessageResponse body = (MessageResponse) response.getEntity();
        assertAll(
                () -> assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus()),
                () -> assertEquals("Request validation failed.", body.getMsg()));
    }
}
