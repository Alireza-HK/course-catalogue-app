package com.example.catalogue;

import com.example.catalogue.client.CourseFeignClient;
import com.example.catalogue.model.Course;
import com.example.catalogue.testutil.CourseTestDataFactory;
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
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = {"USER"})
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseFeignClient restClient;

    @BeforeEach
    void setUp() {
        when(restClient.getAllCourses()).thenReturn(CourseTestDataFactory.DATA);
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
                .andExpect(model().attribute("courses", hasSize(CourseTestDataFactory.DATA.size())))
                .andExpect(model().attribute("courses", containsInAnyOrder(CourseTestDataFactory.DATA.toArray())))

                // And: Verify that there are no errors in the model
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
                .andExpect(view().name("add-course"));
    }

    @Test
    @DisplayName("POST /addcourse - Redirects to Index on Valid Course")
    void addValidCourse_RedirectsToIndex() throws Exception {
        // Given
        var validCourse = CourseTestDataFactory.generateTestCourseToSave();

        // When
        var resultActions = mockMvc.perform(post("/addcourse")
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
        var course = new Course();

        // When
        var resultActions = mockMvc.perform(post("/addcourse")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", course));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("course", "name", "category", "author"))
                .andExpect(view().name("add-course"));

        // Verify that restClient.createCourse() is never called with an invalid course
        verify(restClient, never()).createCourse(eq(course));
    }

    @Test
    @DisplayName("GET /update/{id} - Returns Update Course Template with Selected Course")
    void getUpdateCourseForm_ReturnsUpdateCourseTemplateWithSelectedCourse() throws Exception {
        // Given
        var course = CourseTestDataFactory.generateTestSavedCourse();
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
        var courseToUpdate = CourseTestDataFactory.generateTestSavedCourse();

        // When
        var resultActions = mockMvc.perform(post("/update/{id}", courseToUpdate.getId())
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
    @WithMockUser(username = "admin", roles = {"ADMIN"})
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

    //todo: test not admin
    //todo: access with annonymous

    @DisplayName("POST /search - Returns Index Template with Matching Courses")
    @ParameterizedTest
    @MethodSource("searchTestParameters")
    void searchCourses_ReturnsIndexTemplateWithMatchingCourses(String name, String category, int rating) throws Exception {
        // Given
        var searchModel = Course.builder().name(name).category(category).rating(rating).build();
        when(restClient.searchSimilarCourses(searchModel.getName(), searchModel.getCategory(), searchModel.getRating()))
                .thenReturn(Collections.emptyList());

        // When
        var resultActions = mockMvc.perform(post("/search")
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
    @ParameterizedTest
    @ValueSource(strings = {"/index", "/addcourse", "/update/1", "/search"})
    @WithAnonymousUser
    void authenticatedURLs_AccessDeniedForAnonymous(String url) throws Exception {
        // When
        var resultActions = mockMvc.perform(get(url));

        // Then
        resultActions.andExpect(status().isUnauthorized());

        verifyNoInteractions(restClient);
    }

}