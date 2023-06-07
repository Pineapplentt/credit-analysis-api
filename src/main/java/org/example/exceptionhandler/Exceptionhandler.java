package org.example.exceptionhandler;

import java.net.URI;
import java.time.LocalDateTime;
import org.example.exception.AnalysisNotFoundException;
import org.example.exception.ApiConnectionException;
import org.example.exception.ClientNotFoundException;
import org.example.exception.IllegalArgumentException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class Exceptionhandler {

    static final String TIMESTAMP = "timestamp";

    @ExceptionHandler(ClientNotFoundException.class)
    public ProblemDetail clientNotFoundExceptionHandler(ClientNotFoundException exception) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Status/404"));
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail illegalArgumentExceptionHandler(IllegalArgumentException exception) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problemDetail.setType(URI.create("https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Status/422"));
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(AnalysisNotFoundException.class)
    public ProblemDetail analysisNotFoundExceptionHandler(AnalysisNotFoundException exception) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setType(URI.create("https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Status/404"));
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }

    @ExceptionHandler(ApiConnectionException.class)
    public ProblemDetail apiConnectionExceptionHandler(ApiConnectionException exception) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problemDetail.setType(URI.create("https://developer.mozilla.org/pt-BR/docs/Web/HTTP/Status/503"));
        problemDetail.setProperty(TIMESTAMP, LocalDateTime.now());
        problemDetail.setDetail(exception.getMessage());
        return problemDetail;
    }
}
