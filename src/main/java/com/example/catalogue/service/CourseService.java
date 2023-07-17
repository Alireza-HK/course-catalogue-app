package com.example.catalogue.service;

import com.example.catalogue.model.Course;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseService {

    Iterable<Course> getAllCourses();

    Optional<Course> getCourseById(Long courseId);

    Iterable<Course> searchSimilarCourses(String name, String category, int rating);

    Course createCourse(Course course);

	Course updateCourse(Long courseId, Course course);

    void deleteCourseById(Long courseId);

    void deleteCourses();


}
