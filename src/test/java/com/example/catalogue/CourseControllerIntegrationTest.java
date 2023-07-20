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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "user", roles = {"USER"})
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
    void getIndexPage_ReturnsIndexTemplateWithCourses() throws Exception {
        // When
        var resultActions = mockMvc.perform(get("/index"));

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
    void getAddCourseForm_ReturnsAddCourseTemplate() throws Exception {
        // When
        var resultActions = mockMvc.perform(get("/addcourse"));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("add-course"));
    }

    @Test
    void addValidCourse_RedirectsToIndex() throws Exception {
        // Given
        var validCourse = Course.builder()
                .name("Testing for Dummies")
                .category("Programming")
                .rating(4)
                .author("John Doe")
                .build();

        // When
        var resultActions = mockMvc.perform(post("/addcourse")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", validCourse));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
        verify(courseService, times(1)).createCourse(validCourse);
    }

    @Test
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
        verify(courseService, times(0)).createCourse(any());
    }

    @Test
    void getUpdateCourseForm_ReturnsUpdateCourseTemplateWithSelectedCourse() throws Exception {
        // Given
        when(courseService.getCourseById(anyLong())).thenReturn(TEST_DATA.get(0));

        // When
        var resultActions = mockMvc.perform(get("/update/{id}", 1L));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("update-course"))
                .andExpect(model().attributeExists("course"))
                .andExpect(model().attribute("course", equalToObject(TEST_DATA.get(0))));
        verify(courseService, times(1)).getCourseById(1L);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateValidCourse_RedirectsToIndex() throws Exception {
        // Given
        var course = Course.builder().name("JavaEE for Dummies").category("Programming").rating(3).author("John Doe").build();

        // When
        var resultActions = mockMvc.perform(post("/update/{id}", 1L)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .flashAttr("course", course));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
        verify(courseService, times(1)).updateCourse(1L, course);
    }

    @Test
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
        verify(courseService, times(0)).updateCourse(any(), any());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCourseWithAdminRole_RedirectsToIndex() throws Exception {
        // When
        var resultActions = mockMvc.perform(get("/delete/{id}", 1L));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/index"));
        verify(courseService, times(1)).deleteCourseById(1L);
    }

    @Test
    void deleteCourseWithUserRole_RedirectsToAccessDenied() throws Exception {
        // When
        var resultActions = mockMvc.perform(get("/delete/{id}", 1L));

        // Then
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accessDenied"));
        verify(courseService, times(0)).deleteCourseById(1L);
    }

    //todo: test not admin
    //todo: access with annonymous

    @Test
    void searchCourses_ReturnsIndexTemplateWithMatchingCourses() throws Exception {
        // Given
        var searchModel = Course.builder().category("Programming").build();
        when(courseService.searchSimilarCourses(anyString(), anyString(), anyInt())).thenReturn(Collections.emptyList());

        // When
        var resultActions = mockMvc.perform(post("/search")
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
    void getSearchPage_ReturnsSearchTemplate() throws Exception {
        // When
        var resultActions = mockMvc.perform(get("/search"));

        // Then
        resultActions
                .andExpect(status().isOk())
                .andExpect(view().name("search-course"))
                .andExpect(model().attributeExists("searchModel"))
                .andExpect(model().attribute("searchModel", equalToObject(new Course())));
    }
}