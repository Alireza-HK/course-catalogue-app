package com.example.catalogue.controller;

import com.example.catalogue.model.Course;
import com.example.catalogue.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@Controller
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("searchModel", new Course());
        return "index";
    }

    @GetMapping("/addcourse")
    public String showAddCourseForm(Course course) {
        return "add-course";
    }

    @PostMapping("/addcourse")
    public String addCourse(@Valid Course course, BindingResult result, Model model){
        if (result.hasErrors()) {
            return "add-course";
        }
        courseService.createCourse(course);
        return "redirect:/index";
    }

    @GetMapping("/update/{id}")
    public String showUpdateCourseForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        return "update-course";
    }

    @PutMapping("/update/{id}")
    public String updateCourse(@PathVariable("id") Long id, @Valid Course course, BindingResult result, Model model) {
        if (result.hasErrors()) {
            course.setId(id);
            return "update-course";
        }
        courseService.updateCourse(id, course);
        return "redirect:/index";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseService.deleteCourseById(id);
        return "redirect:/index";
    }

    @PostMapping("/search")
    public String search(Model model, Course course) {
        var result = courseService.searchSimilarCourses(course.getName(), course.getCategory(), course.getRating());
        model.addAttribute("courses", result);
        return "index";
    }

    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("searchModel", new Course());
        return "search";
    }

}