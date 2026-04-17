package com.maplewood.controller;

import com.maplewood.dto.response.EnrollmentResponse;
import com.maplewood.dto.response.StudentProgressResponse;
import com.maplewood.dto.response.StudentResponse;
import com.maplewood.service.StudentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getById(id));
    }

    @GetMapping("/{id}/schedule")
    public ResponseEntity<List<EnrollmentResponse>> getSchedule(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getSchedule(id));
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<StudentProgressResponse> getProgress(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getProgress(id));
    }
}
