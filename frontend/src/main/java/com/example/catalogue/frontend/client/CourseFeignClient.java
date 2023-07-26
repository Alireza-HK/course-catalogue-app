package com.example.catalogue.frontend.client;

import com.example.catalogue.common.model.Course;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "courseFeignClient", url = "${backend.address}/courses", configuration = FeignClientConfiguration.class)
public interface CourseFeignClient {

    @GetMapping("/")
    Iterable<Course> getAllCourses();

    @GetMapping("/{id}")
    Course getCourseById(@PathVariable("id") Long courseId);

    @PostMapping("/")
    Course createCourse(@RequestBody Course course);

    @PutMapping("/{id}")
    Course updateCourse(@PathVariable("id") Long courseId, @RequestBody Course course);

    @DeleteMapping("/{id}")
    void deleteCourseById(@PathVariable("id") Long courseId);

    @DeleteMapping("/")
    void deleteCourses();

    @GetMapping("/search")
    Iterable<Course> searchSimilarCourses(@RequestParam String name, @RequestParam String category, @RequestParam int rating);
}
