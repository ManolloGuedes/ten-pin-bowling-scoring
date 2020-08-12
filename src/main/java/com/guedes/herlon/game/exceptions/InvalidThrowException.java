package com.guedes.herlon.game.exceptions;

import java.io.IOException;

public class InvalidThrowException extends IOException {
    public InvalidThrowException(String message, Throwable err) {
        super(message, err);
    }

    public InvalidThrowException(String message) {
        super(message);
    }
}
