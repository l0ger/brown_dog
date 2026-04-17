package com.maplewood.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("type", "not_found", "message", ex.getMessage()));
    }

    @ExceptionHandler(PrerequisiteNotMetException.class)
    public ResponseEntity<Map<String, String>> handlePrerequisite(PrerequisiteNotMetException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("type", "prerequisite", "message", ex.getMessage()));
    }

    @ExceptionHandler(ScheduleConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(ScheduleConflictException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("type", "conflict", "message", ex.getMessage()));
    }

    @ExceptionHandler(CourseOverloadException.class)
    public ResponseEntity<Map<String, String>> handleOverload(CourseOverloadException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("type", "max_courses", "message", ex.getMessage()));
    }

    @ExceptionHandler(GradeLevelViolationException.class)
    public ResponseEntity<Map<String, String>> handleGradeLevel(GradeLevelViolationException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("type", "grade_level", "message", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("type", "error", "message", ex.getMessage()));
    }
}
