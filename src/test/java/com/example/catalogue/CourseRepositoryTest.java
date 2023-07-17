package com.example.catalogue;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.example.catalogue.model.*;
import com.example.catalogue.repository.CourseRepository;

@DataJpaTest
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Test
    void givenCourseInDatabase_whenFindById_thenReturnCourse() {
        // Given
        Course course = new Course("JavaEE for Dummies",
                "JavaEE",
                4,
                "John Doe"
        );
        courseRepository.save(course);

        // When
        Course foundCourse = courseRepository.findById(course.getId()).orElse(null);

        // Then
        assertThat(foundCourse).isNotNull();
        assertThat(foundCourse.getId()).isNotNull();
        assertThat(foundCourse.getName()).isEqualTo("JavaEE for Dummies");
        assertThat(foundCourse.getCategory()).isEqualTo("JavaEE");
        assertThat(foundCourse.getRating()).isEqualTo(4);
        assertThat(foundCourse.getAuthor()).isEqualTo("John Doe");
    }

    @Test
    public void givenCourse_whenSave_thenCourseShouldBePersisted() {
        // Given
        Course course = new Course("JavaEE for Dummies",
                "JavaEE",
                4,
                "John Doe"
        );

        // When
        Course savedCourse = courseRepository.save(course);

        // Then
        assertThat(Arrays.asList(courseRepository.findAll()).size()).isEqualTo(1);
        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getName()).isEqualTo("JavaEE for Dummies");
        assertThat(savedCourse.getCategory()).isEqualTo("JavaEE");
        assertThat(savedCourse.getRating()).isEqualTo(4);
        assertThat(savedCourse.getAuthor()).isEqualTo("John Doe");
    }

    @Test
    public void givenCourseInDatabase_whenUpdate_thenCourseShouldBeUpdated() {
        // Given
        var course = new Course("JavaEE for Dummies",
                "JavaEE",
                4,
                "John Doe"
        );
        courseRepository.save(course);

        // When
        course.setName("JavaEE for Dummies - 2nd Edition");
        Course updatedCourse = courseRepository.save(course);

        //Then
        assertThat(updatedCourse.getId()).isEqualTo(course.getId());
        assertThat(updatedCourse.getName()).isEqualTo("JavaEE for Dummies - 2nd Edition");
    }

    @Test
    public void givenCourseInDatabase_whenDelete_thenCourseShouldBeDeleted() {
        // Given
        var course = new Course("JavaEE for Dummies",
                "JavaEE",
                4,
                "John Doe"
        );
        course = courseRepository.save(course);

        // When
        courseRepository.delete(course);
        Course deletedCourse = courseRepository.findById(course.getId()).orElse(null);

        // Then
        assertThat(deletedCourse).isNull();
    }

    @ParameterizedTest
    @MethodSource("searchInputData")
    public void givenCoursesInDatabase_whenSearchByParameters_thenExpectedNumberOfCoursesAreReturned(String name, String category, int rating, int expectedSearchResultSize) {
        courseRepository.saveAll(getTestInputData());
        assertThat(courseRepository.searchSimilarCourses(name, category,  rating))
                .hasSize(expectedSearchResultSize);
    }

    static Stream<Arguments> searchInputData() {
        return Stream.of(
                Arguments.of("Spring Security", "",  0, 1),  // name, category, rating, expectedSearchResultSize
                Arguments.of("", "Spring",  0, 3),
                Arguments.of("", "Python", 5, 1),
                Arguments.of("", "", 4, 5)
        );
    }

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