package com.example.catalogue.backend.config;

import com.example.catalogue.backend.entity.CourseEntity;
import com.example.catalogue.backend.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
class DatabaseInitializerConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializerConfig.class);

    private static final List<CourseEntity> TEST_DATA = List.of(
            CourseEntity.builder()
                    .name("Machine Learning Fundamentals")
                    .category("Data Science")
                    .rating(4)
                    .description("Introduction to Machine Learning concepts.")
                    .author("Jane Smith")
                    .build(),

            CourseEntity.builder()
                    .name("Web Development Bootcamp")
                    .category("Web Development")
                    .rating(5)
                    .description("Learn full-stack web development.")
                    .author("Mike Johnson")
                    .build(),

            CourseEntity.builder()
                    .name("Artificial Intelligence Foundations")
                    .category("Artificial Intelligence")
                    .rating(4)
                    .description("Foundational concepts of Artificial Intelligence.")
                    .author("Alex Lee")
                    .build(),

            CourseEntity.builder()
                    .name("Spanish for Beginners")
                    .category("Languages")
                    .rating(3)
                    .description("Beginner's course in learning Spanish.")
                    .author("Maria Rodriguez")
                    .build(),

            CourseEntity.builder()
                    .name("React.js Crash Course")
                    .category("Web Development")
                    .rating(4)
                    .description("Quick overview of React.js fundamentals.")
                    .author("Chris Brown")
                    .build(),

            CourseEntity.builder()
                    .name("Python for Data Analysis")
                    .category("Data Science")
                    .rating(4)
                    .description("Using Python for data analysis.")
                    .author("Emily Wang")
                    .build(),

            CourseEntity.builder()
                    .name("Java Programming 101")
                    .category("Programming")
                    .rating(3)
                    .description("Introduction to Java programming.")
                    .author("John Doe")
                    .build(),

            CourseEntity.builder()
                    .name("Java Advanced Topics")
                    .category("Programming")
                    .rating(4)
                    .description("Advanced Java programming concepts.")
                    .author("John Doe")
                    .build()
    );

    @Bean
    CommandLineRunner initDatabase(CourseRepository courseRepository) {
        return args -> {
            TEST_DATA.forEach(course -> {
                log.info("Preloading data: {}", courseRepository.save(course));
            });
        };
    }
}
