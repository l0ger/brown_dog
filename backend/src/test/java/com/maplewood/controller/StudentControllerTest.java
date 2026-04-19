package com.maplewood.controller;

import com.maplewood.dto.response.*;
import com.maplewood.exception.ResourceNotFoundException;
import com.maplewood.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean StudentService studentService;

    private StudentResponse sampleStudent() {
        return new StudentResponse(1L, "Alice", "Smith", "alice@maplewood.edu", 10, 2022, 2026, "active");
    }

    private SectionResponse sampleSection() {
        CourseResponse course = new CourseResponse(1L, "MAT101", "Algebra I", "desc", 3.0, 4, "core", 9, 12, 1, "Mathematics", null, null);
        return new SectionResponse(10L, course, "Jane Doe", "Room-101", "MON,WED", "09:00", "10:00");
    }

    @Test
    void getById_found_returnsOk() throws Exception {
        when(studentService.getById(1L)).thenReturn(sampleStudent());

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.lastName").value("Smith"))
                .andExpect(jsonPath("$.gradeLevel").value(10))
                .andExpect(jsonPath("$.status").value("active"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(studentService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Student not found: 99"));

        mockMvc.perform(get("/api/students/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("not_found"))
                .andExpect(jsonPath("$.message").value("Student not found: 99"));
    }

    @Test
    void getSchedule_returnsOkWithEnrollments() throws Exception {
        EnrollmentResponse enrollment = new EnrollmentResponse(5L, 1L, sampleSection(), "enrolled");
        when(studentService.getSchedule(1L)).thenReturn(List.of(enrollment));

        mockMvc.perform(get("/api/students/1/schedule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("enrolled"))
                .andExpect(jsonPath("$[0].section.teacherName").value("Jane Doe"));
    }

    @Test
    void getSchedule_studentNotFound_returns404() throws Exception {
        when(studentService.getSchedule(99L))
                .thenThrow(new ResourceNotFoundException("Student not found: 99"));

        mockMvc.perform(get("/api/students/99/schedule"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProgress_returnsOkWithGpa() throws Exception {
        StudentProgressResponse progress = new StudentProgressResponse(1L, "Alice Smith", 10, 3.5, 15.0, 30.0, false);
        when(studentService.getProgress(1L)).thenReturn(progress);

        mockMvc.perform(get("/api/students/1/progress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gpa").value(3.5))
                .andExpect(jsonPath("$.fullName").value("Alice Smith"))
                .andExpect(jsonPath("$.creditsEarned").value(15.0))
                .andExpect(jsonPath("$.canGraduate").value(false));
    }
}
