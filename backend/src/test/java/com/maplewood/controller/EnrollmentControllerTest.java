package com.maplewood.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maplewood.dto.request.EnrollmentRequest;
import com.maplewood.dto.response.CourseResponse;
import com.maplewood.dto.response.EnrollmentResponse;
import com.maplewood.dto.response.SectionResponse;
import com.maplewood.exception.*;
import com.maplewood.service.EnrollmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @MockBean EnrollmentService enrollmentService;

    private SectionResponse sampleSection() {
        CourseResponse course = new CourseResponse(1L, "MAT101", "Algebra I", "desc", 3.0, 4, "core", 9, 12, 1, "Mathematics", null, null);
        return new SectionResponse(10L, course, "Jane Doe", "Room-101", "MON,WED", "09:00", "10:00");
    }

    private String json(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    void enroll_validRequest_returnsOkWithEnrollment() throws Exception {
        EnrollmentResponse response = new EnrollmentResponse(50L, 1L, sampleSection(), "enrolled");
        when(enrollmentService.enroll(any(EnrollmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new EnrollmentRequest(1L, 10L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(50))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.status").value("enrolled"));
    }

    @Test
    void enroll_prerequisiteNotMet_returns422WithType() throws Exception {
        when(enrollmentService.enroll(any(EnrollmentRequest.class)))
                .thenThrow(new PrerequisiteNotMetException("Algebra I"));

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new EnrollmentRequest(1L, 10L))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("prerequisite"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Algebra I")));
    }

    @Test
    void enroll_courseOverload_returns422WithType() throws Exception {
        when(enrollmentService.enroll(any(EnrollmentRequest.class)))
                .thenThrow(new CourseOverloadException());

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new EnrollmentRequest(1L, 10L))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("max_courses"));
    }

    @Test
    void enroll_scheduleConflict_returns422WithType() throws Exception {
        when(enrollmentService.enroll(any(EnrollmentRequest.class)))
                .thenThrow(new ScheduleConflictException("English I"));

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new EnrollmentRequest(1L, 10L))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("conflict"));
    }

    @Test
    void enroll_gradeLevelViolation_returns422WithType() throws Exception {
        when(enrollmentService.enroll(any(EnrollmentRequest.class)))
                .thenThrow(new GradeLevelViolationException(9, 11, 12));

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new EnrollmentRequest(1L, 10L))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("grade_level"));
    }

    @Test
    void enroll_studentNotFound_returns404() throws Exception {
        when(enrollmentService.enroll(any(EnrollmentRequest.class)))
                .thenThrow(new ResourceNotFoundException("Student not found: 1"));

        mockMvc.perform(post("/api/enrollments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(new EnrollmentRequest(1L, 10L))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("not_found"));
    }

    @Test
    void drop_validRequest_returnsNoContent() throws Exception {
        doNothing().when(enrollmentService).drop(50L, 1L);

        mockMvc.perform(delete("/api/enrollments/50").param("studentId", "1"))
                .andExpect(status().isNoContent());

        verify(enrollmentService).drop(50L, 1L);
    }

    @Test
    void drop_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Enrollment not found: 99"))
                .when(enrollmentService).drop(99L, 1L);

        mockMvc.perform(delete("/api/enrollments/99").param("studentId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("not_found"));
    }
}
