package com.maplewood.repository;

import com.maplewood.domain.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query("SELECT t FROM Teacher t JOIN FETCH t.specialization")
    List<Teacher> findAll();
}
