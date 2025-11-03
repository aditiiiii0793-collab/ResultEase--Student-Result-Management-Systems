package com.example.resultease.services;
import com.example.resultease.models.Subject;
import com.example.resultease.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional; // Import Optional

@Service
public class SubjectService {
    @Autowired private SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public void saveSubject(Subject subject) {
        subjectRepository.save(subject);
    }

    // New methods for Edit/Delete
    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id).orElse(null);
    }

    public void updateSubject(Subject subject) {
        subjectRepository.save(subject); // Save works for updates too
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }
}