package com.example.resultease.repositories;

import com.example.resultease.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Method to find all students belonging to a specific course ID
    List<Student> findByCourseId(Long courseId);

    // Kept this in case it's needed, but we will use findByEmail for login
    Optional<Student> findByRollNumber(String rollNumber);
    
    // --- NEW METHOD ---
    // Method to find a student by their email address
    Optional<Student> findByEmail(String email);
}