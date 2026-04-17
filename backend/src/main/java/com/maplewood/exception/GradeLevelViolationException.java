package com.maplewood.exception;

public class GradeLevelViolationException extends RuntimeException {
    public GradeLevelViolationException(int studentGrade, int min, int max) {
        super("Grade level mismatch: this course requires grades " + min + "–" + max +
              ", but student is in grade " + studentGrade + ".");
    }
}
