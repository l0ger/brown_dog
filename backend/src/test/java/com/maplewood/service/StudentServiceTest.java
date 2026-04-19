package com.maplewood.service;

import com.maplewood.domain.model.*;
import com.maplewood.dto.response.EnrollmentResponse;
import com.maplewood.dto.response.StudentProgressResponse;
import com.maplewood.dto.response.StudentResponse;
import com.maplewood.exception.ResourceNotFoundException;
import com.maplewood.repository.EnrollmentRepository;
import com.maplewood.repository.StudentCourseHistoryRepository;
import com.maplewood.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StudentServiceTest {

    @Mock StudentRepository studentRepository;
    @Mock StudentCourseHistoryRepository historyRepository;
    @Mock EnrollmentRepository enrollmentRepository;
    @InjectMocks StudentService studentService;

    private Student stubStudent(Long id, String first, String last, int grade) {
        Student s = mock(Student.class);
        when(s.getId()).thenReturn(id);
        when(s.getFirstName()).thenReturn(first);
        when(s.getLastName()).thenReturn(last);
        when(s.getEmail()).thenReturn(first.toLowerCase() + "@maplewood.edu");
        when(s.getGradeLevel()).thenReturn(grade);
        when(s.getEnrollmentYear()).thenReturn(2022);
        when(s.getExpectedGraduationYear()).thenReturn(2026);
        when(s.getStatus()).thenReturn("active");
        return s;
    }

    private StudentCourseHistory stubHistory(String status, double credits) {
        Course course = mock(Course.class);
        when(course.getCredits()).thenReturn(credits);
        StudentCourseHistory h = mock(StudentCourseHistory.class);
        when(h.getCourse()).thenReturn(course);
        when(h.getStatus()).thenReturn(status);
        return h;
    }

    private Enrollment stubEnrollment(Long id, Student student) {
        Specialization spec = mock(Specialization.class);
        when(spec.getName()).thenReturn("Mathematics");
        Course course = mock(Course.class);
        when(course.getId()).thenReturn(1L);
        when(course.getCode()).thenReturn("MAT101");
        when(course.getName()).thenReturn("Algebra I");
        when(course.getDescription()).thenReturn("desc");
        when(course.getCredits()).thenReturn(3.0);
        when(course.getHoursPerWeek()).thenReturn(4);
        when(course.getCourseType()).thenReturn("core");
        when(course.getGradeLevelMin()).thenReturn(9);
        when(course.getGradeLevelMax()).thenReturn(12);
        when(course.getSemesterOrder()).thenReturn(1);
        when(course.getSpecialization()).thenReturn(spec);
        when(course.getPrerequisite()).thenReturn(null);

        Teacher teacher = mock(Teacher.class);
        when(teacher.getFirstName()).thenReturn("Jane");
        when(teacher.getLastName()).thenReturn("Doe");

        Classroom classroom = mock(Classroom.class);
        when(classroom.getName()).thenReturn("Room-101");

        Semester semester = mock(Semester.class);
        when(semester.getId()).thenReturn(1L);

        CourseSection section = mock(CourseSection.class);
        when(section.getId()).thenReturn(10L);
        when(section.getCourse()).thenReturn(course);
        when(section.getTeacher()).thenReturn(teacher);
        when(section.getClassroom()).thenReturn(classroom);
        when(section.getSemester()).thenReturn(semester);
        when(section.getDaysOfWeek()).thenReturn("MON,WED");
        when(section.getStartTime()).thenReturn("09:00");
        when(section.getEndTime()).thenReturn("10:00");

        Enrollment e = mock(Enrollment.class);
        when(e.getId()).thenReturn(id);
        when(e.getStudent()).thenReturn(student);
        when(e.getSection()).thenReturn(section);
        when(e.getStatus()).thenReturn("enrolled");
        return e;
    }

    @Test
    void getById_found_returnsStudent() {
        Student s = stubStudent(1L, "Alice", "Smith", 10);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s));

        StudentResponse result = studentService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.firstName()).isEqualTo("Alice");
        assertThat(result.gradeLevel()).isEqualTo(10);
        assertThat(result.status()).isEqualTo("active");
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getSchedule_studentNotFound_throwsResourceNotFoundException() {
        when(studentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> studentService.getSchedule(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getSchedule_found_returnsEnrollments() {
        Student s = stubStudent(1L, "Alice", "Smith", 10);
        when(studentRepository.existsById(1L)).thenReturn(true);
        Enrollment enrollment = stubEnrollment(5L, s);
        when(enrollmentRepository.findActiveByStudentId(1L)).thenReturn(List.of(enrollment));

        List<EnrollmentResponse> result = studentService.getSchedule(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo("enrolled");
        assertThat(result.get(0).studentId()).isEqualTo(1L);
    }

    @Test
    void getProgress_calculatesGpaCorrectly() {
        Student s = stubStudent(1L, "Alice", "Smith", 10);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s));
        // 2 courses × 3 credits: 1 passed, 1 failed → GPA = (3/6) * 4 = 2.0
        var h1 = stubHistory("passed", 3.0);
        var h2 = stubHistory("failed", 3.0);
        when(historyRepository.findByStudentId(1L)).thenReturn(List.of(h1, h2));

        StudentProgressResponse result = studentService.getProgress(1L);

        assertThat(result.gpa()).isEqualTo(2.0);
        assertThat(result.creditsEarned()).isEqualTo(3.0);
        assertThat(result.creditsRequired()).isEqualTo(30.0);
        assertThat(result.canGraduate()).isFalse();
        assertThat(result.fullName()).isEqualTo("Alice Smith");
    }

    @Test
    void getProgress_allPassed_canGraduate() {
        Student s = stubStudent(1L, "Alice", "Smith", 12);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s));
        // 10 courses × 3 credits = 30 earned → canGraduate
        List<StudentCourseHistory> history = List.of(
                stubHistory("passed", 3.0), stubHistory("passed", 3.0),
                stubHistory("passed", 3.0), stubHistory("passed", 3.0),
                stubHistory("passed", 3.0), stubHistory("passed", 3.0),
                stubHistory("passed", 3.0), stubHistory("passed", 3.0),
                stubHistory("passed", 3.0), stubHistory("passed", 3.0)
        );
        when(historyRepository.findByStudentId(1L)).thenReturn(history);

        StudentProgressResponse result = studentService.getProgress(1L);

        assertThat(result.gpa()).isEqualTo(4.0);
        assertThat(result.creditsEarned()).isEqualTo(30.0);
        assertThat(result.canGraduate()).isTrue();
    }

    @Test
    void getProgress_noHistory_returnsZeroGpa() {
        Student s = stubStudent(1L, "Alice", "Smith", 9);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(s));
        when(historyRepository.findByStudentId(1L)).thenReturn(List.of());

        StudentProgressResponse result = studentService.getProgress(1L);

        assertThat(result.gpa()).isEqualTo(0.0);
        assertThat(result.creditsEarned()).isEqualTo(0.0);
        assertThat(result.canGraduate()).isFalse();
    }
}
