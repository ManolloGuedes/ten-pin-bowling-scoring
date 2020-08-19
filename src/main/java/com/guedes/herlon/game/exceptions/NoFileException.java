package com.guedes.herlon.game.exceptions;

import java.io.IOException;

/**
 * Exception thrown when there is a problem with the input file.
 * @author herlon-guedes
 * @since 08/11/2020
 */
public class NoFileException extends IOException {
    public NoFileException(String message, Throwable err) {
        super(message, err);
    }

    public NoFileException(String message) {
        super(message);
    }
}
