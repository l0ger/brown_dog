package com.maplewood.repository;

import com.maplewood.domain.model.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseSectionRepository extends JpaRepository<CourseSection, Long> {

    @Query("SELECT s FROM CourseSection s JOIN FETCH s.course JOIN FETCH s.teacher JOIN FETCH s.classroom WHERE s.semester.id = :semesterId")
    List<CourseSection> findBySemesterId(Long semesterId);

    @Query("SELECT s FROM CourseSection s JOIN FETCH s.course c JOIN FETCH s.teacher JOIN FETCH s.classroom WHERE s.semester.id = :semesterId AND c.gradeLevelMin <= :gradeLevel AND c.gradeLevelMax >= :gradeLevel")
    List<CourseSection> findBySemesterIdAndGradeLevel(Long semesterId, int gradeLevel);

    boolean existsByCourseIdAndSemesterId(Long courseId, Long semesterId);
}
