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
        Course course = new Course(1L, "JavaEE for Dummies",
                "JavaEE",
                4,
                "John Doe"
        );
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
        Course course = new Course(1L, "JavaEE for Dummies",
                "JavaEE",
                4,
                "John Doe"
        );
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        // When
        Course foundCourse = courseService.getCourseById(course.getId()).orElse(null);

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
        Course course = new Course("JavaEE for Dummies",
                "JavaEE",
                4,
                "John Doe"
        );
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
        Course course = new Course(1L, "JavaEE for Dummies",
                "JavaEE",
                4,
                "John Doe"
        );
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
        Iterable<Course> matchingCourses = courseService.searchSimilarCourses("Java", "Programming", 4);

        // Then
        assertThat(matchingCourses).isNotEmpty();
        assertThat(matchingCourses).hasSize(7);
        assertThat(matchingCourses).containsExactlyInAnyOrder(
                coursesInDatabase.toArray(Course[]::new)
        );
        verify(courseRepository, times(1)).searchSimilarCourses("Java", "Programming", 4);
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
        return Arrays.asList(
                new Course("Rapid Spring Boot Application Development",
                        "Spring",
                        4,
                        "John Doe"),
                new Course("Getting Started with Spring Security DSL",
                        "Spring",
                        5,
                        "John Doe"),
                new Course("Getting Started with Spring Cloud Kubernetes",
                        "Spring",
                        3,
                        "John Doe"),
                new Course("Getting Started with Python",
                        "Python",
                        5,
                        "John Doe"),
                new Course("Game Development with Python",
                        "Python",
                        3,
                        "John Doe"),
                new Course("JavaScript for All",
                        "JavaScript",
                        4,
                        "John Doe"),
                new Course("JavaScript Complete Guide",
                        "JavaScript",
                        5,
                        "John Doe")
        );
    }
}
