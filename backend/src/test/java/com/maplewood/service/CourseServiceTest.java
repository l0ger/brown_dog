package com.maplewood.service;

import com.maplewood.domain.model.Course;
import com.maplewood.domain.model.Specialization;
import com.maplewood.dto.response.CourseResponse;
import com.maplewood.exception.ResourceNotFoundException;
import com.maplewood.repository.CourseRepository;
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
class CourseServiceTest {

    @Mock CourseRepository courseRepository;
    @InjectMocks CourseService courseService;

    private Course stubCourse(Long id, String code, String name) {
        Specialization spec = mock(Specialization.class);
        when(spec.getName()).thenReturn("Mathematics");
        Course c = mock(Course.class);
        when(c.getId()).thenReturn(id);
        when(c.getCode()).thenReturn(code);
        when(c.getName()).thenReturn(name);
        when(c.getDescription()).thenReturn("desc");
        when(c.getCredits()).thenReturn(3.0);
        when(c.getHoursPerWeek()).thenReturn(4);
        when(c.getCourseType()).thenReturn("core");
        when(c.getGradeLevelMin()).thenReturn(9);
        when(c.getGradeLevelMax()).thenReturn(12);
        when(c.getSemesterOrder()).thenReturn(1);
        when(c.getSpecialization()).thenReturn(spec);
        when(c.getPrerequisite()).thenReturn(null);
        return c;
    }

    @Test
    void getAll_returnsAllCourses() {
        Course c = stubCourse(1L, "MAT101", "Algebra I");
        when(courseRepository.findAllWithDetails()).thenReturn(List.of(c));

        List<CourseResponse> result = courseService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).code()).isEqualTo("MAT101");
        assertThat(result.get(0).specialization()).isEqualTo("Mathematics");
    }

    @Test
    void getAll_empty_returnsEmptyList() {
        when(courseRepository.findAllWithDetails()).thenReturn(List.of());

        assertThat(courseService.getAll()).isEmpty();
    }

    @Test
    void getByGradeLevel_returnsCourses() {
        Course c = stubCourse(2L, "ENG101", "English I");
        when(courseRepository.findByGradeLevel(10)).thenReturn(List.of(c));

        List<CourseResponse> result = courseService.getByGradeLevel(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).code()).isEqualTo("ENG101");
    }

    @Test
    void getById_found_returnsCourse() {
        Course c = stubCourse(1L, "MAT101", "Algebra I");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(c));

        CourseResponse result = courseService.getById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Algebra I");
        assertThat(result.prerequisiteId()).isNull();
    }

    @Test
    void getById_notFound_throwsResourceNotFoundException() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getById_withPrerequisite_mapsPrerequisiteFields() {
        Course prereq = stubCourse(2L, "MAT100", "Pre-Algebra");
        Course c = stubCourse(1L, "MAT101", "Algebra I");
        when(c.getPrerequisite()).thenReturn(prereq);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(c));

        CourseResponse result = courseService.getById(1L);

        assertThat(result.prerequisiteId()).isEqualTo(2L);
        assertThat(result.prerequisiteName()).isEqualTo("Pre-Algebra");
    }
}
