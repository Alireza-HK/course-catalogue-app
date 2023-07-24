package com.example.catalogue.backend.service;

import com.example.catalogue.backend.exception.CourseNotFoundException;
import com.example.catalogue.backend.entity.CourseEntity;
import com.example.catalogue.backend.repository.CourseRepository;
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
    public Iterable<CourseEntity> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public CourseEntity getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(String.format("No course with id %s is available", courseId)));
    }

    @Override
    public Iterable<CourseEntity> searchSimilarCourses(String name, String category, int rating) {
        return courseRepository.searchSimilarCourses(name, category, rating);
    }

    @Override
    public CourseEntity createCourse(CourseEntity course) {
        return courseRepository.save(course);
    }

    @Override
    public CourseEntity updateCourse(Long courseId, CourseEntity course) {
        CourseEntity existingCourse = courseRepository.findById(courseId)
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