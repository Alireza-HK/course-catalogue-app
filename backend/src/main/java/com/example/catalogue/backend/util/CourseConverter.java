package com.example.catalogue.backend.util;

import com.example.catalogue.backend.entity.CourseEntity;
import com.example.catalogue.common.model.Course;
import org.springframework.beans.BeanUtils;

public class CourseConverter {

    public static Course toModel(CourseEntity course){
        Course model = new Course();
        BeanUtils.copyProperties(course, model);
        return model;
    }

    public static CourseEntity toEntity(Course course){
        CourseEntity entity = new CourseEntity();
        BeanUtils.copyProperties(course, entity);
        return entity;
    }
}
