package com.example.resultease.services;

import com.example.resultease.models.result; // Use lowercase 'result'
import com.example.resultease.models.Student;
import com.example.resultease.models.Subject;
import com.example.resultease.repositories.ResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ResultService {

    @Autowired
    private ResultRepository resultRepository;

    public void saveOrUpdateResult(result formData, String assessmentType) {
        Optional<result> existingResultOpt = resultRepository.findByStudentIdAndSubjectId(
            formData.getStudent().getId(),
            formData.getSubject().getId()
        );

        result resultToSave;
        if (existingResultOpt.isPresent()) {
            // Update existing record
            resultToSave = existingResultOpt.get();
            if ("ia1".equalsIgnoreCase(assessmentType) && formData.getIa1Marks() != null) {
                resultToSave.setIa1Marks(formData.getIa1Marks());
            } else if ("ia2".equalsIgnoreCase(assessmentType) && formData.getIa2Marks() != null) {
                resultToSave.setIa2Marks(formData.getIa2Marks());
            } else if ("termwork".equalsIgnoreCase(assessmentType) && formData.getTermWorkMarks() != null) {
                resultToSave.setTermWorkMarks(formData.getTermWorkMarks());
            } else if ("finalexam".equalsIgnoreCase(assessmentType) && formData.getFinalExamMarks() != null) {
                resultToSave.setFinalExamMarks(formData.getFinalExamMarks());
            }
        } else {
            // Create a new record
            resultToSave = new result(); // Use lowercase 'result'
            resultToSave.setStudent(formData.getStudent());
            resultToSave.setSubject(formData.getSubject());
             if ("ia1".equalsIgnoreCase(assessmentType) && formData.getIa1Marks() != null) {
                resultToSave.setIa1Marks(formData.getIa1Marks());
            } else if ("ia2".equalsIgnoreCase(assessmentType) && formData.getIa2Marks() != null) {
                resultToSave.setIa2Marks(formData.getIa2Marks());
            } else if ("termwork".equalsIgnoreCase(assessmentType) && formData.getTermWorkMarks() != null) {
                resultToSave.setTermWorkMarks(formData.getTermWorkMarks());
            } else if ("finalexam".equalsIgnoreCase(assessmentType) && formData.getFinalExamMarks() != null) {
                resultToSave.setFinalExamMarks(formData.getFinalExamMarks());
            }
        }
        
        // --- NEW: Calculate and save total/grade ---
        updateCalculatedFields(resultToSave);
        
        resultRepository.save(resultToSave);
    }

    public List<result> getResultsForStudent(Long studentId) {
        return resultRepository.findByStudentId(studentId);
    }

    public Map<Long, result> getResultsMapForStudentsAndSubject(List<Student> students, Subject subject) {
        List<Long> studentIds = students.stream().map(Student::getId).collect(Collectors.toList());
        List<result> results = resultRepository.findByStudentIdInAndSubjectId(studentIds, subject.getId());
        return results.stream().collect(Collectors.toMap(result -> result.getStudent().getId(), result -> result));
    }

    // --- NEW HELPER METHOD ---
    private void updateCalculatedFields(result result) {
        // Get total obtained marks from the transient method
        int totalObtained = result.getTotalMarksObtained();
        // Get total max marks from the transient method
        int maxMarks = result.getOverallMaxMarks();
        
        if (maxMarks > 0) {
            // Calculate percentage
            double percentage = ((double) totalObtained / maxMarks) * 100;
            result.setPercentage(percentage); // Save percentage to the DB
            
            // Simple grading logic
            if (percentage >= 90) result.setGrade("A+");
            else if (percentage >= 80) result.setGrade("A");
            else if (percentage >= 70) result.setGrade("B");
            else if (percentage >= 60) result.setGrade("C");
            else if (percentage >= 50) result.setGrade("D");
            else if (percentage >= 40) result.setGrade("E");
            else result.setGrade("F");
        } else {
            // Avoid division by zero, set defaults
            result.setPercentage(0.0);
            result.setGrade("N/A");
        }
    }
}