package com.example.catalogue.service;

import com.example.catalogue.exception.CourseNotFoundException;
import com.example.catalogue.model.Course;
import com.example.catalogue.repository.CourseRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(String.format("No course with id %s is available", courseId)));
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
        BeanUtils.copyProperties(course, existingCourse, "id");
        return courseRepository.save(existingCourse);
    }

    @Override
    public void deleteCourseById(Long courseId) {
        courseRepository.findById(courseId).ifPresentOrElse(course -> courseRepository.deleteById(course.getId()),
                () -> {
                    throw new CourseNotFoundException(String.format("No course with id %s is available", courseId));
                });
    }

    @Override
    public void deleteCourses() {
        courseRepository.deleteAll();
    }

}