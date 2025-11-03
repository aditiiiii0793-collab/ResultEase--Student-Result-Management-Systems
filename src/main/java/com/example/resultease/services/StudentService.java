package com.example.resultease.services;

import com.example.resultease.models.Student;
import com.example.resultease.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public void saveStudent(Student student) {
        studentRepository.save(student);
    }

    // --- New Methods ---
    public Student getStudentById(Long id) {
        Optional<Student> optional = studentRepository.findById(id);
        return optional.orElse(null);
    }

    public void updateStudent(Student student) {
        studentRepository.save(student); // Save works for updates
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}