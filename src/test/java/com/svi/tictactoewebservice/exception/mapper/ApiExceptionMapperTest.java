package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.PlayerNameConflictException;
import com.svi.tictactoewebservice.exception.RecordNotFoundException;
import com.svi.tictactoewebservice.exception.RecordSaveException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ApiExceptionMapper")
class ApiExceptionMapperTest {

    private final ApiExceptionMapper mapper = new ApiExceptionMapper();

    @Test
    @DisplayName("maps RecordSaveException to unauthorized with its message")
    void mapsRecordSaveException() {
        Response response = mapper.toResponse(new RecordSaveException("save failed"));

        assertMessageResponse(response, Response.Status.UNAUTHORIZED, "save failed");
    }

    @Test
    @DisplayName("maps RecordNotFoundException to its configured status with its message")
    void mapsRecordNotFoundException() {
        Response response = mapper.toResponse(new RecordNotFoundException("game missing"));

        assertMessageResponse(response, Response.Status.PAYMENT_REQUIRED, "game missing");
    }

    @Test
    @DisplayName("maps PlayerNameConflictException to conflict with its message")
    void mapsPlayerNameConflictException() {
        Response response = mapper.toResponse(new PlayerNameConflictException("name taken"));

        assertMessageResponse(response, Response.Status.CONFLICT, "name taken");
    }

    private void assertMessageResponse(
            Response response,
            Response.Status expectedStatus,
            String expectedMessage) {
        MessageResponse body = (MessageResponse) response.getEntity();
        assertAll(
                () -> assertEquals(expectedStatus.getStatusCode(), response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType()),
                () -> assertEquals(expectedMessage, body.getMsg()));
    }
}
