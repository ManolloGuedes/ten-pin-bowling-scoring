package com.guedes.herlon.game.exceptions;

import java.io.IOException;

public class NoFileException extends IOException {
    public NoFileException(String message, Throwable err) {
        super(message, err);
    }

    public NoFileException(String message) {
        super(message);
    }
}
