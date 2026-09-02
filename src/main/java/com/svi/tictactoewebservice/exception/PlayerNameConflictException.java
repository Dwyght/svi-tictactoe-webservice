package com.svi.tictactoewebservice.exception;

public class PlayerNameConflictException extends RuntimeException {

    public PlayerNameConflictException(String message) {
        super(message);
    }
}
