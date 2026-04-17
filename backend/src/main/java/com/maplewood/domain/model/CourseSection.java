package com.maplewood.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "course_sections")
public class CourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    // e.g. "MON,WED,FRI"
    @Column(name = "days_of_week", nullable = false)
    private String daysOfWeek;

    // e.g. "09:00"
    @Column(name = "start_time", nullable = false)
    private String startTime;

    // e.g. "10:00"
    @Column(name = "end_time", nullable = false)
    private String endTime;

    public Long getId() { return id; }
    public Course getCourse() { return course; }
    public Semester getSemester() { return semester; }
    public Teacher getTeacher() { return teacher; }
    public Classroom getClassroom() { return classroom; }
    public String getDaysOfWeek() { return daysOfWeek; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }

    public void setCourse(Course course) { this.course = course; }
    public void setSemester(Semester semester) { this.semester = semester; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }
    public void setClassroom(Classroom classroom) { this.classroom = classroom; }
    public void setDaysOfWeek(String daysOfWeek) { this.daysOfWeek = daysOfWeek; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
}
