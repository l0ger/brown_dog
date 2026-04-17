package com.maplewood.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "grade_level", nullable = false)
    private int gradeLevel;

    @Column(name = "enrollment_year", nullable = false)
    private int enrollmentYear;

    @Column(name = "expected_graduation_year")
    private Integer expectedGraduationYear;

    @Column(name = "status")
    private String status;

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public int getGradeLevel() { return gradeLevel; }
    public int getEnrollmentYear() { return enrollmentYear; }
    public Integer getExpectedGraduationYear() { return expectedGraduationYear; }
    public String getStatus() { return status; }
}
