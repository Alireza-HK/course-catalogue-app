package com.example.catalogue.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "COURSES")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME")
    @NotEmpty(message = "Course name field can't be empty")
    private String name;

    @Column(name = "CATEGORY")
    @NotEmpty(message = "Course category field can't be empty")
    private String category;

    @Min(value = 1, message = "Minimum rating value is 1")
    @Max(value = 5, message = "maximum rating value is 5")
    @Column(name = "RATING")
    private int rating;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "AUTHOR")
    @NotEmpty(message = "Course author field can't be empty")
    private String author;

}
