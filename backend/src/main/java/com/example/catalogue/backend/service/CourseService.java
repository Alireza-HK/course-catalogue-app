package com.example.catalogue.backend.service;

import com.example.catalogue.backend.model.Course;

public interface CourseService {

    Iterable<Course> getAllCourses();

    Course getCourseById(Long courseId);

    Iterable<Course> searchSimilarCourses(String name, String category, int rating);

    Course createCourse(Course course);

	Course updateCourse(Long courseId, Course course);

    void deleteCourseById(Long courseId);

    void deleteCourses();


}
