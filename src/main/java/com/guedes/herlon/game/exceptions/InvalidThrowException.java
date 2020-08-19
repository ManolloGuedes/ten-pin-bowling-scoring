package com.guedes.herlon.game.exceptions;

import java.io.IOException;

/**
 * Exception thrown when there is a problem with a PlayerThrow.
 * @author herlon-guedes
 * @since 08/12/2020
 */
public class InvalidThrowException extends IOException {
    public InvalidThrowException(String message, Throwable err) {
        super(message, err);
    }

    public InvalidThrowException(String message) {
        super(message);
    }
}
