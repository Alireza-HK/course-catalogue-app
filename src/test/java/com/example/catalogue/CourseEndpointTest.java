package com.example.catalogue;

import com.example.catalogue.model.Course;
import com.example.catalogue.service.CourseService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@WithMockUser(username = "user", roles = {"USER"})
public class CourseEndpointTest {

    private final CourseService courseService;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

    @Autowired
    public CourseEndpointTest(CourseService courseService, MockMvc mockMvc, ObjectMapper objectMapper) {
        this.courseService = courseService;
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
    }

    @Test
    public void whenCreateCourse_thenReturnCreatedCourse() throws Exception {
        Course course = Course.builder().name("JavaEE for Dummies").category("Programming").rating(4).author("John Doe").build();

        ResultActions result = mockMvc.perform(post("/courses/")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andDo(print())
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(jsonPath("$.name").value("JavaEE for Dummies"))
                .andExpect(jsonPath("$.category").value("Programming"))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(status().isCreated());

        Long id = JsonPath.parse(result.andReturn().getResponse().getContentAsString()).read("$.id", Long.class);
        assertNotNull(courseService.getCourseById(id));

    }

    @Test
    public void givenCourseId_whenGetCourse_thenReturnCourse() throws Exception {
        Course course = Course.builder().name("JavaEE for Dummies").category("Programming").rating(4).author("John Doe").build();

        ResultActions postResult = mockMvc.perform(post("/courses/")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isCreated());

        Integer id = JsonPath.parse(postResult.andReturn().getResponse().getContentAsString()).read("$.id");

        mockMvc.perform(get("/courses/{id}", id))
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(jsonPath("$.name").value("JavaEE for Dummies"))
                .andExpect(jsonPath("$.category").value("Programming"))
                .andExpect(jsonPath("$.rating").value(4))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(status().isOk());

    }

    @Test
    public void givenInvalidCourseId_whenGetCourse_thenReturnNotFoundStatus() throws Exception {
        mockMvc.perform(get("/courses/{id}", 100).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenCourseIdAndUpdatedCourse_whenUpdateCourse_thenReturnUpdatedCourse() throws Exception {
        Course course = Course.builder().name("JavaEE for Dummies").category("Programming").rating(4).author("John Doe").build();

        ResultActions postResult = mockMvc.perform(post("/courses/")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isCreated());

        Integer id = JsonPath.parse(postResult.andReturn().getResponse().getContentAsString()).read("$.id");

        Course updatedCourse = Course.builder().name("JavaEE for Dummies - 2nd Edition").category("Programming").rating(5).author("John Doe").build();

        mockMvc.perform(put("/courses/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(updatedCourse)))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("JavaEE for Dummies - 2nd Edition"))
                .andExpect(jsonPath("$.category").value("Programming"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(status().isNoContent());

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void givenCourseId_whenDeleteCourse_thenCourseShouldBeDeleted() throws Exception {
        Course course = Course.builder().name("JavaEE for Dummies").category("Programming").rating(4).author("John Doe").build();

        ResultActions postResult = mockMvc.perform(post("/courses/")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(course)))
                .andExpect(status().isCreated());

        Integer id = JsonPath.parse(postResult.andReturn().getResponse().getContentAsString()).read("$.id");

        mockMvc.perform(delete("/courses/{id}", id).with(csrf()))
                .andExpect(status().isNoContent());

    }

    //todo: test not admin
}
