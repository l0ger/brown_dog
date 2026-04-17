package com.maplewood.dto.response;

import com.maplewood.domain.model.Student;

public record StudentResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        int gradeLevel,
        int enrollmentYear,
        Integer expectedGraduationYear,
        String status
) {
    public static StudentResponse from(Student s) {
        return new StudentResponse(
                s.getId(), s.getFirstName(), s.getLastName(), s.getEmail(),
                s.getGradeLevel(), s.getEnrollmentYear(), s.getExpectedGraduationYear(), s.getStatus()
        );
    }
}
