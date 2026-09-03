package com.svi.tictactoewebservice.exception;

import javax.ws.rs.core.Response;

public class RecordNotFoundException extends ApiException {

    public RecordNotFoundException(String message) {
        super(Response.Status.PAYMENT_REQUIRED, message);
    }
}
