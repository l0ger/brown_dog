package com.maplewood.service;

import com.maplewood.domain.model.*;
import com.maplewood.dto.request.EnrollmentRequest;
import com.maplewood.dto.response.EnrollmentResponse;
import com.maplewood.exception.*;
import com.maplewood.repository.*;
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
class EnrollmentServiceTest {

    @Mock EnrollmentRepository enrollmentRepository;
    @Mock StudentRepository studentRepository;
    @Mock CourseSectionRepository sectionRepository;
    @Mock StudentCourseHistoryRepository historyRepository;
    @InjectMocks EnrollmentService enrollmentService;

    private Student stubStudent(Long id, int grade) {
        Student s = mock(Student.class);
        when(s.getId()).thenReturn(id);
        when(s.getGradeLevel()).thenReturn(grade);
        return s;
    }

    private Course stubCourse(Long id, String name, Integer minGrade, Integer maxGrade, Course prereq) {
        Specialization spec = mock(Specialization.class);
        when(spec.getName()).thenReturn("Mathematics");
        Course c = mock(Course.class);
        when(c.getId()).thenReturn(id);
        when(c.getCode()).thenReturn("MAT" + id);
        when(c.getName()).thenReturn(name);
        when(c.getDescription()).thenReturn("desc");
        when(c.getCredits()).thenReturn(3.0);
        when(c.getHoursPerWeek()).thenReturn(4);
        when(c.getCourseType()).thenReturn("core");
        when(c.getGradeLevelMin()).thenReturn(minGrade);
        when(c.getGradeLevelMax()).thenReturn(maxGrade);
        when(c.getSemesterOrder()).thenReturn(1);
        when(c.getSpecialization()).thenReturn(spec);
        when(c.getPrerequisite()).thenReturn(prereq);
        return c;
    }

    private CourseSection stubSection(Long id, Course course, Long semesterId,
                                      String days, String start, String end) {
        Semester semester = mock(Semester.class);
        when(semester.getId()).thenReturn(semesterId);

        Teacher teacher = mock(Teacher.class);
        when(teacher.getFirstName()).thenReturn("Jane");
        when(teacher.getLastName()).thenReturn("Doe");

        Classroom classroom = mock(Classroom.class);
        when(classroom.getName()).thenReturn("Room-101");

        CourseSection section = mock(CourseSection.class);
        when(section.getId()).thenReturn(id);
        when(section.getCourse()).thenReturn(course);
        when(section.getSemester()).thenReturn(semester);
        when(section.getTeacher()).thenReturn(teacher);
        when(section.getClassroom()).thenReturn(classroom);
        when(section.getDaysOfWeek()).thenReturn(days);
        when(section.getStartTime()).thenReturn(start);
        when(section.getEndTime()).thenReturn(end);
        return section;
    }

    private Enrollment stubSavedEnrollment(Long id, Student student, CourseSection section) {
        Enrollment e = mock(Enrollment.class);
        when(e.getId()).thenReturn(id);
        when(e.getStudent()).thenReturn(student);
        when(e.getSection()).thenReturn(section);
        when(e.getStatus()).thenReturn("enrolled");
        return e;
    }

    @Test
    void enroll_success_returnsEnrollmentResponse() {
        Student student = stubStudent(1L, 10);
        Course course = stubCourse(1L, "Algebra I", 9, 12, null);
        CourseSection section = stubSection(10L, course, 5L, "MON,WED", "09:00", "10:00");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sectionRepository.findById(10L)).thenReturn(Optional.of(section));
        when(enrollmentRepository.countByStudentIdAndSectionSemesterIdAndStatus(1L, 5L, "enrolled"))
                .thenReturn(2L);
        when(enrollmentRepository.findActiveByStudentId(1L)).thenReturn(List.of());
        Enrollment saved = stubSavedEnrollment(100L, student, section);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(saved);

        EnrollmentResponse result = enrollmentService.enroll(new EnrollmentRequest(1L, 10L));

        assertThat(result.id()).isEqualTo(100L);
        assertThat(result.studentId()).isEqualTo(1L);
        assertThat(result.status()).isEqualTo("enrolled");
    }

    @Test
    void enroll_studentNotFound_throwsResourceNotFoundException() {
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(99L, 10L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void enroll_sectionNotFound_throwsResourceNotFoundException() {
        Student student = stubStudent(1L, 10);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sectionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(1L, 99L)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void enroll_prerequisiteNotMet_throwsPrerequisiteNotMetException() {
        Student student = stubStudent(1L, 10);
        Course prereq = stubCourse(2L, "Pre-Algebra", null, null, null);
        Course course = stubCourse(1L, "Algebra I", 9, 12, prereq);
        CourseSection section = stubSection(10L, course, 5L, "MON,WED", "09:00", "10:00");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sectionRepository.findById(10L)).thenReturn(Optional.of(section));
        when(historyRepository.existsByStudentIdAndCourseIdAndStatus(1L, 2L, "passed"))
                .thenReturn(false);

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(1L, 10L)))
                .isInstanceOf(PrerequisiteNotMetException.class)
                .hasMessageContaining("Pre-Algebra");
    }

    @Test
    void enroll_prerequisitePassed_proceedsToNextCheck() {
        Student student = stubStudent(1L, 10);
        Course prereq = stubCourse(2L, "Pre-Algebra", null, null, null);
        Course course = stubCourse(1L, "Algebra I", 9, 12, prereq);
        CourseSection section = stubSection(10L, course, 5L, "MON,WED", "09:00", "10:00");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sectionRepository.findById(10L)).thenReturn(Optional.of(section));
        when(historyRepository.existsByStudentIdAndCourseIdAndStatus(1L, 2L, "passed"))
                .thenReturn(true);
        when(enrollmentRepository.countByStudentIdAndSectionSemesterIdAndStatus(1L, 5L, "enrolled"))
                .thenReturn(0L);
        when(enrollmentRepository.findActiveByStudentId(1L)).thenReturn(List.of());
        Enrollment saved = stubSavedEnrollment(100L, student, section);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(saved);

        EnrollmentResponse result = enrollmentService.enroll(new EnrollmentRequest(1L, 10L));

        assertThat(result).isNotNull();
    }

    @Test
    void enroll_gradeLevelTooLow_throwsGradeLevelViolationException() {
        Student student = stubStudent(1L, 9);
        Course course = stubCourse(1L, "AP Calculus", 11, 12, null);
        CourseSection section = stubSection(10L, course, 5L, "MON,WED", "09:00", "10:00");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sectionRepository.findById(10L)).thenReturn(Optional.of(section));

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(1L, 10L)))
                .isInstanceOf(GradeLevelViolationException.class)
                .hasMessageContaining("9")
                .hasMessageContaining("11");
    }

    @Test
    void enroll_gradeLevelTooHigh_throwsGradeLevelViolationException() {
        Student student = stubStudent(1L, 12);
        Course course = stubCourse(1L, "Intro English", 9, 10, null);
        CourseSection section = stubSection(10L, course, 5L, "MON,WED", "09:00", "10:00");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sectionRepository.findById(10L)).thenReturn(Optional.of(section));

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(1L, 10L)))
                .isInstanceOf(GradeLevelViolationException.class)
                .hasMessageContaining("12");
    }

    @Test
    void enroll_courseOverload_throwsCourseOverloadException() {
        Student student = stubStudent(1L, 10);
        Course course = stubCourse(1L, "Algebra I", 9, 12, null);
        CourseSection section = stubSection(10L, course, 5L, "MON,WED", "09:00", "10:00");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sectionRepository.findById(10L)).thenReturn(Optional.of(section));
        when(enrollmentRepository.countByStudentIdAndSectionSemesterIdAndStatus(1L, 5L, "enrolled"))
                .thenReturn(5L);

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(1L, 10L)))
                .isInstanceOf(CourseOverloadException.class)
                .hasMessageContaining("5");
    }

    @Test
    void enroll_scheduleConflict_throwsScheduleConflictException() {
        Student student = stubStudent(1L, 10);
        Course course = stubCourse(1L, "Algebra I", 9, 12, null);
        // new section: MON,WED 09:00–10:00
        CourseSection newSection = stubSection(10L, course, 5L, "MON,WED", "09:00", "10:00");

        // existing enrollment: MON 09:30–10:30 → overlaps
        Course conflictCourse = stubCourse(3L, "English I", 9, 12, null);
        Semester sem = mock(Semester.class);
        when(sem.getId()).thenReturn(5L);
        CourseSection conflictSection = mock(CourseSection.class);
        when(conflictSection.getSemester()).thenReturn(sem);
        when(conflictSection.getDaysOfWeek()).thenReturn("MON");
        when(conflictSection.getStartTime()).thenReturn("09:30");
        when(conflictSection.getEndTime()).thenReturn("10:30");
        when(conflictSection.getCourse()).thenReturn(conflictCourse);

        Enrollment existing = mock(Enrollment.class);
        when(existing.getSection()).thenReturn(conflictSection);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sectionRepository.findById(10L)).thenReturn(Optional.of(newSection));
        when(enrollmentRepository.countByStudentIdAndSectionSemesterIdAndStatus(1L, 5L, "enrolled"))
                .thenReturn(2L);
        when(enrollmentRepository.findActiveByStudentId(1L)).thenReturn(List.of(existing));

        assertThatThrownBy(() -> enrollmentService.enroll(new EnrollmentRequest(1L, 10L)))
                .isInstanceOf(ScheduleConflictException.class)
                .hasMessageContaining("English I");
    }

    @Test
    void enroll_noTimeConflictDifferentDays_succeeds() {
        Student student = stubStudent(1L, 10);
        Course course = stubCourse(1L, "Algebra I", 9, 12, null);
        // new section: MON,WED 09:00–10:00
        CourseSection newSection = stubSection(10L, course, 5L, "MON,WED", "09:00", "10:00");

        // existing: TUE,THU 09:00–10:00 → no shared day → no conflict
        Semester sem = mock(Semester.class);
        when(sem.getId()).thenReturn(5L);
        CourseSection otherSection = mock(CourseSection.class);
        when(otherSection.getSemester()).thenReturn(sem);
        when(otherSection.getDaysOfWeek()).thenReturn("TUE,THU");
        when(otherSection.getStartTime()).thenReturn("09:00");
        when(otherSection.getEndTime()).thenReturn("10:00");

        Enrollment existing = mock(Enrollment.class);
        when(existing.getSection()).thenReturn(otherSection);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(sectionRepository.findById(10L)).thenReturn(Optional.of(newSection));
        when(enrollmentRepository.countByStudentIdAndSectionSemesterIdAndStatus(1L, 5L, "enrolled"))
                .thenReturn(1L);
        when(enrollmentRepository.findActiveByStudentId(1L)).thenReturn(List.of(existing));
        Enrollment saved = stubSavedEnrollment(100L, student, newSection);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(saved);

        EnrollmentResponse result = enrollmentService.enroll(new EnrollmentRequest(1L, 10L));

        assertThat(result).isNotNull();
    }

    @Test
    void drop_success_setsStatusToDropped() {
        Student student = stubStudent(1L, 10);
        Course course = stubCourse(1L, "Algebra I", 9, 12, null);
        CourseSection section = stubSection(10L, course, 5L, "MON,WED", "09:00", "10:00");

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setSection(section);
        enrollment.setStatus("enrolled");

        when(enrollmentRepository.findById(50L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(enrollment)).thenReturn(enrollment);

        enrollmentService.drop(50L, 1L);

        assertThat(enrollment.getStatus()).isEqualTo("dropped");
        verify(enrollmentRepository).save(enrollment);
    }

    @Test
    void drop_enrollmentNotFound_throwsResourceNotFoundException() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.drop(99L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void drop_wrongStudent_throwsResourceNotFoundException() {
        Student student = stubStudent(1L, 10);
        Enrollment enrollment = mock(Enrollment.class);
        when(enrollment.getStudent()).thenReturn(student);

        when(enrollmentRepository.findById(50L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.drop(50L, 2L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
