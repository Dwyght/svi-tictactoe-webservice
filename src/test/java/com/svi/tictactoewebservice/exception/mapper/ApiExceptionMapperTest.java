package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import com.svi.tictactoewebservice.exception.ApiException;
import com.svi.tictactoewebservice.exception.PlayerNameConflictException;
import com.svi.tictactoewebservice.exception.RecordNotFoundException;
import com.svi.tictactoewebservice.exception.RecordSaveException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("ApiExceptionMapper")
class ApiExceptionMapperTest {

    private final ApiExceptionMapper mapper = new ApiExceptionMapper();
    private Logger mapperLogger;
    private Level originalLoggerLevel;
    private boolean originalUseParentHandlers;
    private RecordingLogHandler logHandler;

    @BeforeEach
    void setUpLogger() {
        mapperLogger = Logger.getLogger(ApiExceptionMapper.class.getName());
        originalLoggerLevel = mapperLogger.getLevel();
        originalUseParentHandlers = mapperLogger.getUseParentHandlers();
        mapperLogger.setLevel(Level.ALL);
        mapperLogger.setUseParentHandlers(false);
        logHandler = new RecordingLogHandler();
        mapperLogger.addHandler(logHandler);
    }

    @AfterEach
    void tearDownLogger() {
        mapperLogger.removeHandler(logHandler);
        mapperLogger.setLevel(originalLoggerLevel);
        mapperLogger.setUseParentHandlers(originalUseParentHandlers);
    }

    @Test
    @DisplayName("maps RecordSaveException to unauthorized with its message")
    void mapsRecordSaveException() {
        RecordSaveException exception = new RecordSaveException("save failed");
        Response response = mapper.toResponse(exception);

        assertMessageResponse(response, Response.Status.UNAUTHORIZED, "save failed");
        assertHandledExceptionLog(exception);
    }

    @Test
    @DisplayName("maps RecordNotFoundException to its configured status with its message")
    void mapsRecordNotFoundException() {
        RecordNotFoundException exception = new RecordNotFoundException("game missing");
        Response response = mapper.toResponse(exception);

        assertMessageResponse(response, Response.Status.PAYMENT_REQUIRED, "game missing");
        assertHandledExceptionLog(exception);
    }

    @Test
    @DisplayName("maps PlayerNameConflictException to conflict with its message")
    void mapsPlayerNameConflictException() {
        PlayerNameConflictException exception =
                new PlayerNameConflictException("name taken");
        Response response = mapper.toResponse(exception);

        assertMessageResponse(response, Response.Status.CONFLICT, "name taken");
        assertHandledExceptionLog(exception);
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

    private void assertHandledExceptionLog(ApiException exception) {
        assertEquals(1, logHandler.records.size());
        LogRecord logRecord = logHandler.records.get(0);
        assertAll(
                () -> assertEquals(Level.INFO, logRecord.getLevel()),
                () -> assertNull(logRecord.getThrown()),
                () -> assertEquals(
                        "Handled API exception " + exception.getClass().getSimpleName()
                                + ": " + exception.getMessage(),
                        logRecord.getMessage()));
    }

    private static class RecordingLogHandler extends Handler {

        private final List<LogRecord> records = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            records.add(record);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }
}
