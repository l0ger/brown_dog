package com.maplewood.dto.response;

public record StudentProgressResponse(
        Long studentId,
        String fullName,
        int gradeLevel,
        double gpa,
        double creditsEarned,
        double creditsRequired,
        boolean canGraduate
) {}
