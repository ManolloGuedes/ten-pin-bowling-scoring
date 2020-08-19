package com.guedes.herlon.game.exceptions;

/**
 * Exception related to attempting to overflow the limit of throws.
 * @author herlon-guedes
 * @since 08/10/2020
 */
public class TooMuchThrowsException extends RuntimeException {
    public TooMuchThrowsException(String message, Throwable err) {
        super(message, err);
    }

    public TooMuchThrowsException(String message) {
        super(message);
    }
}
