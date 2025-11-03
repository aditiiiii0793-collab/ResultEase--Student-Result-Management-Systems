package com.example.resultease.controller; // Your singular package name

import com.example.resultease.models.*;
import com.example.resultease.repositories.*;
import com.example.resultease.services.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/teacher") // Add base path for all teacher URLs
public class TeacherController {

    @Autowired private SubjectRepository subjectRepo;
    @Autowired private TeacherRepository teacherRepo;
    @Autowired private StudentRepository studentRepo;
    @Autowired private ResultService resultService;

    @GetMapping("/dashboard")
    public String showTeacherDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
             return "redirect:/login";
        }
        String currentUsername = authentication.getName();
        System.out.println("Attempting to find teacher with email: " + currentUsername); // DEBUG
        Optional<Teacher> teacherOpt = teacherRepo.findByEmail(currentUsername);
        System.out.println("Teacher found: " + teacherOpt.isPresent()); // DEBUG

        if (teacherOpt.isPresent()) {
            Teacher teacher = teacherOpt.get();
            List<Subject> assignedSubjects = subjectRepo.findByTeacher(teacher);
            model.addAttribute("teacher", teacher);
            model.addAttribute("subjects", assignedSubjects);
        } else {
             model.addAttribute("teacher", null);
             model.addAttribute("subjects", Collections.emptyList());
        }
        return "teacher_dashboard";
    }

    @GetMapping("/subject/{id}")
    public String showSubjectAssessments(@PathVariable Long id, Model model) {
        Subject subject = subjectRepo.findById(id).orElse(null);
        model.addAttribute("subject", subject);
        return "teacher_select_assessment";
    }

    @GetMapping("/subject/{subjectId}/assessment/{assessmentType}")
    public String showMarksEntryForm(@PathVariable Long subjectId, @PathVariable String assessmentType, Model model) {
        Subject subject = subjectRepo.findById(subjectId).orElse(null);
        Map<Long, result> resultsMap = Collections.emptyMap(); // Use lowercase 'result'
        List<Student> students = Collections.emptyList();

        if (subject != null && subject.getCourse() != null) {
            students = studentRepo.findByCourseId(subject.getCourse().getId());
            if (!students.isEmpty()) {
                 resultsMap = resultService.getResultsMapForStudentsAndSubject(students, subject);
            }
        }
        model.addAttribute("students", students);
        model.addAttribute("subject", subject);
        model.addAttribute("assessmentType", assessmentType);
        model.addAttribute("resultsMap", resultsMap);
        model.addAttribute("marksForm", new MarksEntryDTO());

        return "teacher_marks_entry";
    }

    @PostMapping("/subject/saveMarks")
    public String saveMarks(@ModelAttribute MarksEntryDTO form, @RequestParam("subjectId") Long subjectId, @RequestParam("assessmentType") String assessmentType) {
        Subject subject = subjectRepo.findById(subjectId).orElse(null);
        if (subject != null) {
            for (result resultData : form.getResults()) { // Use lowercase 'result'
                 if (resultData.getStudent() != null) {
                    resultData.setSubject(subject);
                    resultService.saveOrUpdateResult(resultData, assessmentType);
                 }
            }
        }
        return "redirect:/teacher/subject/" + subjectId;
    }
}