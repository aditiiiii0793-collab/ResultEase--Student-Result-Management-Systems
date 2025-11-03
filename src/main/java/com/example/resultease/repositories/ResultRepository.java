package com.example.resultease.repositories;

import com.example.resultease.models.result; // Use lowercase 'result'
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<result, Long> {

    List<result> findByStudentId(Long studentId);

    Optional<result> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    List<result> findByStudentIdInAndSubjectId(List<Long> studentIds, Long subjectId);
    
    // --- ADDED THIS METHOD ---
    List<result> findBySubjectId(Long subjectId);
}