package com.example.catalogue.frontend.testutil;


import com.example.catalogue.frontend.model.Course;

import java.util.List;

public class CourseTestDataFactory {

    public static final List<Course> DATA = List.of(
            Course.builder()
                    .id(1L)
                    .name("Machine Learning Fundamentals")
                    .category("Data Science")
                    .rating(4)
                    .description("Introduction to Machine Learning concepts.")
                    .author("Jane Smith")
                    .build(),

            Course.builder()
                    .id(2L)
                    .name("Web Development Bootcamp")
                    .category("Web Development")
                    .rating(5)
                    .description("Learn full-stack web development.")
                    .author("Mike Johnson")
                    .build(),

            Course.builder()
                    .id(3L)
                    .name("Artificial Intelligence Foundations")
                    .category("Artificial Intelligence")
                    .rating(4)
                    .description("Foundational concepts of Artificial Intelligence.")
                    .author("Alex Lee")
                    .build(),

            Course.builder()
                    .id(4L)
                    .name("Spanish for Beginners")
                    .category("Languages")
                    .rating(3)
                    .description("Beginner's course in learning Spanish.")
                    .author("Maria Rodriguez")
                    .build(),

            Course.builder()
                    .id(5L)
                    .name("React.js Crash Course")
                    .category("Web Development")
                    .rating(4)
                    .description("Quick overview of React.js fundamentals.")
                    .author("Chris Brown")
                    .build(),

            Course.builder()
                    .id(6L)
                    .name("Python for Data Analysis")
                    .category("Data Science")
                    .rating(4)
                    .description("Using Python for data analysis.")
                    .author("Emily Wang")
                    .build(),

            Course.builder()
                    .id(7L)
                    .name("Java Programming 101")
                    .category("Programming")
                    .rating(3)
                    .description("Introduction to Java programming.")
                    .author("John Doe")
                    .build(),

            Course.builder()
                    .id(8L)
                    .name("Java Advanced Topics")
                    .category("Programming")
                    .rating(4)
                    .description("Advanced Java programming concepts.")
                    .author("John Doe")
                    .build());


    public static Course generateTestCourseToSave() {
        return Course.builder()
                .name("JavaEE for Dummies")
                .category("JavaEE")
                .rating(4)
                .author("John Doe")
                .build();
    }

    public static Course generateTestSavedCourse() {
        return Course.builder()
                .id(1L)
                .name("JavaEE for Dummies")
                .category("JavaEE")
                .rating(4)
                .author("John Doe")
                .build();
    }
}
