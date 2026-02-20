package com.web.demo.exceptions;

import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(JwtProblemException.class)
    public ResponseEntity<ProblemDetail> handleJwtProblem(JwtProblemException ex) {
        ProblemDetail problem = ex.getProblemDetail();
        return ResponseEntity
                .status(problem.getStatus())
                .body(problem);
    }
}
