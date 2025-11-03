package com.example.resultease.services;

import com.example.resultease.models.Teacher;
import com.example.resultease.repositories.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // Import Optional

@Service
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;

    // Get all teachers
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    // Save a new teacher or update an existing one
    public void saveTeacher(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    // Get a single teacher by their ID
    public Teacher getTeacherById(Long id) {
        Optional<Teacher> optional = teacherRepository.findById(id);
        return optional.orElse(null);
    }

    // Update an existing teacher (same as save)
    public void updateTeacher(Teacher teacher) {
        teacherRepository.save(teacher);
    }

    // Delete a teacher by their ID
    public void deleteTeacher(Long id) {
        teacherRepository.deleteById(id);
    }
}