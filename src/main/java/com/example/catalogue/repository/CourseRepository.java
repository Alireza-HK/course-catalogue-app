package com.example.catalogue.repository;

import com.example.catalogue.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

    @Query("""
            select c 
            from Course c 
            where c.name like %:name% 
            and c.category like %:category% 
            and c.rating >= :rating
            """)
    Iterable<Course> searchSimilarCourses(@Param("name") String name, @Param("category") String category, @Param("rating") int rating);

}