package com.example.catalogue.frontend.controller;

import com.example.catalogue.frontend.client.CourseFeignClient;
import com.example.catalogue.frontend.model.Course;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class CourseController {

    private final CourseFeignClient courseFeignClient;

    @Autowired
    public CourseController(CourseFeignClient courseFeignClient) {
        this.courseFeignClient = courseFeignClient;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/index";
    }

    @GetMapping("/index")
    public String index(Model model) {
        model.addAttribute("courses", courseFeignClient.getAllCourses());
        model.addAttribute("searchModel", new Course());
        return "index";
    }

    @GetMapping("/addcourse")
    public String showAddCourseForm(Course course) {
        return "add-course";
    }

    @PostMapping("/addcourse")
    public String addCourse(@Valid Course course, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "add-course";
        }
        courseFeignClient.createCourse(course);
        return "redirect:/index";
    }

    @GetMapping("/update/{id}")
    public String showUpdateCourseForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("course", courseFeignClient.getCourseById(id));
        return "update-course";
    }

    @PostMapping("/update/{id}")
    public String updateCourse(@PathVariable("id") Long id, @Valid Course course, BindingResult result, Model model) {
        if (result.hasErrors()) {
            course.setId(id);
            return "update-course";
        }
        courseFeignClient.updateCourse(id, course);
        return "redirect:/index";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable("id") Long id) {
        courseFeignClient.deleteCourseById(id);
        return "redirect:/index";
    }

    @PostMapping("/search")
    public String search(Model model, Course course) {
        var result = courseFeignClient.searchSimilarCourses(course.getName(), course.getCategory(), course.getRating());
        model.addAttribute("courses", result);
        return "index";
    }

    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("searchModel", new Course());
        return "search-course";
    }

}