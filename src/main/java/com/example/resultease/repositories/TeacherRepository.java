package com.example.resultease.repositories;

import com.example.resultease.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // Import Optional

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    // Method to find a teacher by their email address
    Optional<Teacher> findByEmail(String email);
}