package com.maplewood.controller;

import com.maplewood.dto.response.CourseResponse;
import com.maplewood.exception.ResourceNotFoundException;
import com.maplewood.service.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
class CourseControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean CourseService courseService;

    private CourseResponse sampleCourse() {
        return new CourseResponse(1L, "MAT101", "Algebra I", "desc", 3.0, 4, "core", 9, 12, 1, "Mathematics", null, null);
    }

    @Test
    void getAll_returnsOkWithCourseList() throws Exception {
        when(courseService.getAll()).thenReturn(List.of(sampleCourse()));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("MAT101"))
                .andExpect(jsonPath("$[0].specialization").value("Mathematics"))
                .andExpect(jsonPath("$[0].credits").value(3.0));
    }

    @Test
    void getAll_withGradeLevel_delegatesToGradeLevelFilter() throws Exception {
        when(courseService.getByGradeLevel(10)).thenReturn(List.of(sampleCourse()));

        mockMvc.perform(get("/api/courses").param("gradeLevel", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("MAT101"));

        verify(courseService).getByGradeLevel(10);
        verify(courseService, never()).getAll();
    }

    @Test
    void getById_found_returnsOk() throws Exception {
        when(courseService.getById(1L)).thenReturn(sampleCourse());

        mockMvc.perform(get("/api/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Algebra I"))
                .andExpect(jsonPath("$.gradeLevelMin").value(9))
                .andExpect(jsonPath("$.gradeLevelMax").value(12));
    }

    @Test
    void getById_notFound_returns404WithErrorBody() throws Exception {
        when(courseService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Course not found: 99"));

        mockMvc.perform(get("/api/courses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("not_found"))
                .andExpect(jsonPath("$.message").value("Course not found: 99"));
    }
}
