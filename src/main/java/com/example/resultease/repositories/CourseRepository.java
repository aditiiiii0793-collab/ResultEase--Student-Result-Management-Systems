package com.example.resultease.repositories;
import com.example.resultease.models.course;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CourseRepository extends JpaRepository<course, Long> {}