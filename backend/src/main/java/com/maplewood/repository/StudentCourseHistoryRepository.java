package com.maplewood.repository;

import com.maplewood.domain.model.StudentCourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentCourseHistoryRepository extends JpaRepository<StudentCourseHistory, Long> {

    boolean existsByStudentIdAndCourseIdAndStatus(Long studentId, Long courseId, String status);

    @Query("SELECT h FROM StudentCourseHistory h JOIN FETCH h.course WHERE h.student.id = :studentId")
    List<StudentCourseHistory> findByStudentId(Long studentId);
}
