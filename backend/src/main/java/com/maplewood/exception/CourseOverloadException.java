package com.maplewood.exception;

public class CourseOverloadException extends RuntimeException {
    public CourseOverloadException() {
        super("Cannot enroll: maximum of 5 courses per semester has been reached.");
    }
}
