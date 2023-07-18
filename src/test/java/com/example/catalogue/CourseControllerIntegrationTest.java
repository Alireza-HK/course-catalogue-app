package com.example.catalogue;

import com.example.catalogue.model.Course;
import com.example.catalogue.service.CourseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseService courseService;

    private static final List<Course> TEST_DATA = List.of(
            Course.builder().name("JavaEE for Dummies").category("Programming").rating(3).author("John Doe").build(),
            Course.builder().name("Javascript for Beginners").category("Programming").rating(3).author("John Muller").build(),
            Course.builder().name("What Is This Thing Called Science?").category("Science").rating(5).author("Alan Chalmers").build(),
            Course.builder().name("Suomen mestari").category("Languages").rating(2).author("Sonja Gehring").build(),
            Course.builder().name("Spring in Action").category("Programming").rating(5).author("John Doe").build()
    );

    @BeforeEach
    void setUp() {
        when(courseService.getAllCourses()).thenReturn(TEST_DATA);
    }

    @Test
    @DisplayName("GET /index returns index template with courses when courses are available")
    void getIndexPage_ReturnsIndexTemplateWithCourses() throws Exception {
        // When
        ResultActions resultActions = mockMvc.perform(get("/index"));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("courses", hasSize(TEST_DATA.size())))
                .andExpect(model().attribute("courses", containsInAnyOrder(TEST_DATA.toArray())))
                .andExpect(model().attributeExists("searchModel"));
        verify(courseService, times(1)).getAllCourses();
    }

    @Test
    @DisplayName("GET /addcourse returns add-course template")
    void getAddCourseForm_ReturnsAddCourseTemplate() throws Exception {
        // When
        ResultActions resultActions = mockMvc.perform(get("/addcourse"));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("add-course"));
    }

    @Test
    @DisplayName("POST /addcourse redirects to /index when a valid course is provided")
    void addValidCourse_RedirectsToIndex() throws Exception {
        // Given
        Course validCourse = Course.builder()
                .name("Testing for Dummies")
                .category("Programming")
                .rating(4)
                .author("John Doe")
                .build();

        // When
        ResultActions resultActions = mockMvc.perform(post("/addcourse")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", validCourse));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
        verify(courseService, times(1)).createCourse(validCourse);
    }

    @Test
    @DisplayName("POST /addcourse returns add-course template with errors when an invalid course is provided")
    void addInvalidCourse_ReturnsAddCourseTemplateWithErrors() throws Exception {
        // Given
        Course course = new Course();

        // When
        ResultActions resultActions = mockMvc.perform(post("/addcourse")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", course));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("course", "name", "category", "author"))
                .andExpect(view().name("add-course"));
        verify(courseService, times(0)).createCourse(any());
    }

    @Test
    @DisplayName("GET /update/{id} returns update-course template with the selected course")
    void getUpdateCourseForm_ReturnsUpdateCourseTemplateWithSelectedCourse() throws Exception{
        // Given
        when(courseService.getCourseById(anyLong())).thenReturn(TEST_DATA.get(0));

        // When
        ResultActions resultActions = mockMvc.perform(get("/update/{id}", 1L));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("update-course"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attribute("course", equalToObject(TEST_DATA.get(0))));
        verify(courseService, times(1)).getCourseById(1L);
    }

    @Test
    @DisplayName("PUT /update/{id} redirects to /index when a valid course is provided")
    void updateValidCourse_RedirectsToIndex() throws Exception {
        // Given
        Course course = Course.builder().name("JavaEE for Dummies").category("Programming").rating(3).author("John Doe").build();

        // When
        ResultActions resultActions = mockMvc.perform(put("/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", course));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
        verify(courseService, times(1)).updateCourse(1L, course);
    }

    @Test
    @DisplayName("PUT /update/{id} returns update-course template with errors when an invalid course is provided")
    void updateInvalidCourse_ReturnsUpdateCourseTemplateWithErrors() throws Exception {
        // Given
        Course invalidCourse = new Course();

        // When
        ResultActions resultActions = mockMvc.perform(put("/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", invalidCourse));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("course", "name", "category", "author"))
                .andExpect(view().name("update-course"));
        verify(courseService, times(0)).updateCourse(any(), any());
    }

    @Test
    @DisplayName("DELETE /delete/{id} redirects to /index")
    void deleteCourse_RedirectsToIndex() throws Exception {
        // When
        ResultActions resultActions = mockMvc.perform(delete("/delete/{id}", 1L));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
        verify(courseService, times(1)).deleteCourseById(1L);
    }

    @Test
    @DisplayName("POST /search returns index template with matching courses when search criteria is provided")
    void searchCourses_ReturnsIndexTemplateWithMatchingCourses() throws Exception {
        // Given
        Course searchModel = Course.builder().category("Programming").build();
        when(courseService.searchSimilarCourses(anyString(), anyString(), anyInt())).thenReturn(Collections.emptyList());

        // When
        ResultActions resultActions = mockMvc.perform(post("/search")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", searchModel));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("courses"));
        verify(courseService, times(1)).searchSimilarCourses(searchModel.getName(), searchModel.getCategory(), searchModel.getRating());
    }

    @Test
    @DisplayName("GET /search returns search template")
    void getSearchPage_ReturnsSearchTemplate() throws Exception {
        // When
        ResultActions resultActions = mockMvc.perform(get("/search"));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attributeExists("searchModel"))
                .andExpect(model().attribute("searchModel", equalToObject(new Course())));
    }
}