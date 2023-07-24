package com.example.catalogue.backend.testutil;

import com.example.catalogue.backend.entity.CourseEntity;

import java.util.List;

public class CourseTestDataFactory {

    public static final List<CourseEntity> DATA = List.of(
            CourseEntity.builder()
                    .id(1L)
                    .name("Machine Learning Fundamentals")
                    .category("Data Science")
                    .rating(4)
                    .description("Introduction to Machine Learning concepts.")
                    .author("Jane Smith")
                    .build(),

            CourseEntity.builder()
                    .id(2L)
                    .name("Web Development Bootcamp")
                    .category("Web Development")
                    .rating(5)
                    .description("Learn full-stack web development.")
                    .author("Mike Johnson")
                    .build(),

            CourseEntity.builder()
                    .id(3L)
                    .name("Artificial Intelligence Foundations")
                    .category("Artificial Intelligence")
                    .rating(4)
                    .description("Foundational concepts of Artificial Intelligence.")
                    .author("Alex Lee")
                    .build(),

            CourseEntity.builder()
                    .id(4L)
                    .name("Spanish for Beginners")
                    .category("Languages")
                    .rating(3)
                    .description("Beginner's course in learning Spanish.")
                    .author("Maria Rodriguez")
                    .build(),

            CourseEntity.builder()
                    .id(5L)
                    .name("React.js Crash Course")
                    .category("Web Development")
                    .rating(4)
                    .description("Quick overview of React.js fundamentals.")
                    .author("Chris Brown")
                    .build(),

            CourseEntity.builder()
                    .id(6L)
                    .name("Python for Data Analysis")
                    .category("Data Science")
                    .rating(4)
                    .description("Using Python for data analysis.")
                    .author("Emily Wang")
                    .build(),

            CourseEntity.builder()
                    .id(7L)
                    .name("Java Programming 101")
                    .category("Programming")
                    .rating(3)
                    .description("Introduction to Java programming.")
                    .author("John Doe")
                    .build(),

            CourseEntity.builder()
                    .id(8L)
                    .name("Java Advanced Topics")
                    .category("Programming")
                    .rating(4)
                    .description("Advanced Java programming concepts.")
                    .author("John Doe")
                    .build());


    public static CourseEntity generateTestCourseToSave() {
        return CourseEntity.builder()
                .name("JavaEE for Dummies")
                .category("JavaEE")
                .rating(4)
                .author("John Doe")
                .build();
    }

    public static CourseEntity generateTestSavedCourse() {
        return CourseEntity.builder()
                .id(1L)
                .name("JavaEE for Dummies")
                .category("JavaEE")
                .rating(4)
                .author("John Doe")
                .build();
    }
}
