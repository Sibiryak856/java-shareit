package ru.practicum.shareit.exception;

import lombok.Getter;

import java.io.PrintWriter;
import java.io.StringWriter;

@Getter
public class ErrorResponse {

    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(Throwable e) {
        this.error = stackTraceToString(e);
    }

    private String stackTraceToString (Throwable e) {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        String exceptionAsString = writer.toString();
        return exceptionAsString;
    }
}
