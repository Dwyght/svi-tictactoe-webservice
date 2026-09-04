package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.InvalidRecordIdException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("InvalidRecordIdExceptionMapper")
class InvalidRecordIdExceptionMapperTest {

    private final InvalidRecordIdExceptionMapper mapper =
            new InvalidRecordIdExceptionMapper();

    @Test
    @DisplayName("maps unsafe record identifiers to a 400 response")
    void mapsUnsafeRecordIdToBadRequest() {
        InvalidRecordIdException exception = new InvalidRecordIdException(
                "Record ID contains unsafe path characters.");

        Response response = mapper.toResponse(exception);

        MessageResponse body = (MessageResponse) response.getEntity();
        assertAll(
                () -> assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType()),
                () -> assertEquals("Record ID contains unsafe path characters.", body.getMsg()));
    }
}
