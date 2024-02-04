package ru.practicum.shareit.exception;

public class IncorrectRequestException extends RuntimeException {

    public IncorrectRequestException() {
    }

    public IncorrectRequestException(String message) {
        super(message);
    }

    public IncorrectRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectRequestException(Throwable cause) {
        super(cause);
    }

    public IncorrectRequestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
