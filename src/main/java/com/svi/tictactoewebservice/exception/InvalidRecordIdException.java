package com.svi.tictactoewebservice.exception;

public class InvalidRecordIdException extends IllegalArgumentException {

    public InvalidRecordIdException(String message) {
        super(message);
    }
}
