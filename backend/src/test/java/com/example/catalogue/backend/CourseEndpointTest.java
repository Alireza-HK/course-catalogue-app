package com.example.catalogue.backend;

import com.example.catalogue.backend.api.webservice.autogen.*;
import com.example.catalogue.backend.exception.CourseNotFoundException;
import com.example.catalogue.backend.entity.CourseEntity;
import com.example.catalogue.backend.service.CourseService;
import com.example.catalogue.backend.testutil.CourseTestDataFactory;
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
        marshaller.setPackagesToScan("com.example.catalogue.backend.api.webservice.autogen");
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

        CourseEntity foundCourse = CourseTestDataFactory.generateTestSavedCourse();
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
        when(courseService.createCourse(any(CourseEntity.class))).thenReturn(CourseTestDataFactory.generateTestSavedCourse());

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

        verify(courseService, times(1)).createCourse(any(CourseEntity.class));
        verifyNoMoreInteractions(courseService);
    }

    @Test
    @DisplayName("Given an UpdateCourseRequest, when the request is sent, then verify the response contains the updated course details")
    public void givenUpdateCourseRequest_whenRequestSent_thenVerifyResponseContainsUpdatedCourseDetails() {
        // Given
        var updatedCourse = CourseEntity.builder()
                .id(1L)
                .name("JavaEE for Dummies - 2nd edition")
                .category("JavaEE")
                .rating(4)
                .author("John Doe")
                .build();
        when(courseService.updateCourse(anyLong(), any(CourseEntity.class))).thenReturn(updatedCourse);

        UpdateCourseRequest request = new UpdateCourseRequest();
        request.setCourseId(1L);
        request.setCourse(convertCourseToCourseXml(updatedCourse));

        UpdateCourseResponse expectedResponse = new UpdateCourseResponse();
        expectedResponse.setCourse(convertCourseToCourseXml(updatedCourse));

        // When
        StringSource requestPayload = marshallAndToStringSource(request);
        StringSource expectedResponsePayload = marshallAndToStringSource(expectedResponse);
        var responseAction = mockClient.sendRequest(withPayload(requestPayload));

        // Then
        responseAction
                .andExpect(noFault())
                .andExpect(payload(expectedResponsePayload));

        verify(courseService, times(1)).updateCourse(eq(1L), any(CourseEntity.class));
        verifyNoMoreInteractions(courseService);
    }

    @Test
    @DisplayName("Given a DeleteCourseByIdRequest, when the request is sent, then verify the response contains a success message")
    public void givenDeleteCourseByIdRequest_whenRequestSent_thenVerifyResponseContainsSuccessMessage() {
        // Given
        doNothing().when(courseService).deleteCourseById(anyLong());

        DeleteCourseByIdRequest request = new DeleteCourseByIdRequest();
        request.setCourseId(1L);

        DeleteCourseByIdResponse expectedResponse = new DeleteCourseByIdResponse();
        expectedResponse.setMessage("Course with ID 1 has been deleted successfully.");

        // When
        StringSource requestPayload = marshallAndToStringSource(request);
        StringSource expectedResponsePayload = marshallAndToStringSource(expectedResponse);
        var responseAction = mockClient.sendRequest(withPayload(requestPayload));

        // Then
        responseAction
                .andExpect(noFault())
                .andExpect(payload(expectedResponsePayload));

        verify(courseService, times(1)).deleteCourseById(eq(1L));
        verifyNoMoreInteractions(courseService);
    }

    @Test
    @DisplayName("Given a DeleteCoursesRequest, when the request is sent, then verify the response contains a success message")
    public void givenDeleteCoursesRequest_whenRequestSent_thenVerifyResponseContainsSuccessMessage() {
        // Given
        doNothing().when(courseService).deleteCourses();

        DeleteCoursesRequest request = new DeleteCoursesRequest();

        DeleteCoursesResponse expectedResponse = new DeleteCoursesResponse();
        expectedResponse.setMessage("All courses have been deleted successfully.");

        // When
        StringSource requestPayload = marshallAndToStringSource(request);
        StringSource expectedResponsePayload = marshallAndToStringSource(expectedResponse);
        var responseAction = mockClient.sendRequest(withPayload(requestPayload));

        // Then
        responseAction
                .andExpect(noFault())
                .andExpect(payload(expectedResponsePayload));

        verify(courseService, times(1)).deleteCourses();
        verifyNoMoreInteractions(courseService);
    }

    @Test
    @DisplayName("Given a SearchCoursesRequest, when the request is sent, then verify the response contains the matched courses")
    public void givenSearchCoursesRequest_whenRequestSent_thenVerifyResponseContainsMatchedCourses() {
        // Given
        var data = List.of(CourseEntity.builder()
                        .id(1L)
                        .name("Machine Learning Fundamentals")
                        .category("Data Science")
                        .rating(4)
                        .description("Introduction to Machine Learning concepts.")
                        .author("Jane Smith")
                        .build(),
                CourseEntity.builder()
                        .id(2L)
                        .name("Machine Learning in Action")
                        .category("Data Science")
                        .rating(4)
                        .description("Introduction to Machine Learning concepts.")
                        .author("Jane Smith")
                        .build()
        );
        when(courseService.searchSimilarCourses(anyString(), anyString(), anyInt())).thenReturn(data);

        SearchCoursesRequest request = new SearchCoursesRequest();
        request.setName("Machine Learning");
        request.setCategory("Data Science");
        request.setRating(4);

        List<CourseXml> courseXmlList = data
                .stream()
                .map(this::convertCourseToCourseXml)
                .toList();
        SearchCoursesResponse expectedResponse = new SearchCoursesResponse();
        expectedResponse.getCourses().addAll(courseXmlList);

        // When
        StringSource requestPayload = marshallAndToStringSource(request);
        StringSource expectedResponsePayload = marshallAndToStringSource(expectedResponse);
        var responseAction = mockClient.sendRequest(withPayload(requestPayload));

        // Then
        responseAction
                .andExpect(noFault())
                .andExpect(payload(expectedResponsePayload));

        verify(courseService, times(1)).searchSimilarCourses(eq("Machine Learning"), eq("Data Science"), eq(4));
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

    private CourseXml convertCourseToCourseXml(CourseEntity course) {
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
