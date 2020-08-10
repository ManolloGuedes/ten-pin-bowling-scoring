package com.guedes.herlon.game.exceptions;

public class TooMuchFramesException extends RuntimeException {
    public TooMuchFramesException(String message, Throwable err) {
        super(message, err);
    }

    public TooMuchFramesException(String message) {
        super(message);
    }
}
