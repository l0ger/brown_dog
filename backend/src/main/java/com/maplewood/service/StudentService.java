package com.maplewood.service;

import com.maplewood.domain.model.StudentCourseHistory;
import com.maplewood.dto.response.EnrollmentResponse;
import com.maplewood.dto.response.StudentProgressResponse;
import com.maplewood.dto.response.StudentResponse;
import com.maplewood.exception.ResourceNotFoundException;
import com.maplewood.repository.EnrollmentRepository;
import com.maplewood.repository.StudentCourseHistoryRepository;
import com.maplewood.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentCourseHistoryRepository historyRepository;
    private final EnrollmentRepository enrollmentRepository;

    public StudentService(StudentRepository studentRepository,
                          StudentCourseHistoryRepository historyRepository,
                          EnrollmentRepository enrollmentRepository) {
        this.studentRepository = studentRepository;
        this.historyRepository = historyRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public StudentResponse getById(Long id) {
        return studentRepository.findById(id)
                .map(StudentResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    public List<EnrollmentResponse> getSchedule(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found: " + studentId);
        }
        return enrollmentRepository.findActiveByStudentId(studentId).stream()
                .map(EnrollmentResponse::from)
                .toList();
    }

    public StudentProgressResponse getProgress(Long studentId) {
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        List<StudentCourseHistory> history = historyRepository.findByStudentId(studentId);

        double totalCreditsTaken = history.stream()
                .mapToDouble(h -> h.getCourse().getCredits())
                .sum();

        double creditsEarned = history.stream()
                .filter(h -> "passed".equals(h.getStatus()))
                .mapToDouble(h -> h.getCourse().getCredits())
                .sum();

        double gpa = totalCreditsTaken > 0
                ? Math.round((creditsEarned / totalCreditsTaken) * 4.0 * 100.0) / 100.0
                : 0.0;

        double creditsRequired = 30.0;

        return new StudentProgressResponse(
                student.getId(),
                student.getFirstName() + " " + student.getLastName(),
                student.getGradeLevel(),
                gpa,
                creditsEarned,
                creditsRequired,
                creditsEarned >= creditsRequired
        );
    }
}
