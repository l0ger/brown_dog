package com.maplewood.controller;

import com.maplewood.dto.response.CourseResponse;
import com.maplewood.dto.response.SectionResponse;
import com.maplewood.exception.ResourceNotFoundException;
import com.maplewood.service.SectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SectionController.class)
class SectionControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean SectionService sectionService;

    private SectionResponse sampleSection() {
        CourseResponse course = new CourseResponse(1L, "MAT101", "Algebra I", "desc", 3.0, 4, "core", 9, 12, 1, "Mathematics", null, null);
        return new SectionResponse(10L, course, "Jane Doe", "Room-101", "MON,WED,FRI", "09:00", "10:00");
    }

    @Test
    void getAll_returnsOkWithSections() throws Exception {
        when(sectionService.getActiveSemesterSections()).thenReturn(List.of(sampleSection()));

        mockMvc.perform(get("/api/sections"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].teacherName").value("Jane Doe"))
                .andExpect(jsonPath("$[0].classroom").value("Room-101"))
                .andExpect(jsonPath("$[0].daysOfWeek").value("MON,WED,FRI"));
    }

    @Test
    void getAll_noActiveSemester_returns404() throws Exception {
        when(sectionService.getActiveSemesterSections())
                .thenThrow(new ResourceNotFoundException("No active semester found."));

        mockMvc.perform(get("/api/sections"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").value("not_found"));
    }

    @Test
    void getById_found_returnsOk() throws Exception {
        when(sectionService.getById(10L)).thenReturn(sampleSection());

        mockMvc.perform(get("/api/sections/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.course.code").value("MAT101"))
                .andExpect(jsonPath("$.startTime").value("09:00"))
                .andExpect(jsonPath("$.endTime").value("10:00"));
    }

    @Test
    void getById_notFound_returns404() throws Exception {
        when(sectionService.getById(99L))
                .thenThrow(new ResourceNotFoundException("Section not found: 99"));

        mockMvc.perform(get("/api/sections/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Section not found: 99"));
    }
}
