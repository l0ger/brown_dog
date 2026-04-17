package com.maplewood.service;

import com.maplewood.dto.response.CourseResponse;
import com.maplewood.exception.ResourceNotFoundException;
import com.maplewood.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<CourseResponse> getAll() {
        return courseRepository.findAllWithDetails().stream()
                .map(CourseResponse::from)
                .toList();
    }

    public List<CourseResponse> getByGradeLevel(int gradeLevel) {
        return courseRepository.findByGradeLevel(gradeLevel).stream()
                .map(CourseResponse::from)
                .toList();
    }

    public CourseResponse getById(Long id) {
        return courseRepository.findById(id)
                .map(CourseResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + id));
    }
}
