package com.example.catalogue.config;

import com.example.catalogue.model.Course;
import com.example.catalogue.repository.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        var javaCourse = new Course(
                "Spring Boot in Action",
                "Java",
                4,
                "Spring Boot gives all the power of the Spring Framework without all of the complexity",
                "John Doe"
        );

        var groovyCourse = new Course(
                "Getting Started with Groovy",
                "Groovy",
                5,
                "Learn about Groovy",
                "John Doe"
        );

        var kubernetesCourse = new Course(
                "Getting Started with Kubernetes",
                "Kubernetes",
                3,
                "Master application deployment with Kubernetes",
                "Mikel Muller"
        );

        return List.of(javaCourse, groovyCourse, kubernetesCourse);
    }


}