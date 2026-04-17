package com.maplewood.exception;

public class ScheduleConflictException extends RuntimeException {
    public ScheduleConflictException(String conflictingCourse) {
        super("Schedule conflict with \"" + conflictingCourse + "\": the time slots overlap.");
    }
}
