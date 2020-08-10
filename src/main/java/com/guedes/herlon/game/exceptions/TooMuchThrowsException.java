package com.guedes.herlon.game.exceptions;

public class TooMuchThrowsException extends RuntimeException {
    public TooMuchThrowsException(String message, Throwable err) {
        super(message, err);
    }

    public TooMuchThrowsException(String message) {
        super(message);
    }
}
