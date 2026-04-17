package com.maplewood.dto.response;

import com.maplewood.domain.model.Course;

public record CourseResponse(
        Long id,
        String code,
        String name,
        String description,
        double credits,
        int hoursPerWeek,
        String courseType,
        Integer gradeLevelMin,
        Integer gradeLevelMax,
        int semesterOrder,
        String specialization,
        Long prerequisiteId,
        String prerequisiteName
) {
    public static CourseResponse from(Course c) {
        return new CourseResponse(
                c.getId(), c.getCode(), c.getName(), c.getDescription(),
                c.getCredits(), c.getHoursPerWeek(), c.getCourseType(),
                c.getGradeLevelMin(), c.getGradeLevelMax(), c.getSemesterOrder(),
                c.getSpecialization() != null ? c.getSpecialization().getName() : null,
                c.getPrerequisite() != null ? c.getPrerequisite().getId() : null,
                c.getPrerequisite() != null ? c.getPrerequisite().getName() : null
        );
    }
}
