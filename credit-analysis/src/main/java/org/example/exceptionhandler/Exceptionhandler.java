package org.example.exceptionhandler;

import org.example.exception.ClientNotFoundException;
import org.example.exception.IllegalArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.URI;
import java.time.LocalDateTime;

@ControllerAdvice
public class Exceptionhandler {

    static final String TIMESTAMP = "timestamp";

    @ExceptionHandler(ClientNotFoundException.class)
    public ProblemDetail clientNotFoundExceptionHandler(ClientNotFoundException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Status/404"));
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail illegalArgumentExceptionHandler(IllegalArgumentException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetail.setType(URI.create("https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Status/422"));
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
}