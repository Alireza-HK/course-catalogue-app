package com.example.catalogue;

import com.example.catalogue.model.Course;
import com.example.catalogue.repository.CourseRepository;
import com.example.catalogue.testutil.CourseTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

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
        var course = Course.builder().name("JavaEE for Dummies").category("JavaEE").rating(4).author("John Doe").build();
        courseRepository.save(course);

        // When
        Optional<Course> foundCourseOptional = courseRepository.findById(course.getId());

        // Then
        assertThat(foundCourseOptional).isPresent();
        Course foundCourse = foundCourseOptional.get();
        assertThat(foundCourse.getId()).isNotNull();
        assertThat(foundCourse.getName()).isEqualTo("JavaEE for Dummies");
        assertThat(foundCourse.getCategory()).isEqualTo("JavaEE");
        assertThat(foundCourse.getRating()).isEqualTo(4);
        assertThat(foundCourse.getAuthor()).isEqualTo("John Doe");
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
        var course = Course.builder().name("JavaEE for Dummies").category("JavaEE").rating(4).author("John Doe").build();
        Long recordCount = courseRepository.count();

        // When
        Course savedCourse = courseRepository.save(course);

        // Then
        assertThat(courseRepository.findAll()).hasSize(recordCount.intValue() + 1);
        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getName()).isEqualTo("JavaEE for Dummies");
        assertThat(savedCourse.getCategory()).isEqualTo("JavaEE");
        assertThat(savedCourse.getRating()).isEqualTo(4);
        assertThat(savedCourse.getAuthor()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Given course in database, when update, then course should be updated")
    void givenCourseInDatabase_whenUpdate_thenCourseShouldBeUpdated() {
        // Given
        var course = Course.builder().name("JavaEE for Dummies").category("JavaEE").rating(4).author("John Doe").build();
        courseRepository.save(course);

        // When
        course.setName("JavaEE for Dummies - 2nd Edition");
        Course updatedCourse = courseRepository.save(course);

        //Then
        assertThat(updatedCourse.getId()).isEqualTo(course.getId());
        assertThat(updatedCourse.getName()).isEqualTo("JavaEE for Dummies - 2nd Edition");
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
        // When
        courseRepository.deleteById(courseId);

        // Then
        assertThat(courseRepository.findById(courseId)).isEmpty();
    }

    @ParameterizedTest(name = "Search courses with name: {0}, category: {1}, and rating: {2}")
    @MethodSource("searchParameters")
    @DisplayName("Given courses in database, when searchSimilarCourses, then return matching courses")
    void givenCoursesInDatabase_whenSearchSimilarCourses_thenReturnMatchingCourses(
            String name, String category, int rating, List<String> expectedCourseTitles) {

        // When
        Iterable<Course> matchingCourses = courseRepository.searchSimilarCourses(name, category, rating);

        // Extract course titles as a list of strings
        List<String> matchingCourseTitles = ((Collection<Course>) matchingCourses).stream()
                .map(Course::getName)
                .collect(Collectors.toList());

        // Then
        assertThat(matchingCourseTitles).isNotNull();
        assertThat(matchingCourseTitles).hasSize(expectedCourseTitles.size());
        assertThat(matchingCourseTitles).containsExactlyInAnyOrderElementsOf(expectedCourseTitles);
    }

    static Stream<Arguments> searchParameters() {
        return Stream.of(
                Arguments.of("Web", "", 0, List.of("Web Development Bootcamp")),
                Arguments.of("", "Programming", 0, List.of("Java Programming 101", "Java Advanced Topics")),
                Arguments.of("", "Languages", 2, List.of("Spanish for Beginners")),
                Arguments.of("", "", 5, List.of("Web Development Bootcamp")),
                Arguments.of("", "", 0,
                        CourseTestDataFactory.DATA
                                .stream()
                                .map(Course::getName)
                                .collect(Collectors.toList())),
                Arguments.of("NonExistentCourse", "NonExistentCategory", 0, Collections.emptyList())
        );
    }
}