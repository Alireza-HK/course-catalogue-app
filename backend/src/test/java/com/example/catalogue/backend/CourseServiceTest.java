package com.example.catalogue.backend;

import com.example.catalogue.backend.exception.CourseNotFoundException;
import com.example.catalogue.backend.entity.CourseEntity;
import com.example.catalogue.backend.repository.CourseRepository;
import com.example.catalogue.backend.service.CourseService;
import com.example.catalogue.backend.service.CourseServiceImpl;
import com.example.catalogue.backend.testutil.CourseTestDataFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService = new CourseServiceImpl(courseRepository);

    @Test
    @DisplayName("Given courses in database, when findAll, then return all courses")
    void givenCoursesInDatabase_whenFindAll_thenReturnAllCourses() {
        // Given
        List<CourseEntity> coursesInDatabase = CourseTestDataFactory.DATA;
        when(courseRepository.findAll()).thenReturn(coursesInDatabase);

        // When
        Iterable<CourseEntity> allCourses = courseService.getAllCourses();

        // Then
        assertThat(allCourses)
                .as("All courses should not be empty")
                .isNotEmpty()
                .hasSize(coursesInDatabase.size())
                .containsExactlyElementsOf(coursesInDatabase);

        // Verify that the courseRepository.findAll() method was called once
        verify(courseRepository, times(1)).findAll();

        // Verify that no other interactions were made with the courseRepository
        verifyNoMoreInteractions(courseRepository);
    }

    @Test
    @DisplayName("Given course in database, when findById, then return course")
    void givenCourseInDatabase_whenFindById_thenReturnCourse() {
        // Given
        var course = CourseTestDataFactory.generateTestSavedCourse();
        when(courseRepository.findById(anyLong())).thenReturn(Optional.of(course));

        // When
        CourseEntity foundCourse = courseService.getCourseById(course.getId());

        // Then
        assertThat(foundCourse)
                .as("Found course should not be null")
                .isNotNull()
                .isEqualTo(course);

        // Verify that the courseRepository.findById() method was called once with the correct ID argument
        verify(courseRepository, times(1)).findById(course.getId());
    }

    @Test
    @DisplayName("Given invalid courseId, when getCourseById, then throw CourseNotFoundException")
    void givenInvalidCourseId_whenGetCourseById_thenThrowCourseNotFoundException() {
        // Given
        Long invalidCourseId = 999L;
        when(courseRepository.findById(eq(invalidCourseId))).thenReturn(Optional.empty());

        // When and Then
        String expectedErrorMessage = "Course not found with ID: " + invalidCourseId;
        assertThrows(CourseNotFoundException.class,
                () -> courseService.getCourseById(invalidCourseId),
                expectedErrorMessage);

        verify(courseRepository, times(1)).findById(eq(invalidCourseId));
        verifyNoMoreInteractions(courseRepository);
    }

    @Test
    @DisplayName("Given valid course, when createCourse, then course should be persisted")
    void givenValidCourse_whenCreateCourse_thenCourseShouldBePersisted() {
        // Given
        CourseEntity courseToSave = CourseTestDataFactory.generateTestCourseToSave();
        CourseEntity savedCourse = CourseTestDataFactory.generateTestSavedCourse();

        when(courseRepository.save(courseToSave)).thenReturn(savedCourse);

        // When
        CourseEntity result = courseService.createCourse(courseToSave);

        // Then
        assertAll("Course should be saved",
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isNotNull(),
                () -> assertThat(result.getName()).isEqualTo("JavaEE for Dummies"),
                () -> assertThat(result.getCategory()).isEqualTo("JavaEE"),
                () -> assertThat(result.getRating()).isEqualTo(4),
                () -> assertThat(result.getAuthor()).isEqualTo("John Doe")
        );

        verify(courseRepository, times(1)).save(courseToSave);
        verifyNoMoreInteractions(courseRepository);
    }

    @Test
    @DisplayName("Given course in database, when update, then course should be updated")
    void givenCourseInDatabase_whenUpdate_thenCourseShouldBeUpdated() {
        // Given
        var existingCourse = CourseTestDataFactory.generateTestSavedCourse();
        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));

        var updatedCourse = CourseEntity.builder().id(1L).name("JavaEE for Dummies - 2nd Edition").category("JavaEE").rating(4).author("John Doe").build();
        doReturn(updatedCourse).when(courseRepository).save(existingCourse);

        // When
        CourseEntity result = courseService.updateCourse(1L, updatedCourse);

        // Then
        assertAll("Course should be updated",
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getId()).isEqualTo(1L),
                () -> assertThat(result.getName()).isEqualTo("JavaEE for Dummies - 2nd Edition"),
                () -> assertThat(result.getCategory()).isEqualTo("JavaEE"),
                () -> assertThat(result.getRating()).isEqualTo(4),
                () -> assertThat(result.getAuthor()).isEqualTo("John Doe")
        );
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(existingCourse);
    }

    @Test
    @DisplayName("Given course in database, when delete, then course should be deleted")
    void givenCourseInDatabase_whenDelete_thenCourseShouldBeDeleted() {
        // Given
        var course = CourseTestDataFactory.generateTestSavedCourse();
        when(courseRepository.findById(course.getId())).thenReturn(Optional.of(course));

        // When and Then
        assertDoesNotThrow(() -> courseService.deleteCourseById(course.getId()));

        // Then
        verify(courseRepository, times(1)).deleteById(course.getId());

    }

    @Test
    @DisplayName("Given courses in database, when delete all, then all courses should be deleted")
    void givenCoursesInDatabase_whenDeleteAll_thenAllCoursesShouldBeDeleted() {
        // Given
        assertDoesNotThrow(() -> courseService.deleteCourses());

        // Then
        assertThat(courseService.getAllCourses()).isEmpty();
        verify(courseRepository, times(1)).deleteAll();
    }

    @Test
    @DisplayName("Given non-existing courseId, when deleteCourseById, then throw CourseNotFoundException")
    void givenNonExistingCourseId_whenDeleteCourseById_thenThrowCourseNotFoundException() {
        // Given
        Long nonExistingCourseId = 999L;
        when(courseRepository.findById(eq(nonExistingCourseId))).thenReturn(Optional.empty());

        // When and Then
        CourseNotFoundException exception = assertThrows(CourseNotFoundException.class,
                () -> courseService.deleteCourseById(nonExistingCourseId));

        assertThat(exception).hasMessage(String.format("No course with id %s is available", nonExistingCourseId));
        verify(courseRepository, times(1)).findById(eq(nonExistingCourseId));
    }

    @ParameterizedTest(name = "Search courses with name: {0}, category: {1}, and rating: {2}")
    @MethodSource("searchParameters")
    @DisplayName("Given courses in database, when searchSimilarCourses, then return matching courses")
    void givenCoursesInDatabase_whenSearchSimilarCourses_thenReturnMatchingCourses(
            String name, String category, int rating, List<CourseEntity> expectedCourses) {

        // Given
        when(courseRepository.searchSimilarCourses(name, category, rating)).thenReturn(expectedCourses);

        // When
        Iterable<CourseEntity> matchingCourses = courseService.searchSimilarCourses(name, category, rating);

        // Then
        assertThat(matchingCourses).hasSize(expectedCourses.size());
        assertThat(matchingCourses).containsExactlyInAnyOrderElementsOf(expectedCourses);
        verify(courseRepository, times(1)).searchSimilarCourses(name, category, rating);
    }

    static Stream<Arguments> searchParameters() {
        List<CourseEntity> testData = CourseTestDataFactory.DATA;
        return Stream.of(
                Arguments.of("Web", "", 0,
                        testData.stream()
                                .filter(c -> c.getName().contains("Web Development Bootcamp"))
                                .collect(Collectors.toList())
                ),
                Arguments.of("", "Programming", 0,
                        testData.stream()
                                .filter(c -> c.getName().contains("Java Programming 101") || c.getName().equals("Java Advanced Topics"))
                                .collect(Collectors.toList())
                ),
                Arguments.of("", "Languages", 2,
                        testData.stream()
                                .filter(c -> c.getName().contains("Spanish for Beginners"))
                                .collect(Collectors.toList())
                ),
                Arguments.of("", "", 5,
                        testData.stream()
                                .filter(c -> c.getRating() >= 5)
                                .collect(Collectors.toList())
                ),
                Arguments.of("", "", 0,
                        testData
                ),
                Arguments.of("NonExistentCourse", "NonExistentCourse", 0,
                        Collections.emptyList())
        );
    }
}
