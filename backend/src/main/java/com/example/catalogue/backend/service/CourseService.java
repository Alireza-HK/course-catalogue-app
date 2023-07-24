package com.example.catalogue.backend.service;

import com.example.catalogue.backend.entity.CourseEntity;

public interface CourseService {

    Iterable<CourseEntity> getAllCourses();

    CourseEntity getCourseById(Long courseId);

    Iterable<CourseEntity> searchSimilarCourses(String name, String category, int rating);

    CourseEntity createCourse(CourseEntity course);

	CourseEntity updateCourse(Long courseId, CourseEntity course);

    void deleteCourseById(Long courseId);

    void deleteCourses();


}
