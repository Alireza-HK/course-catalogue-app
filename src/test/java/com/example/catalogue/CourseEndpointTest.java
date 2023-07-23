package com.example.catalogue;

import com.example.catalogue.api.webservice.autogen.*;
import com.example.catalogue.exception.CourseNotFoundException;
import com.example.catalogue.model.Course;
import com.example.catalogue.service.CourseService;
import com.example.catalogue.testutil.CourseTestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webservices.server.WebServiceServerTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.oxm.XmlMappingException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;

import javax.xml.transform.Result;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.*;

@WebServiceServerTest
public class CourseEndpointTest {
    @Autowired
    private MockWebServiceClient mockClient;

    @MockBean
    private CourseService courseService;

    private Jaxb2Marshaller marshaller;


    @BeforeEach
    public void setUp() {
        marshaller = new Jaxb2Marshaller();
        marshaller.setPackagesToScan("com.example.catalogue.api.webservice.autogen");
    }

    @Test
    @DisplayName("Given a GetAllCoursesRequest, when the request is sent, then verify the response contains all courses")
    public void givenGetAllCoursesRequest_whenRequestSent_thenVerifyResponseContainsAllCourses() {
        // Given
        GetAllCoursesRequest request = new GetAllCoursesRequest();

        List<CourseXml> courseXmlList = CourseTestDataFactory.DATA
                .stream()
                .map(this::convertCourseToCourseXml)
                .toList();
        GetAllCoursesResponse expectedResponse = new GetAllCoursesResponse();
        expectedResponse.getCourses().addAll(courseXmlList);

        when(courseService.getAllCourses()).thenReturn(CourseTestDataFactory.DATA);

        // When
        StringSource requestPayload = marshallAndToStringSource(request);
        StringSource expectedResponsePayload = marshallAndToStringSource(expectedResponse);
        var responseAction = mockClient.sendRequest(withPayload(requestPayload));

        // Then
        responseAction
                .andExpect(noFault())
                .andExpect(payload(expectedResponsePayload));

        verify(courseService, times(1)).getAllCourses();
        verifyNoMoreInteractions(courseService);
    }

    @Test
    @DisplayName("Given a GetCourseByIdRequest for an existing course, when the request is sent, then verify the response contains the course details")
    public void givenGetCourseByIdRequestForExistingCourse_whenRequestSent_thenVerifyResponseContainsCourseDetails() {
        // Given
        GetCourseByIdRequest request = new GetCourseByIdRequest();
        request.setCourseId(1L);

        Course foundCourse = CourseTestDataFactory.generateTestSavedCourse();
        when(courseService.getCourseById(1L)).thenReturn(foundCourse);

        GetCourseByIdResponse expectedResponse = new GetCourseByIdResponse();
        expectedResponse.setCourse(convertCourseToCourseXml(foundCourse));

        // When
        StringSource requestPayload = marshallAndToStringSource(request);
        StringSource expectedResponsePayload = marshallAndToStringSource(expectedResponse);
        var responseAction = mockClient.sendRequest(withPayload(requestPayload));

        // Then
        responseAction
                .andExpect(noFault())
                .andExpect(payload(expectedResponsePayload));

        verify(courseService, times(1)).getCourseById(1L);
        verifyNoMoreInteractions(courseService);
    }

    @Test
    @DisplayName("Given a GetCourseByIdRequest for a non-existing course, when the request is sent, then verify the response contains a SOAP fault")
    public void givenGetCourseByIdRequestForNonExistingCourse_whenRequestSent_thenVerifyResponseContainsSoapFault() {
        // Given
        GetCourseByIdRequest request = new GetCourseByIdRequest();
        request.setCourseId(20L);
        when(courseService.getCourseById(20L)).thenThrow(new CourseNotFoundException("No course with id 20 is available"));

        // When
        StringSource requestPayload = marshallAndToStringSource(request);
        var responseAction = mockClient.sendRequest(withPayload(requestPayload));

        // Then
        responseAction.andExpect(serverOrReceiverFault("No course with id 20 is available"));

        verify(courseService, times(1)).getCourseById(20L);
        verifyNoMoreInteractions(courseService);
    }

    @Test
    @DisplayName("Given a CreateCourseRequest, when the request is sent, then verify the response contains the newly created course details")
    public void givenCreateCourseRequest_whenRequestSent_thenVerifyResponseContainsNewlyCreatedCourseDetails() {
        // Given
        when(courseService.createCourse(any(Course.class))).thenReturn(CourseTestDataFactory.generateTestSavedCourse());

        CreateCourseRequest request = new CreateCourseRequest();
        request.setCourse(convertCourseToCourseXml(CourseTestDataFactory.generateTestCourseToSave()));

        CreateCourseResponse expectedResponse = new CreateCourseResponse();
        expectedResponse.setCourse(convertCourseToCourseXml(CourseTestDataFactory.generateTestSavedCourse()));

        // When
        StringSource requestPayload = marshallAndToStringSource(request);
        StringSource expectedResponsePayload = marshallAndToStringSource(expectedResponse);
        var responseAction = mockClient.sendRequest(withPayload(requestPayload));

        // Then
        responseAction
                .andExpect(noFault())
                .andExpect(payload(expectedResponsePayload));

        verify(courseService, times(1)).createCourse(any(Course.class));
        verifyNoMoreInteractions(courseService);
    }

    public StringSource marshallAndToStringSource(Object object) {
        try {
            Result writer = new StringResult();
            marshaller.marshal(object, writer);
            return new StringSource(writer.toString());
        } catch (XmlMappingException e) {
            throw new RuntimeException("Error marshalling object to payload source.", e);
        }
    }

    private CourseXml convertCourseToCourseXml(Course course) {
        CourseXml courseXml = new CourseXml();
        if (course.getId() != null) {
            courseXml.setId(course.getId());
        }
        courseXml.setName(course.getName());
        courseXml.setCategory(course.getCategory());
        courseXml.setRating(course.getRating());
        courseXml.setDescription(course.getDescription());
        courseXml.setAuthor(course.getAuthor());
        return courseXml;
    }
}
