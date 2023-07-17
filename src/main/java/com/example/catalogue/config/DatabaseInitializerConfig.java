package com.example.catalogue.config;

import com.example.catalogue.model.Course;
import com.example.catalogue.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
class DatabaseInitializerConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializerConfig.class);

    @Bean
    CommandLineRunner initDatabase(CourseRepository courseRepository) {
        return args -> {
            getData().forEach(model ->
                    log.info("Preloading data: " + courseRepository.save(model)));
        };
    }

    private List<Course> getData() {
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
                        .author("John Doe").build()
        );
    }


}