package com.example.catalogue.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "COURSES")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
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


    public Course() {
    }

    public Course(String name, String category, int rating, String author) {
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.author = author;
    }

    public Course(Long id, String name, String category, int rating, String author) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.author = author;
    }

    public Course(Long id, String name, String category, int rating, String description, String author) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.description = description;
        this.author = author;
    }

    public Course(String name, String category, int rating, String description, String author) {
        this.name = name;
        this.category = category;
        this.rating = rating;
        this.description = description;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return rating == course.rating && Objects.equals(id, course.id) && Objects.equals(name, course.name) && Objects.equals(category, course.category) && Objects.equals(description, course.description) && Objects.equals(author, course.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, category, rating, description, author);
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", rating=" + rating +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
