package com.example.catalogue.backend.exception.handler;

import com.example.catalogue.backend.exception.CourseNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CourseTrackerGlobalExceptionHandler {

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<String> handleCourseNotFound(CourseNotFoundException ex, WebRequest request) {
        return ResponseEntity.notFound().build();
    }
}
