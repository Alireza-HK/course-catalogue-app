package com.example.catalogue.backend;

import com.example.catalogue.backend.model.Course;
import com.example.catalogue.backend.repository.CourseRepository;
import com.example.catalogue.backend.testutil.CourseTestDataFactory;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        courseRepository.saveAll(CourseTestDataFactory.DATA);
    }

    @Test
    @DisplayName("Given course in database, when findById, then return course")
    void givenCourseInDatabase_whenFindById_thenReturnCourse() {
        // Given
        var course = CourseTestDataFactory.generateTestCourseToSave();
        courseRepository.save(course);

        // When
        Course foundCourse = courseRepository.findById(course.getId()).orElse(null);

        // Then
        assertAll("Course details",
                () -> assertThat(foundCourse).isNotNull(),
                () -> assertThat(foundCourse.getId()).isNotNull(),
                () -> assertThat(foundCourse.getName()).isEqualTo("JavaEE for Dummies"),
                () -> assertThat(foundCourse.getCategory()).isEqualTo("JavaEE"),
                () -> assertThat(foundCourse.getRating()).isEqualTo(4),
                () -> assertThat(foundCourse.getAuthor()).isEqualTo("John Doe")
        );
    }

    @ParameterizedTest(name = "Find course by ID: {0}")
    @ValueSource(longs = {-1, 100, 9999})
    @DisplayName("Given course in database, when findById with non-existing course ID, then return empty optional")
    void givenCourseInDatabase_whenFindByIdWithNonExistingId_thenReturnEmptyOptional(Long courseId) {
        // When
        Optional<Course> foundCourse = courseRepository.findById(courseId);

        // Then
        assertThat(foundCourse).isEmpty();
    }

    @Test
    @DisplayName("Given course, when save, then course should be persisted")
    void givenCourse_whenSave_thenCourseShouldBePersisted() {
        // Given
        var course = CourseTestDataFactory.generateTestCourseToSave();
        long initialCount = courseRepository.count();

        // When
        Course savedCourse = courseRepository.save(course);

        // Then
        assertThat(courseRepository.count()).isEqualTo(initialCount + 1);
        assertAll("Course details",
                () -> assertThat(savedCourse).isNotNull(),
                () -> assertThat(savedCourse.getId()).isNotNull(),
                () -> assertThat(savedCourse.getName()).isEqualTo("JavaEE for Dummies"),
                () -> assertThat(savedCourse.getCategory()).isEqualTo("JavaEE"),
                () -> assertThat(savedCourse.getRating()).isEqualTo(4),
                () -> assertThat(savedCourse.getAuthor()).isEqualTo("John Doe")
        );
    }

    @Test
    @DisplayName("Given course in database, when update, then course should be updated")
    void givenCourseInDatabase_whenUpdate_thenCourseShouldBeUpdated() {
        // Given
        var course = CourseTestDataFactory.generateTestCourseToSave();
        courseRepository.save(course);

        // When
        course.setName("JavaEE for Dummies - 2nd Edition");
        course.setCategory("Programming");
        course.setRating(1);
        course.setAuthor("Mark Doe");
        Course updatedCourse = courseRepository.save(course);

        //Then
        assertThat(updatedCourse).usingRecursiveComparison().isEqualTo(course);
    }

    @ParameterizedTest(name = "Delete course by ID: {0}")
    @ValueSource(longs = {1, 2, 3})
    @DisplayName("Given course in database, when deleteById with existing course ID, then course should be deleted")
    void givenCourseInDatabase_whenDeleteByIdWithExistingId_thenCourseShouldBeDeleted(Long courseId) {
        // When
        courseRepository.deleteById(courseId);

        // Then
        assertThat(courseRepository.findById(courseId)).isEmpty();
    }

    @ParameterizedTest(name = "Delete course by ID: {0}")
    @ValueSource(longs = {-1, 100, 9999})
    @DisplayName("Given course in database, when deleteById with non-existing course ID, then no course should be deleted")
    void givenCourseInDatabase_whenDeleteByIdWithNonExistingId_thenNoCourseShouldBeDeleted(Long courseId) {
        // Given a count of courses in the database before attempting to delete
        Long initialCount = courseRepository.count();

        // When
        courseRepository.deleteById(courseId);

        // Then
        assertThat(courseRepository.count()).isEqualTo(initialCount);
        assertThat(courseRepository.findById(courseId)).isEmpty();
    }

    @ParameterizedTest(name = "Search courses with name: {0}, category: {1}, and rating: {2}")
    @MethodSource("searchParameters")
    @DisplayName("Given courses in database, when searchSimilarCourses, then return matching courses")
    void givenCoursesInDatabase_whenSearchSimilarCourses_thenReturnMatchingCourses(
            String name, String category, int rating, List<Course> expectedCourses) {

        // When
        Iterable<Course> matchingCourses = courseRepository.searchSimilarCourses(name, category, rating);

        // Then
        assertThat(matchingCourses).isNotNull();
        assertThat(matchingCourses).hasSize(expectedCourses.size());
        Assertions.assertThat(matchingCourses).usingRecursiveComparison(
                RecursiveComparisonConfiguration.builder()
                        .withIgnoredFields("id")
                        .build()
        ).isEqualTo(expectedCourses);
    }

    static Stream<Arguments> searchParameters() {
        List<Course> testData = CourseTestDataFactory.DATA;
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