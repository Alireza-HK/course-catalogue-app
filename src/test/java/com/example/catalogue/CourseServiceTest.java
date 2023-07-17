package com.example.catalogue;

import com.example.catalogue.exception.CourseNotFoundException;
import com.example.catalogue.model.Course;
import com.example.catalogue.repository.CourseRepository;
import com.example.catalogue.service.CourseService;
import com.example.catalogue.service.CourseServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService = new CourseServiceImpl(courseRepository);

    @Test
    void givenCoursesInDatabase_whenFindAll_thenReturnAllCourses() {
        // Given
        List<Course> coursesInDatabase = getTestInputData();
        when(courseRepository.findAll()).thenReturn(coursesInDatabase);

        // When
        Iterable<Course> allCourses = courseService.getAllCourses();

        // Then
        assertThat(allCourses).isNotEmpty();
        assertThat(allCourses).hasSize(coursesInDatabase.size());
        assertThat(allCourses).containsExactlyElementsOf(coursesInDatabase);
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    void givenValidCourse_whenSave_thenCourseShouldBePersisted() {
        // Given
        var course = Course.builder().id(1L).name("JavaEE for Dummies").category("JavaEE").rating(4).author("John Doe").build();
        when(courseRepository.save(course)).thenReturn(course);

        // When
        Course savedCourse = courseService.createCourse(course);

        // Then
        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getName()).isEqualTo("JavaEE for Dummies");
        assertThat(savedCourse.getCategory()).isEqualTo("JavaEE");
        assertThat(savedCourse.getRating()).isEqualTo(4);
        assertThat(savedCourse.getAuthor()).isEqualTo("John Doe");
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void givenCourseInDatabase_whenFindById_thenReturnCourse() {
        // Given
        var course = Course.builder().id(1L).name("JavaEE for Dummies").category("JavaEE").rating(4).author("John Doe").build();
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        // When
        Course foundCourse = courseService.getCourseById(course.getId());

        // Then
        assertThat(foundCourse).isNotNull();
        assertThat(foundCourse.getId()).isNotNull();
        assertThat(foundCourse.getName()).isEqualTo("JavaEE for Dummies");
        assertThat(foundCourse.getCategory()).isEqualTo("JavaEE");
        assertThat(foundCourse.getRating()).isEqualTo(4);
        assertThat(foundCourse.getAuthor()).isEqualTo("John Doe");
        verify(courseRepository, times(1)).findById(course.getId());
    }

    @Test
    void givenInvalidCourseId_whenGetCourseById_thenThrowCourseNotFoundException() {
        // Given
        Long invalidCourseId = 999L;
        when(courseRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When and Then
        assertThrows(CourseNotFoundException.class, () -> courseService.getCourseById(invalidCourseId));
    }

    @Test
    void givenCourseInDatabase_whenUpdate_thenCourseShouldBeUpdated() {
        // Given
        var course = Course.builder().name("JavaEE for Dummies").category("JavaEE").rating(4).author("John Doe").build();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(course)).thenReturn(course);

        // When
        course.setName("JavaEE for Dummies - 2nd Edition");
        Course updatedCourse = courseService.updateCourse(1L, course);

        // Then
        assertThat(updatedCourse.getId()).isEqualTo(course.getId());
        assertThat(updatedCourse.getName()).isEqualTo("JavaEE for Dummies - 2nd Edition");
        assertThat(updatedCourse.getCategory()).isEqualTo("JavaEE");
        assertThat(updatedCourse.getRating()).isEqualTo(4);
        assertThat(updatedCourse.getAuthor()).isEqualTo("John Doe");
        verify(courseRepository, times(1)).save(course);
    }

    @Test
    void givenCourseInDatabase_whenDelete_thenCourseShouldBeDeleted() {
        // Given
        var course = Course.builder().id(1L).name("JavaEE for Dummies").category("JavaEE").rating(4).author("John Doe").build();
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        // When
        courseService.deleteCourseById(course.getId());

        // Then
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    void givenCoursesInDatabase_whenSearchSimilarCourses_thenReturnMatchingCourses() {
        // Given
        List<Course> coursesInDatabase = getTestInputData();
        when(courseRepository.searchSimilarCourses(anyString(), anyString(), anyInt())).thenReturn(coursesInDatabase);

        // When
        Iterable<Course> matchingCourses = courseService.searchSimilarCourses("Spring", "Programming", 5);

        // Then
        assertThat(matchingCourses).isNotEmpty();
        assertThat(matchingCourses).hasSize(6);
        assertThat(matchingCourses).containsExactlyInAnyOrder(
                coursesInDatabase.toArray(Course[]::new)
        );
        verify(courseRepository, times(1)).searchSimilarCourses("Spring", "Programming", 5);
    }

    @Test
    void givenNoMatchingCourses_whenSearchSimilarCourses_thenReturnEmptyList() {
        // Given
        when(courseRepository.searchSimilarCourses(anyString(), anyString(), anyInt())).thenReturn(Collections.emptyList());

        // When
        Iterable<Course> matchingCourses = courseService.searchSimilarCourses("Chemistry", "Science", 4);

        // Then
        assertThat(matchingCourses).isEmpty();
        verify(courseRepository, times(1)).searchSimilarCourses("Chemistry", "Science", 4);
    }

    //ToDo: Test CourseNotFoundException



    private List<Course> getTestInputData() {
        return List.of(
                Course.builder()
                        .name("JavaEE for Dummies")
                        .category("Programming")
                        .rating(3)
                        .author("John Doe").build(),
                Course.builder()
                        .name("Javascript for Beginners")
                        .category("Programming")
                        .rating(3)
                        .author("John Muller").build(),
                Course.builder()
                        .name("What Is This Thing Called Science?")
                        .category("Science")
                        .rating(5)
                        .author("Alan Chalmers").build(),
                Course.builder()
                        .name("Suomen mestari")
                        .category("Languages")
                        .rating(2)
                        .author("Sonja Gehring").build(),
                Course.builder()
                        .name("Spring in Action")
                        .category("Programming")
                        .rating(5)
                        .author("John Doe").build(),
                Course.builder()
                        .name("Spring Security For Beginners")
                        .category("Programming")
                        .rating(5)
                        .author("John Doe").build()
        );
    }
}
