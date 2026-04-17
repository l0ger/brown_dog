package com.maplewood.controller;

import com.maplewood.dto.request.EnrollmentRequest;
import com.maplewood.dto.response.EnrollmentResponse;
import com.maplewood.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<EnrollmentResponse> enroll(@Valid @RequestBody EnrollmentRequest request) {
        return ResponseEntity.ok(enrollmentService.enroll(request));
    }

    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<Void> drop(
            @PathVariable Long enrollmentId,
            @RequestParam Long studentId) {
        enrollmentService.drop(enrollmentId, studentId);
        return ResponseEntity.noContent().build();
    }
}
