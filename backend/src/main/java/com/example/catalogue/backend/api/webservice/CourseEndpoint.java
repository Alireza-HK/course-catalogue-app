package com.example.catalogue.backend.api.webservice;

import com.example.catalogue.backend.api.webservice.autogen.*;
import com.example.catalogue.backend.entity.CourseEntity;
import com.example.catalogue.backend.service.CourseService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.List;
import java.util.stream.StreamSupport;


@Endpoint
public class CourseEndpoint {
    //ToDo: WS Security

    private CourseService courseService;

    @Autowired
    public CourseEndpoint(CourseService courseService) {
        this.courseService = courseService;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "getAllCoursesRequest")
    @ResponsePayload
    @Operation(summary = "Get all courses", description = "Get a list of all courses.")
    public GetAllCoursesResponse getAlCourses(@RequestPayload GetAllCoursesRequest request) {
        Iterable<CourseEntity> courses = courseService.getAllCourses();
        List<CourseXml> courseXmlList = StreamSupport.stream(courses.spliterator(), false)
                .map(this::convertCourseEntityToCourseXml)
                .toList();

        GetAllCoursesResponse response = new GetAllCoursesResponse();
        response.getCourses().addAll(courseXmlList);
        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "getCourseByIdRequest")
    @ResponsePayload
    @Operation(summary = "Get course by ID", description = "Get a course by its ID.")
    public GetCourseByIdResponse getCourseById(@RequestPayload GetCourseByIdRequest request) {
        CourseEntity course = courseService.getCourseById(request.getCourseId());

        GetCourseByIdResponse response = new GetCourseByIdResponse();
        response.setCourse(convertCourseEntityToCourseXml(course));
        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "createCourseRequest")
    @ResponsePayload
    @Operation(summary = "Create course", description = "Create a new course.")
    public CreateCourseResponse createCourse(@RequestPayload CreateCourseRequest request) {
        CourseEntity course = convertCourseXmlToCourseEntity(request.getCourse());

        CourseEntity createdCourse = courseService.createCourse(course);

        CreateCourseResponse response = new CreateCourseResponse();
        response.setCourse(convertCourseEntityToCourseXml(createdCourse));
        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "updateCourseRequest")
    @ResponsePayload
    @Operation(summary = "Update course", description = "Update an existing course.")
    public UpdateCourseResponse updateCourse(@RequestPayload UpdateCourseRequest request) {
        var course = convertCourseXmlToCourseEntity(request.getCourse());
        var updatedCourse = courseService.updateCourse(request.getCourseId(), course);

        UpdateCourseResponse response = new UpdateCourseResponse();
        response.setCourse(convertCourseEntityToCourseXml(updatedCourse));
        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "deleteCourseByIdRequest")
    @ResponsePayload
    @Operation(summary = "Delete course by ID", description = "Delete a course by its ID.")
    public DeleteCourseByIdResponse deleteCourseById(@RequestPayload DeleteCourseByIdRequest request) {
        courseService.deleteCourseById(request.getCourseId());

        DeleteCourseByIdResponse response = new DeleteCourseByIdResponse();
        response.setMessage("Course with ID " + request.getCourseId() + " has been deleted successfully.");
        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "deleteCoursesRequest")
    @ResponsePayload
    @Operation(summary = "Delete all courses", description = "Delete all courses from the course catalogue application.")
    public DeleteCoursesResponse deleteCourses(@RequestPayload DeleteCoursesRequest request) {
        courseService.deleteCourses();

        DeleteCoursesResponse response = new DeleteCoursesResponse();
        response.setMessage("All courses have been deleted successfully.");
        return response;
    }

    @PayloadRoot(namespace = WebServiceConfig.NAMESPACE_URI, localPart = "searchCoursesRequest")
    @ResponsePayload
    @Operation(summary = "Search similar courses", description = "Search similar courses based on provided parameters.")
    public SearchCoursesResponse searchCourses(@RequestPayload SearchCoursesRequest request) {
        Iterable<CourseEntity> result = courseService.searchSimilarCourses(request.getName(), request.getCategory(), request.getRating());
        List<CourseXml> courseXmlList = StreamSupport.stream(result.spliterator(), false)
                .map(this::convertCourseEntityToCourseXml)
                .toList();

        SearchCoursesResponse response = new SearchCoursesResponse();
        response.getCourses().addAll(courseXmlList);
        return response;
    }

    private CourseXml convertCourseEntityToCourseXml(CourseEntity course) {
        CourseXml courseXml = new CourseXml();
        BeanUtils.copyProperties(course, courseXml);
        return courseXml;
    }

    private CourseEntity convertCourseXmlToCourseEntity(CourseXml courseXml) {
        CourseEntity course = new CourseEntity();
        BeanUtils.copyProperties(courseXml, course);
        return course;
    }
}

