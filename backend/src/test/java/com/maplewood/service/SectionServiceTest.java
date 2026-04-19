package com.maplewood.service;

import com.maplewood.domain.model.*;
import com.maplewood.dto.response.SectionResponse;
import com.maplewood.exception.ResourceNotFoundException;
import com.maplewood.repository.CourseSectionRepository;
import com.maplewood.repository.SemesterRepository;
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
class SectionServiceTest {

    @Mock CourseSectionRepository sectionRepository;
    @Mock SemesterRepository semesterRepository;
    @InjectMocks SectionService sectionService;

    private Semester stubSemester(Long id) {
        Semester sem = mock(Semester.class);
        when(sem.getId()).thenReturn(id);
        return sem;
    }

    private CourseSection stubSection(Long id, Semester semester) {
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
        when(teacher.getFirstName()).thenReturn("John");
        when(teacher.getLastName()).thenReturn("Smith");

        Classroom classroom = mock(Classroom.class);
        when(classroom.getName()).thenReturn("Room-101");

        CourseSection section = mock(CourseSection.class);
        when(section.getId()).thenReturn(id);
        when(section.getCourse()).thenReturn(course);
        when(section.getTeacher()).thenReturn(teacher);
        when(section.getClassroom()).thenReturn(classroom);
        when(section.getSemester()).thenReturn(semester);
        when(section.getDaysOfWeek()).thenReturn("MON,WED,FRI");
        when(section.getStartTime()).thenReturn("09:00");
        when(section.getEndTime()).thenReturn("10:00");
        return section;
    }

    @Test
    void getActiveSemesterSections_returnsForActiveSemester() {
        Semester sem = stubSemester(1L);
        when(semesterRepository.findByIsActive(1)).thenReturn(Optional.of(sem));
        CourseSection section = stubSection(10L, sem);
        when(sectionRepository.findBySemesterId(1L)).thenReturn(List.of(section));

        List<SectionResponse> result = sectionService.getActiveSemesterSections(null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(10L);
        assertThat(result.get(0).teacherName()).isEqualTo("John Smith");
        assertThat(result.get(0).daysOfWeek()).isEqualTo("MON,WED,FRI");
    }

    @Test
    void getActiveSemesterSections_withGradeLevel_callsFilteredQuery() {
        Semester sem = stubSemester(1L);
        when(semesterRepository.findByIsActive(1)).thenReturn(Optional.of(sem));
        CourseSection section = stubSection(10L, sem);
        when(sectionRepository.findBySemesterIdAndGradeLevel(1L, 10)).thenReturn(List.of(section));

        List<SectionResponse> result = sectionService.getActiveSemesterSections(10);

        assertThat(result).hasSize(1);
        verify(sectionRepository).findBySemesterIdAndGradeLevel(1L, 10);
        verify(sectionRepository, never()).findBySemesterId(any());
    }

    @Test
    void getActiveSemesterSections_noActiveSemester_throwsResourceNotFoundException() {
        when(semesterRepository.findByIsActive(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.getActiveSemesterSections(null))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("active semester");
    }

    @Test
    void getById_found_returnsSection() {
        Semester sem = stubSemester(1L);
        CourseSection section = stubSection(10L, sem);
        when(sectionRepository.findById(10L)).thenReturn(Optional.of(section));

        SectionResponse result = sectionService.getById(10L);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.teacherName()).isEqualTo("John Smith");
        assertThat(result.classroom()).isEqualTo("Room-101");
        assertThat(result.startTime()).isEqualTo("09:00");
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(sectionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sectionService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}
