package com.maplewood.controller;

import com.maplewood.dto.response.CourseResponse;
import com.maplewood.service.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAll(
            @RequestParam(required = false) Integer gradeLevel) {
        if (gradeLevel != null) {
            return ResponseEntity.ok(courseService.getByGradeLevel(gradeLevel));
        }
        return ResponseEntity.ok(courseService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getById(id));
    }
}
