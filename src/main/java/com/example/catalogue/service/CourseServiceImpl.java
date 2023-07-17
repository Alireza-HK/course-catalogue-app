package com.example.catalogue.service;

import com.example.catalogue.exception.CourseNotFoundException;
import com.example.catalogue.model.Course;
import com.example.catalogue.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseServiceImpl implements CourseService {

    private CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Iterable<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public Optional<Course> getCourseById(Long courseId) {
        return Optional.ofNullable(courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(String.format("No course with id %s is available", courseId))));
    }

    @Override
    public Iterable<Course> searchSimilarCourses(String name, String category, int rating) {
        return courseRepository.searchSimilarCourses(name, category, rating);
    }

    @Override
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Long courseId, Course course) {
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(String.format("No course with id %s is available", courseId)));
        existingCourse.setName(course.getName());
        existingCourse.setCategory(course.getCategory());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setRating(course.getRating());
        existingCourse.setAuthor(course.getAuthor());
        return courseRepository.save(existingCourse);
    }

    @Override
    public void deleteCourseById(Long courseId) {
        courseRepository.findById(courseId).orElseThrow(() -> new CourseNotFoundException(String.format("No course with id %s is available", courseId)));
        courseRepository.deleteById(courseId);
    }

    @Override
    public void deleteCourses() {
        courseRepository.deleteAll();
    }

}