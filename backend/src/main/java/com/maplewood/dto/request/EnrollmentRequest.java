package com.maplewood.dto.request;

import jakarta.validation.constraints.NotNull;

public record EnrollmentRequest(
        @NotNull Long studentId,
        @NotNull Long sectionId
) {}
