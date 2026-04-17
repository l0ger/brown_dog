package com.maplewood.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "credits", nullable = false)
    private double credits;

    @Column(name = "hours_per_week", nullable = false)
    private int hoursPerWeek;

    @Column(name = "course_type", nullable = false)
    private String courseType;

    @Column(name = "grade_level_min")
    private Integer gradeLevelMin;

    @Column(name = "grade_level_max")
    private Integer gradeLevelMax;

    @Column(name = "semester_order", nullable = false)
    private int semesterOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_id")
    private Course prerequisite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getCredits() { return credits; }
    public int getHoursPerWeek() { return hoursPerWeek; }
    public String getCourseType() { return courseType; }
    public Integer getGradeLevelMin() { return gradeLevelMin; }
    public Integer getGradeLevelMax() { return gradeLevelMax; }
    public int getSemesterOrder() { return semesterOrder; }
    public Course getPrerequisite() { return prerequisite; }
    public Specialization getSpecialization() { return specialization; }
}
