package de.kaleidox.javacord.dialogue.exception;

public class ResponseTimeoutException extends RuntimeException {
    public ResponseTimeoutException() {
        this("Response timeout reached!");
    }

    public ResponseTimeoutException(String message) {
        super(message);
    }
}
