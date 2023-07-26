package com.example.catalogue.backend.repository;

import com.example.catalogue.backend.entity.CourseEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository<CourseEntity, Long> {

    @Query("""
            select c 
            from CourseEntity c 
            where lower(c.name) like lower(concat('%', :name, '%'))
            and lower(c.category) like lower(concat('%', :category, '%'))
            and c.rating >= :rating
            """)
    Iterable<CourseEntity> searchSimilarCourses(@Param("name") String name, @Param("category") String category, @Param("rating") int rating);

}