package com.maplewood.dto.response;

import com.maplewood.domain.model.CourseSection;

public record SectionResponse(
        Long id,
        CourseResponse course,
        String teacherName,
        String classroom,
        String daysOfWeek,
        String startTime,
        String endTime
) {
    public static SectionResponse from(CourseSection s) {
        return new SectionResponse(
                s.getId(),
                CourseResponse.from(s.getCourse()),
                s.getTeacher().getFirstName() + " " + s.getTeacher().getLastName(),
                s.getClassroom().getName(),
                s.getDaysOfWeek(),
                s.getStartTime(),
                s.getEndTime()
        );
    }
}
