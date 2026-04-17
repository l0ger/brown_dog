package com.maplewood.service;

import com.maplewood.domain.model.Enrollment;
import com.maplewood.dto.request.EnrollmentRequest;
import com.maplewood.dto.response.EnrollmentResponse;
import com.maplewood.exception.*;
import com.maplewood.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseSectionRepository sectionRepository;
    private final StudentCourseHistoryRepository historyRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             CourseSectionRepository sectionRepository,
                             StudentCourseHistoryRepository historyRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.sectionRepository = sectionRepository;
        this.historyRepository = historyRepository;
    }

    @Transactional
    public EnrollmentResponse enroll(EnrollmentRequest request) {
        var student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + request.studentId()));

        var section = sectionRepository.findById(request.sectionId())
                .orElseThrow(() -> new ResourceNotFoundException("Section not found: " + request.sectionId()));

        var course = section.getCourse();
        var semesterId = section.getSemester().getId();

        // Check 1: prerequisite
        if (course.getPrerequisite() != null) {
            boolean hasPassed = historyRepository.existsByStudentIdAndCourseIdAndStatus(
                    student.getId(), course.getPrerequisite().getId(), "passed");
            if (!hasPassed) {
                throw new PrerequisiteNotMetException(course.getPrerequisite().getName());
            }
        }

        // Check 2: grade level
        if (course.getGradeLevelMin() != null && course.getGradeLevelMax() != null) {
            int grade = student.getGradeLevel();
            if (grade < course.getGradeLevelMin() || grade > course.getGradeLevelMax()) {
                throw new GradeLevelViolationException(grade, course.getGradeLevelMin(), course.getGradeLevelMax());
            }
        }

        // Check 3: course overload (max 5 per semester)
        long currentCount = enrollmentRepository.countByStudentIdAndSectionSemesterIdAndStatus(
                student.getId(), semesterId, "enrolled");
        if (currentCount >= 5) {
            throw new CourseOverloadException();
        }

        // Check 4: schedule conflict
        List<Enrollment> existing = enrollmentRepository.findActiveByStudentId(student.getId());
        for (Enrollment e : existing) {
            var existingSection = e.getSection();
            if (!existingSection.getSemester().getId().equals(semesterId)) continue;
            if (hasTimeConflict(section.getDaysOfWeek(), section.getStartTime(), section.getEndTime(),
                    existingSection.getDaysOfWeek(), existingSection.getStartTime(), existingSection.getEndTime())) {
                throw new ScheduleConflictException(existingSection.getCourse().getName());
            }
        }

        var enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSection(section);
        enrollment.setStatus("enrolled");

        return EnrollmentResponse.from(enrollmentRepository.save(enrollment));
    }

    @Transactional
    public void drop(Long enrollmentId, Long studentId) {
        var enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found: " + enrollmentId));
        if (!enrollment.getStudent().getId().equals(studentId)) {
            throw new ResourceNotFoundException("Enrollment not found: " + enrollmentId);
        }
        enrollment.setStatus("dropped");
        enrollmentRepository.save(enrollment);
    }

    private boolean hasTimeConflict(String days1, String start1, String end1,
                                     String days2, String start2, String end2) {
        List<String> d1 = Arrays.asList(days1.split(","));
        List<String> d2 = Arrays.asList(days2.split(","));
        boolean sharedDay = d1.stream().anyMatch(d2::contains);
        if (!sharedDay) return false;
        // times are "HH:mm" — compare lexicographically (works for 24h format)
        return start1.compareTo(end2) < 0 && start2.compareTo(end1) < 0;
    }
}
