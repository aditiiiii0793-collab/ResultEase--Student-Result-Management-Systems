package com.example.resultease.services;

import com.example.resultease.models.course;
import com.example.resultease.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // Import Optional

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // Get all courses
    public List<course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Save a new course or update an existing one
    public void saveCourse(course course) {
        courseRepository.save(course);
    }

    // --- New Methods ---
    // Get a single course by its ID
    public course getCourseById(Long id) {
        Optional<course> optional = courseRepository.findById(id);
        return optional.orElse(null); // Return null if not found
    }

    // Delete a course by its ID
    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}