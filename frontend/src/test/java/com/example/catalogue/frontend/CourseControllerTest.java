package com.example.catalogue.frontend;

import com.example.catalogue.frontend.client.CourseFeignClient;
import com.example.catalogue.common.model.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = {"USER"})
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseFeignClient restClient;

    @BeforeEach
    void setUp() {
        when(restClient.getAllCourses()).thenReturn(getTestData());
    }

    @Test
    @DisplayName("GET /index - Returns Index Template with Existing Courses")
    void getIndexPage_ReturnsIndexTemplateWithExistingCourses() throws Exception {
        // When: Perform GET request to /index
        var resultActions = mockMvc.perform(get("/index"));

        // Then: Ensure the response is successful and verify the model attributes
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("searchModel"))
                .andExpect(model().attribute("courses", hasSize(getTestData().size())))
                .andExpect(model().attribute("courses", containsInAnyOrder(getTestData().toArray())))
                .andExpect(model().attributeHasNoErrors());

        // And: Verify that the getAllCourses method of the restClient is called exactly once
        verify(restClient, times(1)).getAllCourses();

        // And: Verify that no other interactions were made with the restClient
        verifyNoMoreInteractions(restClient);
    }

    @Test
    @DisplayName("GET /addcourse - Returns Add Course Template")
    void getAddCourseForm_ReturnsAddCourseTemplate() throws Exception {
        // When
        var resultActions = mockMvc.perform(get("/addcourse"));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("add-course"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attribute("course", instanceOf(Course.class)))
                .andExpect(model().attributeDoesNotExist("errorMessage"))
                .andExpect(model().attributeDoesNotExist("successMessage"));
    }

    @Test
    @DisplayName("POST /addcourse - Redirects to Index on Valid Course")
    void addValidCourse_RedirectsToIndex() throws Exception {
        // Given
        var validCourse = generateTestCourseToSave();

        // When
        var resultActions = mockMvc.perform(post("/addcourse")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", validCourse));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
        verify(restClient, times(1)).createCourse(validCourse);
    }

    @Test
    @DisplayName("POST /addcourse - Returns Add Course Template with Errors on Invalid Course")
    void addInvalidCourse_ReturnsAddCourseTemplateWithErrors() throws Exception {
        // Given
        var invalidCourse = new Course();

        // When
        var resultActions = mockMvc.perform(post("/addcourse")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", invalidCourse));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("course", "name", "category", "author"))
                .andExpect(view().name("add-course"));

        // Verify that restClient.createCourse() is never called with an invalid course
        verify(restClient, never()).createCourse(eq(invalidCourse));
    }

    @Test
    @DisplayName("GET /update/{id} - Returns Update Course Template with Selected Course")
    void getUpdateCourseForm_ReturnsUpdateCourseTemplateWithSelectedCourse() throws Exception {
        // Given
        var course = generateTestSavedCourse();
        when(restClient.getCourseById(course.getId())).thenReturn(course);

        // When
        var resultActions = mockMvc.perform(get("/update/{id}", course.getId()));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("update-course"))
                .andExpect(model().attribute("course", equalTo(course)));
        verify(restClient, times(1)).getCourseById(course.getId());
    }

    @Test
    @DisplayName("POST /update/{id} - Redirects to Index on Valid Course Update")
    void updateValidCourse_RedirectsToIndex() throws Exception {
        // Given
        var courseToUpdate = generateTestSavedCourse();

        // When
        var resultActions = mockMvc.perform(post("/update/{id}", courseToUpdate.getId())
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", courseToUpdate));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
        verify(restClient, times(1)).updateCourse(courseToUpdate.getId(), courseToUpdate);
    }

    @Test
    @DisplayName("POST /update/{id} - Returns Update Course Template with Errors on Invalid Course Update")
    void updateInvalidCourse_ReturnsUpdateCourseTemplateWithErrors() throws Exception {
        // Given
        var invalidCourse = new Course();

        // When
        var resultActions = mockMvc.perform(post("/update/{id}", 1L)
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", invalidCourse));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("course", "name", "category", "author"))
                .andExpect(view().name("update-course"));
        verify(restClient, never()).updateCourse(any(), any());
    }

    @Test
    @DisplayName("GET /delete/{id} - Redirects to Index for Admin Role")
    @WithMockUser(roles = {"ADMIN"})
    void deleteCourseWithAdminRole_RedirectsToIndex() throws Exception {
        // When
        var resultActions = mockMvc.perform(get("/delete/{id}", 1L));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
        verify(restClient, times(1)).deleteCourseById(1L);
    }

    @Test
    @DisplayName("GET /delete/{id} - Redirects to Access Denied for User Role")
    void deleteCourseWithUserRole_RedirectsToAccessDenied() throws Exception {
        // When
        var resultActions = mockMvc.perform(get("/delete/{id}", 1L));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accessDenied"));
        verify(restClient, never()).deleteCourseById(1L);
    }

    @DisplayName("POST /search - Returns Index Template with Matching Courses")
    @ParameterizedTest(name = "Search courses with name: {0}, category: {1}, and rating: {2}")
    @MethodSource("searchTestParameters")
    void searchCourses_ReturnsIndexTemplateWithMatchingCourses(String name, String category, int rating) throws Exception {
        // Given
        var searchModel = Course.builder().name(name).category(category).rating(rating).build();
        when(restClient.searchSimilarCourses(searchModel.getName(), searchModel.getCategory(), searchModel.getRating()))
                .thenReturn(Collections.emptyList());

        // When
        var resultActions = mockMvc.perform(post("/search")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", searchModel));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("courses"));
        verify(restClient, times(1))
                .searchSimilarCourses(searchModel.getName(), searchModel.getCategory(), searchModel.getRating());
    }

    static Stream<Arguments> searchTestParameters() {
        return Stream.of(
                Arguments.of("Groovy", "Programming", 5),
                Arguments.of("", "Programming", 4),
                Arguments.of("", "", 3)
        );
    }


    @Test
    @DisplayName("GET /search - Returns Search Template")
    void getSearchPage_ReturnsSearchTemplate() throws Exception {
        // When
        var resultActions = mockMvc.perform(get("/search"));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("search-course"))
                .andExpect(model().attribute("searchModel", equalToObject(new Course())));
    }

    @DisplayName("Authenticated URLs - Access Denied for Anonymous User")
    @ParameterizedTest(name = "URL: {0}")
    @ValueSource(strings = {"/index", "/addcourse", "/update/1", "/search"})
    @WithAnonymousUser
    void authenticatedURLs_AccessDeniedForAnonymous(String url) throws Exception {
        // When
        var resultActions = mockMvc.perform(get(url));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verifyNoInteractions(restClient);
    }

    private static List<Course> getTestData(){
        return List.of(
                Course.builder()
                        .id(1L)
                        .name("Machine Learning Fundamentals")
                        .category("Data Science")
                        .rating(4)
                        .description("Introduction to Machine Learning concepts.")
                        .author("Jane Smith")
                        .build(),

                Course.builder()
                        .id(2L)
                        .name("Web Development Bootcamp")
                        .category("Web Development")
                        .rating(5)
                        .description("Learn full-stack web development.")
                        .author("Mike Johnson")
                        .build(),

                Course.builder()
                        .id(3L)
                        .name("Artificial Intelligence Foundations")
                        .category("Artificial Intelligence")
                        .rating(4)
                        .description("Foundational concepts of Artificial Intelligence.")
                        .author("Alex Lee")
                        .build(),

                Course.builder()
                        .id(4L)
                        .name("Spanish for Beginners")
                        .category("Languages")
                        .rating(3)
                        .description("Beginner's course in learning Spanish.")
                        .author("Maria Rodriguez")
                        .build(),

                Course.builder()
                        .id(5L)
                        .name("React.js Crash Course")
                        .category("Web Development")
                        .rating(4)
                        .description("Quick overview of React.js fundamentals.")
                        .author("Chris Brown")
                        .build(),

                Course.builder()
                        .id(6L)
                        .name("Python for Data Analysis")
                        .category("Data Science")
                        .rating(4)
                        .description("Using Python for data analysis.")
                        .author("Emily Wang")
                        .build(),

                Course.builder()
                        .id(7L)
                        .name("Java Programming 101")
                        .category("Programming")
                        .rating(3)
                        .description("Introduction to Java programming.")
                        .author("John Doe")
                        .build(),

                Course.builder()
                        .id(8L)
                        .name("Java Advanced Topics")
                        .category("Programming")
                        .rating(4)
                        .description("Advanced Java programming concepts.")
                        .author("John Doe")
                        .build());
    }

    private static Course generateTestCourseToSave() {
        return Course.builder()
                .name("JavaEE for Dummies")
                .category("JavaEE")
                .rating(4)
                .author("John Doe")
                .build();
    }

    private static Course generateTestSavedCourse() {
        return Course.builder()
                .id(1L)
                .name("JavaEE for Dummies")
                .category("JavaEE")
                .rating(4)
                .author("John Doe")
                .build();
    }
}