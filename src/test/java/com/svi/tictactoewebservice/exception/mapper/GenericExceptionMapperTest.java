package com.svi.tictactoewebservice.exception.mapper;

import com.svi.tictactoewebservice.dto.response.MessageResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.ws.rs.WebApplicationException;
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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GenericExceptionMapper")
class GenericExceptionMapperTest {

    private final GenericExceptionMapper mapper = new GenericExceptionMapper();
    private Logger mapperLogger;
    private Level originalLoggerLevel;
    private boolean originalUseParentHandlers;
    private RecordingLogHandler logHandler;

    @BeforeEach
    void setUpLogger() {
        mapperLogger = Logger.getLogger(GenericExceptionMapper.class.getName());
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
    @DisplayName("maps an unexpected exception to internal server error")
    void mapsUnexpectedException() {
        RuntimeException exception = new RuntimeException("sensitive detail");

        Response response = mapper.toResponse(exception);

        MessageResponse body = (MessageResponse) response.getEntity();
        assertEquals(1, logHandler.records.size());
        LogRecord logRecord = logHandler.records.get(0);
        assertAll(
                () -> assertEquals(
                        Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                        response.getStatus()),
                () -> assertEquals(MediaType.APPLICATION_JSON_TYPE, response.getMediaType()),
                () -> assertEquals(
                        "The server ran into an unexpected exception.",
                        body.getMsg()),
                () -> assertEquals(Level.SEVERE, logRecord.getLevel()),
                () -> assertTrue(logRecord.getMessage().contains("Unhandled exception")),
                () -> assertSame(exception, logRecord.getThrown()));
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
                () -> assertSame(body, response.getEntity()),
                () -> assertTrue(logHandler.records.isEmpty()));
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
