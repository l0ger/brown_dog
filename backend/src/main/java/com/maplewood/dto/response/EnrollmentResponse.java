package com.maplewood.dto.response;

import com.maplewood.domain.model.Enrollment;

public record EnrollmentResponse(
        Long id,
        Long studentId,
        SectionResponse section,
        String status
) {
    public static EnrollmentResponse from(Enrollment e) {
        return new EnrollmentResponse(
                e.getId(),
                e.getStudent().getId(),
                SectionResponse.from(e.getSection()),
                e.getStatus()
        );
    }
}
