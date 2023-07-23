package com.example.catalogue.frontend.model;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    private Long id;

    @NotEmpty(message = "Course name field can't be empty")
    private String name;

    @NotEmpty(message = "Course category field can't be empty")
    private String category;

    @Min(value = 1, message = "Minimum rating value is 1")
    @Max(value = 5, message = "Maximum rating value is 5")
    private int rating;

    private String description;

    @NotEmpty(message = "Course author field can't be empty")
    private String author;

}
