package com.example.catalogue;

import com.example.catalogue.exception.CourseNotFoundException;
import com.example.catalogue.model.Course;
import com.example.catalogue.service.CourseService;
import com.example.catalogue.testutil.CourseTestDataFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = {"USER"})
@Transactional
public class CourseRestControllerTest {

    private MockMvc mockMvc;

    private CourseService courseService;

    private ObjectMapper objectMapper;

    @Autowired
    public CourseRestControllerTest(CourseService courseService, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.courseService = courseService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    @DisplayName("Get All Courses - Return List of Courses")
    void whenGetAllCourses_thenReturnListOfCourses() throws Exception {
        // Perform GET request to /courses
        ResultActions result = mockMvc.perform(get("/courses/")
                .accept(MediaType.APPLICATION_JSON));

        // Then: Verify the response
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(courseService.getAllCourses())));
    }

    @Test
    @DisplayName("Get Course By ID - Return Course")
    void givenCourseId_whenGetCourse_thenReturnCourse() throws Exception {
        // Given
        Course course = CourseTestDataFactory.generateTestCourseToSave();

        // When: Perform POST request to create the course
        ResultActions postResult = mockMvc.perform(post("/courses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isCreated());

        // Then: Extract the ID from the response
        Long id = JsonPath.parse(postResult.andReturn().getResponse().getContentAsString()).read("$.id", Long.class);

        // When: Perform GET request to retrieve the course by ID
        ResultActions getResult = mockMvc.perform(get("/courses/{id}", id));

        // Then: Verify the response
        getResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));

        // Verify the course details in the response
        assertCourseDetails(getResult, id, course);
    }

    @Test
    @DisplayName("Get Course By Non-Existent ID - Return Not Found Status")
    public void givenNonExistentCourseId_whenGetCourse_thenReturnNotFoundStatus() throws Exception {
        mockMvc.perform(get("/courses/{id}", 100))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Create Course - Return Created Course and Verify in Database (Given Valid Course Data)")
    void givenValidCourseData_whenCreateCourse_thenReturnCreatedCourseAndVerifyInDatabase() throws Exception {
        // Given
        Course courseToSave = CourseTestDataFactory.generateTestCourseToSave();

        // When
        ResultActions result = mockMvc.perform(post("/courses/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(courseToSave)));

        // Then
        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        // Extract the generated ID from the response
        Long id = JsonPath.parse(result.andReturn().getResponse().getContentAsString()).read("$.id", Long.class);
        assertNotNull(id);

        // Verify the course details in the response
        assertCourseDetails(result, id, courseToSave);

        // And: Verify the course in the database using the service
        Course retrievedCourse = courseService.getCourseById(id);
        courseToSave.setId(id); // Set the generated ID for comparison
        Assertions.assertThat(retrievedCourse).usingRecursiveComparison().isEqualTo(courseToSave);
    }

    private void assertCourseDetails(ResultActions result, Long id, Course course) throws Exception {
        result.andExpect(jsonPath("$.name").value(course.getName()))
                .andExpect(jsonPath("$.category").value(course.getCategory()))
                .andExpect(jsonPath("$.rating").value(course.getRating()))
                .andExpect(jsonPath("$.author").value(course.getAuthor()));
    }


    @Test
    @DisplayName("Update Course - Return Updated Course and Verify in Database")
    void givenValidCourseIdAndUpdatedCourse_whenUpdateCourse_thenReturnUpdatedCourseAndVerifyInDatabase() throws Exception {
        // Given
        Course course = CourseTestDataFactory.generateTestCourseToSave();

        // Create a new course using the POST request and expect a 201 Created status
        ResultActions postResult = mockMvc.perform(post("/courses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isCreated());

        Long id = JsonPath.parse(postResult.andReturn().getResponse().getContentAsString()).read("$.id", Long.class);

        // Update the course details
        Course updatedCourse = Course.builder().id(id).name("JavaEE for Dummies - 2nd Edition").category("Programming").rating(5).author("Mark Doe").build();

        // When: Perform a PUT request to update the course with the given ID
        ResultActions putResult = mockMvc.perform(put("/courses/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCourse)));

        // Then: Verify the response
        putResult
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(status().isNoContent());
        assertCourseDetails(putResult, id, updatedCourse);

        // And: Verify the updated course in the database using the service
        Course retrievedCourse = courseService.getCourseById(id);
        Assertions.assertThat(retrievedCourse).usingRecursiveComparison().isEqualTo(updatedCourse);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void givenCourseId_whenDeleteCourse_thenCourseShouldBeDeleted() throws Exception {
        // Given: Create a new course using the POST request and expect a 201 Created status
        Course course = CourseTestDataFactory.generateTestCourseToSave();
        ResultActions postResult = mockMvc.perform(post("/courses/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isCreated());

        Long id = JsonPath.parse(postResult.andReturn().getResponse().getContentAsString()).read("$.id", Long.class);

        // When: Perform a DELETE request to delete the course with the given ID
        mockMvc.perform(delete("/courses/{id}", id))
                .andExpect(status().isNoContent());

        // Then: Verify that the course has been deleted from the database using the service layer
        assertThrows(CourseNotFoundException.class, () -> courseService.getCourseById(id));
    }

    @Test
    void givenUserRole_whenDeleteCourse_thenAccessDenied() throws Exception {
        // When: Perform a GET request to retrieve the course with the given ID
        ResultActions result = mockMvc.perform(delete("/courses/1")
                        .accept(MediaType.APPLICATION_JSON)) // Set the Accept header to application/json
                .andExpect(status().isForbidden());

        // Then: Verify the response
        result.andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }


    @ParameterizedTest
    @MethodSource("searchParameters")
    @DisplayName("Search Courses - Return List of Matching Courses")
    void whenSearchCourses_thenReturnListOfMatchingCourses(String name, String category, int rating, List<Course> expectedCourses) throws Exception {
        // Perform GET request to /search with the search parameters
        ResultActions result = mockMvc.perform(get("/courses/search")
                .param("name", name)
                .param("category", category)
                .param("rating", String.valueOf(rating))
                .accept(MediaType.APPLICATION_JSON));

        // Then: Verify the response
        result.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(mvcResult -> {
                    String jsonResponse = mvcResult.getResponse().getContentAsString();
                    List<Course> actualCourses = objectMapper.readValue(jsonResponse, new TypeReference<List<Course>>() {
                    });
                    Assertions.assertThat(actualCourses).usingRecursiveComparison(
                            RecursiveComparisonConfiguration.builder()
                                    .withIgnoredFields("id")
                                    .build()
                    ).isEqualTo(expectedCourses);
                });
    }

    static Stream<Arguments> searchParameters() {
        List<Course> testData = CourseTestDataFactory.DATA;
        return Stream.of(
                Arguments.of("Web", "", 0,
                        testData.stream()
                                .filter(c -> c.getName().contains("Web Development Bootcamp"))
                                .collect(Collectors.toList())
                ),
                Arguments.of("", "Programming", 0,
                        testData.stream()
                                .filter(c -> c.getName().contains("Java Programming 101") || c.getName().equals("Java Advanced Topics"))
                                .collect(Collectors.toList())
                ),
                Arguments.of("", "Languages", 2,
                        testData.stream()
                                .filter(c -> c.getName().contains("Spanish for Beginners"))
                                .collect(Collectors.toList())
                ),
                Arguments.of("", "", 5,
                        testData.stream()
                                .filter(c -> c.getRating() >= 5)
                                .collect(Collectors.toList())
                ),
                Arguments.of("", "", 0,
                        testData
                ),
                Arguments.of("NonExistentCourse", "NonExistentCourse", 0,
                        Collections.emptyList())
        );
    }
}
