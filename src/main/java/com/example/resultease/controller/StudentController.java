package com.example.resultease.controller;

import com.example.resultease.models.result;
import com.example.resultease.models.Student;
import com.example.resultease.models.course;
import com.example.resultease.repositories.StudentRepository;
import com.example.resultease.services.ResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap; // Import HashMap

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired private ResultService resultService;
    @Autowired private StudentRepository studentRepo;

    @GetMapping("/dashboard")
    public String showStudentDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        Optional<Student> studentOpt = studentRepo.findByEmail(currentUsername);

        if (studentOpt.isPresent()) {
            Student student = studentOpt.get();
            List<result> results = resultService.getResultsForStudent(student.getId());
            
            // Group results by Course
            Map<course, List<result>> resultsByCourse = results.stream()
                .filter(r -> r.getSubject() != null && r.getSubject().getCourse() != null)
                .collect(Collectors.groupingBy(r -> r.getSubject().getCourse()));

            // --- NEW: Calculate Percentage/Grade PER COURSE ---
            Map<String, Double> percentagePerCourse = new HashMap<>();
            Map<String, String> gradePerCourse = new HashMap<>();

            for (Map.Entry<course, List<result>> entry : resultsByCourse.entrySet()) {
                course course = entry.getKey();
                List<result> courseResults = entry.getValue();
                
                double totalObtained = 0;
                double totalMax = 0;
                for (result res : courseResults) {
                    totalObtained += res.getTotalMarksObtained();
                    totalMax += res.getOverallMaxMarks();
                }
                
                double percentage = (totalMax > 0) ? (totalObtained * 100.0 / totalMax) : 0;
                String grade = calculateOverallGrade(percentage);
                
                percentagePerCourse.put(course.getName(), percentage);
                gradePerCourse.put(course.getName(), grade);
            }
            // --- END NEW CALCULATION ---

            model.addAttribute("student", student);
            model.addAttribute("resultsByCourse", resultsByCourse); // Pass the grouped map
            model.addAttribute("percentagePerCourse", percentagePerCourse); // Pass percentage map
            model.addAttribute("gradePerCourse", gradePerCourse); // Pass grade map
            
        } else {
            model.addAttribute("student", null);
            model.addAttribute("resultsByCourse", Collections.emptyMap());
            model.addAttribute("percentagePerCourse", Collections.emptyMap());
            model.addAttribute("gradePerCourse", Collections.emptyMap());
        }
        return "student_dashboard";
    }

    private String calculateOverallGrade(double percentage) {
        if (percentage >= 90) return "A+ (Excellent)";
        else if (percentage >= 80) return "A (Very Good)";
        else if (percentage >= 70) return "B (Good)";
        else if (percentage >= 60) return "C (Average)";
        else if (percentage >= 50) return "D (Pass)";
        else if (percentage >= 40) return "E (Pass)";
        else return "F (Fail)";
    }
}