package com.example.catalogue.webservice;

import com.example.catalogue.model.Course;
import com.example.catalogue.service.CourseService;
import com.example.catalogue.webservice.autogen.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.StreamSupport;


@Endpoint
public class CourseEndpoint {//todo interface

    private CourseService courseService;

    @Autowired
    public CourseEndpoint(CourseService courseService) {
        this.courseService = courseService;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "getAllCoursesRequest")
    @ResponsePayload
    public GetAllCoursesResponse getAlCourses(@RequestPayload GetAllCoursesRequest request) {
        Iterable<Course> courses = courseService.getAllCourses();
        List<CourseXml> courseXmlList = StreamSupport.stream(courses.spliterator(), false)
                .map(this::convertCourseToCourseXml)
                .toList();

        GetAllCoursesResponse response = new GetAllCoursesResponse();
        response.getCourses().addAll(courseXmlList);
        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "getCourseByIdRequest")
    @ResponsePayload
    public GetCourseByIdResponse getCourseById(@RequestPayload GetCourseByIdRequest request) {
        Course course = courseService.getCourseById(request.getCourseId());

        GetCourseByIdResponse response = new GetCourseByIdResponse();
        response.setCourse(convertCourseToCourseXml(course));
        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "createCourseRequest")
    @ResponsePayload
    public CreateCourseResponse createCourse(@RequestPayload CreateCourseRequest request) {
        Course course = convertCourseXmlToCourse(request.getCourse());

        Course createdCourse = courseService.createCourse(course);

        CreateCourseResponse response = new CreateCourseResponse();
        response.setCourse(convertCourseToCourseXml(createdCourse));
        return response;
    }

    private CourseXml convertCourseToCourseXml(Course course) {
        CourseXml courseXml = new CourseXml();
        courseXml.setId(course.getId());
        courseXml.setName(course.getName());
        courseXml.setCategory(course.getCategory());
        courseXml.setRating(course.getRating());
        courseXml.setDescription(course.getDescription());
        courseXml.setAuthor(course.getAuthor());
        return courseXml;
    }

    private Course convertCourseXmlToCourse(CourseXml courseXml) {
        Course course = new Course();
        course.setId(courseXml.getId());
        course.setName(courseXml.getName());
        course.setCategory(courseXml.getCategory());
        course.setRating(courseXml.getRating());
        course.setDescription(courseXml.getDescription());
        course.setAuthor(courseXml.getAuthor());
        return course;
    }
}

