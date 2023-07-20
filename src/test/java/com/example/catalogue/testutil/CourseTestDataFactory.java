package com.example.catalogue.testutil;

import com.example.catalogue.model.Course;

import java.util.List;

public class CourseTestDataFactory {

    public static final List<Course> DATA = List.of(
            Course.builder()
                    .name("Machine Learning Fundamentals")
                    .category("Data Science")
                    .rating(4)
                    .description("Introduction to Machine Learning concepts.")
                    .author("Jane Smith")
                    .build(),

            Course.builder()
                    .name("Web Development Bootcamp")
                    .category("Web Development")
                    .rating(5)
                    .description("Learn full-stack web development.")
                    .author("Mike Johnson")
                    .build(),

            Course.builder()
                    .name("Artificial Intelligence Foundations")
                    .category("Artificial Intelligence")
                    .rating(4)
                    .description("Foundational concepts of Artificial Intelligence.")
                    .author("Alex Lee")
                    .build(),

            Course.builder()
                    .name("Spanish for Beginners")
                    .category("Languages")
                    .rating(3)
                    .description("Beginner's course in learning Spanish.")
                    .author("Maria Rodriguez")
                    .build(),

            Course.builder()
                    .name("React.js Crash Course")
                    .category("Web Development")
                    .rating(4)
                    .description("Quick overview of React.js fundamentals.")
                    .author("Chris Brown")
                    .build(),

            Course.builder()
                    .name("Python for Data Analysis")
                    .category("Data Science")
                    .rating(4)
                    .description("Using Python for data analysis.")
                    .author("Emily Wang")
                    .build(),

            Course.builder()
                    .name("Java Programming 101")
                    .category("Programming")
                    .rating(3)
                    .description("Introduction to Java programming.")
                    .author("John Doe")
                    .build(),

            Course.builder()
                    .name("Java Advanced Topics")
                    .category("Programming")
                    .rating(4)
                    .description("Advanced Java programming concepts.")
                    .author("John Doe")
                    .build());
}
