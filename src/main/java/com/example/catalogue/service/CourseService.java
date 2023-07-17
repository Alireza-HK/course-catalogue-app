package com.example.catalogue.service;

import com.example.catalogue.model.Course;

public interface CourseService {

    Iterable<Course> getAllCourses();

    Course getCourseById(Long courseId);

    Iterable<Course> searchSimilarCourses(String name, String category, int rating);

    Course createCourse(Course course);

	Course updateCourse(Long courseId, Course course);

    void deleteCourseById(Long courseId);

    void deleteCourses();


}
