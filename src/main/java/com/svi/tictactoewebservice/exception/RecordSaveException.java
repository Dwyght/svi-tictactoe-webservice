package com.svi.tictactoewebservice.exception;

import javax.ws.rs.core.Response;

public class RecordSaveException extends ApiException {

    public RecordSaveException(String message) {
        super(Response.Status.UNAUTHORIZED, message);
    }

    public RecordSaveException(String message, Throwable cause) {
        super(Response.Status.UNAUTHORIZED, message);
        initCause(cause);
    }
}
