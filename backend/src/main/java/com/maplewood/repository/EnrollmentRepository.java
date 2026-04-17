package com.maplewood.repository;

import com.maplewood.domain.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.section s JOIN FETCH s.course JOIN FETCH s.teacher JOIN FETCH s.classroom WHERE e.student.id = :studentId AND e.status = 'enrolled'")
    List<Enrollment> findActiveByStudentId(Long studentId);

    long countByStudentIdAndSectionSemesterIdAndStatus(Long studentId, Long semesterId, String status);

    boolean existsByStudentIdAndSectionCourseIdAndSectionSemesterIdAndStatus(Long studentId, Long courseId, Long semesterId, String status);

    boolean existsByStudentIdAndSectionIdAndStatus(Long studentId, Long sectionId, String status);
}
