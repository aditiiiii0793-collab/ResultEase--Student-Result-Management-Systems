package com.example.resultease.repositories;
import com.example.resultease.models.Subject;
import com.example.resultease.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByTeacher(Teacher teacher);
}