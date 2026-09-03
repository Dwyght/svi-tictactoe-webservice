package com.svi.tictactoewebservice.exception;

import javax.ws.rs.core.Response;

public class PlayerNameConflictException extends ApiException {

    public PlayerNameConflictException(String message) {
        super(Response.Status.CONFLICT, message);
    }
}
