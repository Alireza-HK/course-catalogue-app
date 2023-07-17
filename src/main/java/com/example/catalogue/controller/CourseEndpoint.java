package com.example.catalogue.controller;

import com.example.catalogue.model.Course;
import com.example.catalogue.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/courses/")
@Tag(name = "Course Catalogue Controller", description = "This REST controller provides services to manage courses in the course catalogue application")
public class CourseEndpoint {

    private final CourseService courseService;

    @Autowired
    public CourseEndpoint(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Provides all courses available in the course catalogue application")
    public Iterable<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("{id}")
    @ResponseStatus(code = HttpStatus.OK)
    @Operation(summary = "Provides course details for the supplied course id from the course catalogue application")
    public Course getCourseById(@PathVariable("id") long courseId) {
        return courseService.getCourseById(courseId);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Creates a new course in the course catalogue application")
    public Course createCourse(@Valid @RequestBody Course course) {
        return courseService.createCourse(course);
    }

    @PutMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Updates the course details in the course catalogue application for the supplied course id")
    public Course updateCourse(@PathVariable("id") long courseId, @Valid @RequestBody Course course) {
        return courseService.updateCourse(courseId, course);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes the course details for the supplied course id from the course catalogue application")
    public void deleteCourseById(@PathVariable("id") long courseId) {
        courseService.deleteCourseById(courseId);
    }

    @DeleteMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes all courses from the course catalogue application")
    public void deleteCourses() {
        courseService.deleteCourses();
    }

}
