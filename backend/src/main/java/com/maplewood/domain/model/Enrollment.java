package com.maplewood.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "enrollments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "section_id"}))
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private CourseSection section;

    // "enrolled" | "dropped"
    @Column(name = "status", nullable = false)
    private String status = "enrolled";

    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public CourseSection getSection() { return section; }
    public String getStatus() { return status; }

    public void setStudent(Student student) { this.student = student; }
    public void setSection(CourseSection section) { this.section = section; }
    public void setStatus(String status) { this.status = status; }
}
