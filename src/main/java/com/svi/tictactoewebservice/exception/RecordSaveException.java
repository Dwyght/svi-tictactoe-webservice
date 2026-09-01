package com.svi.tictactoewebservice.exception;

public class RecordSaveException extends RuntimeException {

    public RecordSaveException(String message) {
        super(message);
    }

    public RecordSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}