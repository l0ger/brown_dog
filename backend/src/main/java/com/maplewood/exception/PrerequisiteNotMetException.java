package com.maplewood.exception;

public class PrerequisiteNotMetException extends RuntimeException {
    public PrerequisiteNotMetException(String courseName) {
        super("Prerequisite not met: you must pass \"" + courseName + "\" before enrolling in this course.");
    }
}
