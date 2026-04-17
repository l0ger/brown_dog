package com.maplewood.repository;

import com.maplewood.domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.prerequisite LEFT JOIN FETCH c.specialization")
    List<Course> findAllWithDetails();

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.prerequisite LEFT JOIN FETCH c.specialization WHERE c.gradeLevelMin <= :gradeLevel AND c.gradeLevelMax >= :gradeLevel")
    List<Course> findByGradeLevel(int gradeLevel);
}
