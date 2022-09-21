package io.github.davidhallj.stache.exception;

public class StacheException extends RuntimeException {

    public StacheException() {
    }

    public StacheException(String message) {
        super(message);
    }

    public StacheException(String message, Throwable cause) {
        super(message, cause);
    }

}
