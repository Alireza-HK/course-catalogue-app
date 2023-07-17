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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
public class CourseEndpointTest {
    @Autowired
    private CourseService courseService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenCreateCourse_thenReturnCreatedCourse() throws Exception {
        Course course = new Course("Rapid Spring Boot Application Development",
                "Spring",
                5,
                "Spring Boot gives all the power of the Spring Framework without all of the complexity",
                "John Doe"
        );
        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(post("/courses/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(course)))
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(jsonPath("$.name").value("Rapid Spring Boot Application Development"))
                .andExpect(jsonPath("$.category").value("Spring"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(status().isCreated()).andReturn().getResponse();

        Long id = JsonPath.parse(response.getContentAsString()).read("$.id", Long.class);
        assertNotNull(courseService.getCourseById(id));

    }

    @Test
    public void givenCourseId_whenGetCourse_thenReturnCourse() throws Exception {
        Course course = new Course("Rapid Spring Boot Application Development",
                "Spring",
                5,
                "Spring Boot gives all the power of the Spring Framework without all of the complexity",
                "John Doe"
        );
        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(post("/courses/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(course)))
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(jsonPath("$.name").value("Rapid Spring Boot Application Development"))
                .andExpect(jsonPath("$.category").value("Spring"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(status().isCreated()).andReturn().getResponse();
        Integer id = JsonPath.parse(response.getContentAsString()).read("$.id");

        mockMvc.perform(get("/courses/{id}",id))
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(jsonPath("$.name").value("Rapid Spring Boot Application Development"))
                .andExpect(jsonPath("$.category").value("Spring"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(status().isOk());

    }

    @Test
    public void givenInvalidCourseId_whenGetCourse_thenReturnNotFoundStatus() throws Exception {
        mockMvc.perform(get("/courses/{id}",100))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenCourseIdAndUpdatedCourse_whenUpdateCourse_thenReturnUpdatedCourse() throws Exception {
        Course course = new Course("Rapid Spring Boot Application Development",
                "Spring",
                3,
                "Spring Boot gives all the power of the Spring Framework without all of the complexity",
                "John Doe"
        );
        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(post("/courses/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(course)))
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(jsonPath("$.name").value("Rapid Spring Boot Application Development"))
                .andExpect(jsonPath("$.category").value("Spring"))
                .andExpect(jsonPath("$.rating").value(3))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(status().isCreated()).andReturn().getResponse();
        Integer id = JsonPath.parse(response.getContentAsString()).read("$.id");

        Course updatedCourse = new Course("Rapid Spring Boot Application Development",
                "Spring",
                5,
                "Spring Boot gives all the power of the Spring Framework without all of the complexity",
                "John Doe"
        );

        mockMvc.perform(put("/courses/{id}", id)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedCourse)))
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value("Rapid Spring Boot Application Development"))
                .andExpect(jsonPath("$.category").value("Spring"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(status().isNoContent());

    }

    @Test
    public void givenCourseId_whenDeleteCourse_thenCourseShouldBeDeleted() throws Exception {
        Course course = new Course("Rapid Spring Boot Application Development",
                "Spring",
                5,
                "Spring Boot gives all the power of the Spring Framework without all of the complexity",
                "John Doe"
        );        ObjectMapper objectMapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(post("/courses/")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(course)))
                .andDo(print())
                .andExpect(jsonPath("$.*", hasSize(6)))
                .andExpect(jsonPath("$.id", greaterThan(0)))
                .andExpect(jsonPath("$.name").value("Rapid Spring Boot Application Development"))
                .andExpect(jsonPath("$.category").value("Spring"))
                .andExpect(jsonPath("$.rating").value(5))
                .andExpect(jsonPath("$.author").value("John Doe"))
                .andExpect(status().isCreated()).andReturn().getResponse();
        Integer id = JsonPath.parse(response.getContentAsString()).read("$.id");

        mockMvc.perform(delete("/courses/{id}", id))
                .andDo(print())
                .andExpect(status().isNoContent());

    }
}
