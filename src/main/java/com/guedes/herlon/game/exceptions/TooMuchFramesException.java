package com.guedes.herlon.game.exceptions;

/**
 * Exception related to attempting to overflow the frame limit.
 * @author herlon-guedes
 * @since 08/10/2020
 */
public class TooMuchFramesException extends RuntimeException {
    public TooMuchFramesException(String message, Throwable err) {
        super(message, err);
    }

    public TooMuchFramesException(String message) {
        super(message);
    }
}
