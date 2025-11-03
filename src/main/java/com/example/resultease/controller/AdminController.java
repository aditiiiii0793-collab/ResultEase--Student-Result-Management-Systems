package com.example.resultease.controller;

import com.example.resultease.models.*;
import com.example.resultease.repositories.*;
import com.example.resultease.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private CourseService courseService;
    @Autowired private SubjectService subjectService;
    @Autowired private TeacherService teacherService;
    @Autowired private StudentService studentService;
    @Autowired private UserRepository userRepo;
    @Autowired private PasswordEncoder passwordEncoder;
    
    @Autowired private SubjectRepository subjectRepo; 
    @Autowired private ResultRepository resultRepo;

    // --- Admin Dashboard ---
    @GetMapping("/dashboard")
    public String showAdminDashboard(Model model) {
        model.addAttribute("courseCount", courseService.getAllCourses().size());
        model.addAttribute("teacherCount", teacherService.getAllTeachers().size());
        model.addAttribute("studentCount", studentService.getAllStudents().size());
        return "admin_dashboard";
    }

    // --- Course Mappings ---
    @GetMapping("/courses")
    public String manageCourses(Model model) {
        model.addAttribute("newCourse", new course());
        model.addAttribute("courseList", courseService.getAllCourses());
        return "manage_courses";
    }
    
    @PostMapping("/courses/add")
    public String addCourse(@ModelAttribute course course) {
        courseService.saveCourse(course);
        return "redirect:/admin/courses";
    }
    
    @GetMapping("/courses/edit/{id}")
    public String showEditCourseForm(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        return "edit_course";
    }
    
    @PostMapping("/courses/update/{id}")
    public String updateCourse(@PathVariable Long id, @ModelAttribute course course) {
        course.setId(id);
        courseService.saveCourse(course);
        return "redirect:/admin/courses";
    }
    
    @GetMapping("/courses/delete/{id}")
    public String deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return "redirect:/admin/courses";
    }

    // --- Subject Mappings ---
    @GetMapping("/subjects")
    public String manageSubjects(Model model) {
        model.addAttribute("newSubject", new Subject());
        model.addAttribute("subjectList", subjectService.getAllSubjects());
        model.addAttribute("courseList", courseService.getAllCourses());
        model.addAttribute("teacherList", teacherService.getAllTeachers());
        return "manage_subjects";
    }
    
    @PostMapping("/subjects/add")
    public String addSubject(@ModelAttribute Subject subject, RedirectAttributes redirectAttributes) {
        if (subject.getIa1MaxMarks() == null) subject.setIa1MaxMarks(20);
        if (subject.getIa2MaxMarks() == null) subject.setIa2MaxMarks(20);
        if (subject.getTermWorkMaxMarks() == null) subject.setTermWorkMaxMarks(20);
        if (subject.getFinalExamMaxMarks() == null) subject.setFinalExamMaxMarks(60);
        subjectService.saveSubject(subject);
        redirectAttributes.addFlashAttribute("success", "Subject " + subject.getName() + " added successfully.");
        return "redirect:/admin/subjects";
    }
    
    @GetMapping("/subjects/edit/{id}")
    public String showEditSubjectForm(@PathVariable Long id, Model model) {
        model.addAttribute("subject", subjectService.getSubjectById(id));
        model.addAttribute("courseList", courseService.getAllCourses());
        model.addAttribute("teacherList", teacherService.getAllTeachers());
        return "edit_subject";
    }
    
    @PostMapping("/subjects/update/{id}")
    public String updateSubject(@PathVariable Long id, @ModelAttribute Subject subjectFormData, RedirectAttributes redirectAttributes) {
        Subject existingSubject = subjectService.getSubjectById(id);
        if (existingSubject != null) {
            existingSubject.setName(subjectFormData.getName());
            existingSubject.setCourse(subjectFormData.getCourse());
            if (subjectFormData.getTeacher() == null || subjectFormData.getTeacher().getId() == null) {
                existingSubject.setTeacher(null);
            } else {
                Teacher assignedTeacher = teacherService.getTeacherById(subjectFormData.getTeacher().getId());
                existingSubject.setTeacher(assignedTeacher);
            }
            existingSubject.setIa1MaxMarks(subjectFormData.getIa1MaxMarks());
            existingSubject.setIa2MaxMarks(subjectFormData.getIa2MaxMarks());
            existingSubject.setTermWorkMaxMarks(subjectFormData.getTermWorkMaxMarks());
            existingSubject.setFinalExamMaxMarks(subjectFormData.getFinalExamMaxMarks());
            subjectService.updateSubject(existingSubject);
            redirectAttributes.addFlashAttribute("success", "Subject " + existingSubject.getName() + " updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Subject not found for update.");
        }
        return "redirect:/admin/subjects";
    }
    
    @GetMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            List<result> results = resultRepo.findBySubjectId(id);
            resultRepo.deleteAll(results);
            subjectService.deleteSubject(id);
            redirectAttributes.addFlashAttribute("success", "Subject and its results deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting subject: " + e.getMessage());
        }
        return "redirect:/admin/subjects";
    }

    // --- Teacher Mappings ---
    @GetMapping("/teachers")
    public String manageTeachers(Model model) {
        model.addAttribute("newTeacher", new Teacher());
        model.addAttribute("teacherList", teacherService.getAllTeachers());
        return "manage_teachers";
    }
    
    @PostMapping("/teachers/add")
    public String addTeacher(@ModelAttribute Teacher teacher, RedirectAttributes redirectAttributes) {
        teacherService.saveTeacher(teacher);
        String initialPassword = generateInitialPassword(teacher.getName(), teacher.getBirthDate());
        if (initialPassword == null) {
            redirectAttributes.addFlashAttribute("error", "Could not generate password. Check name/birthdate.");
            return "redirect:/admin/teachers";
        }
        User user = new User();
        user.setUsername(teacher.getEmail());
        user.setPassword(passwordEncoder.encode(initialPassword));
        user.setRole("ROLE_TEACHER");
        user.setEmail(teacher.getEmail());
        user.setMustChangePassword(true);
        userRepo.save(user);
        redirectAttributes.addFlashAttribute("success", "Teacher " + teacher.getName() + " added. Initial password: " + initialPassword);
        return "redirect:/admin/teachers";
    }
    
    @GetMapping("/teachers/edit/{id}")
    public String showEditTeacherForm(@PathVariable Long id, Model model) {
        model.addAttribute("teacher", teacherService.getTeacherById(id));
        return "edit_teacher";
    }
    
    @PostMapping("/teachers/update/{id}")
    public String updateTeacher(@PathVariable Long id, @ModelAttribute Teacher teacher) {
        teacher.setId(id);
        teacherService.updateTeacher(teacher);
        return "redirect:/admin/teachers";
    }
    
    @GetMapping("/teachers/delete/{id}")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Teacher teacher = teacherService.getTeacherById(id);
            if (teacher == null) {
                redirectAttributes.addFlashAttribute("error", "Teacher not found.");
                return "redirect:/admin/teachers";
            }
            List<Subject> subjects = subjectRepo.findByTeacher(teacher);
            for (Subject sub : subjects) {
                sub.setTeacher(null);
                subjectRepo.save(sub);
            }
            Optional<User> userOpt = userRepo.findByUsername(teacher.getEmail());
            userOpt.ifPresent(user -> userRepo.delete(user));
            teacherService.deleteTeacher(id);
            redirectAttributes.addFlashAttribute("success", "Teacher deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting teacher: " + e.getMessage());
        }
        return "redirect:/admin/teachers";
    }

    // --- Student Mappings ---
    @GetMapping("/students")
    public String manageStudents(Model model) {
        model.addAttribute("newStudent", new Student());
        model.addAttribute("studentList", studentService.getAllStudents());
        model.addAttribute("courseList", courseService.getAllCourses());
        return "manage_students";
    }
    
    @PostMapping("/students/add")
    public String addStudent(@ModelAttribute Student student, RedirectAttributes redirectAttributes) {
        // ========== FIX: Fetch the complete course object ==========
        if (student.getCourse() != null && student.getCourse().getId() != null) {
            course fullCourse = courseService.getCourseById(student.getCourse().getId());
            if (fullCourse != null) {
                student.setCourse(fullCourse);
                System.out.println("DEBUG: Course loaded - ID: " + fullCourse.getId() + ", Name: " + fullCourse.getName());
            } else {
                System.out.println("DEBUG: ⚠️ Course not found with ID: " + student.getCourse().getId());
                redirectAttributes.addFlashAttribute("error", "Invalid course selected.");
                return "redirect:/admin/students";
            }
        } else {
            System.out.println("DEBUG: ⚠️ No course selected!");
            redirectAttributes.addFlashAttribute("error", "Please select a course.");
            return "redirect:/admin/students";
        }
        // ========== END FIX ==========
        
        studentService.saveStudent(student);
        System.out.println("DEBUG: Student saved - ID: " + student.getId() + ", Course: " + student.getCourse().getName());
        
        String initialPassword = generateInitialPassword(student.getName(), student.getBirthDate());
        if (initialPassword == null) {
             redirectAttributes.addFlashAttribute("error", "Could not generate password for " + student.getName() + ". Check name/birthdate.");
            return "redirect:/admin/students";
        }
        User user = new User();
        user.setUsername(student.getEmail());
        user.setPassword(passwordEncoder.encode(initialPassword));
        user.setRole("ROLE_STUDENT");
        user.setEmail(student.getEmail());
        user.setMustChangePassword(true);
        userRepo.save(user);
        redirectAttributes.addFlashAttribute("success", "Student " + student.getName() + " added. Initial password: " + initialPassword);
        return "redirect:/admin/students";
    }
    
    @GetMapping("/students/edit/{id}")
    public String showEditStudentForm(@PathVariable Long id, Model model) {
        model.addAttribute("student", studentService.getStudentById(id));
        model.addAttribute("courseList", courseService.getAllCourses());
        return "edit_student";
    }
    
    @PostMapping("/students/update/{id}")
    public String updateStudent(@PathVariable Long id, @ModelAttribute Student student) {
        // ========== FIX: Fetch the complete course object for update too ==========
        if (student.getCourse() != null && student.getCourse().getId() != null) {
            course fullCourse = courseService.getCourseById(student.getCourse().getId());
            if (fullCourse != null) {
                student.setCourse(fullCourse);
            }
        }
        // ========== END FIX ==========
        
        student.setId(id);
        studentService.updateStudent(student);
        return "redirect:/admin/students";
    }
    
    @GetMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Student student = studentService.getStudentById(id);
            if (student == null) {
                redirectAttributes.addFlashAttribute("error", "Student not found.");
                return "redirect:/admin/students";
            }
            List<result> results = resultRepo.findByStudentId(id);
            resultRepo.deleteAll(results);
            Optional<User> userOpt = userRepo.findByUsername(student.getEmail());
            userOpt.ifPresent(user -> userRepo.delete(user));
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("success", "Student deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting student: " + e.getMessage());
        }
        return "redirect:/admin/students";
    }

    // --- Admin Password Reset Mappings ---
    @GetMapping("/users/reset-password/{id}")
    public String showAdminResetPasswordForm(@PathVariable Long id, @RequestParam String userType, Model model, RedirectAttributes redirectAttributes) {
        String userName = "";
        String userLoginName = "";
        User userToReset = null;
        
        if ("teacher".equalsIgnoreCase(userType)) {
            Teacher teacher = teacherService.getTeacherById(id);
            if (teacher != null && teacher.getEmail() != null) {
                userName = teacher.getName();
                userLoginName = teacher.getEmail();
                userToReset = userRepo.findByUsername(userLoginName).orElse(null);
            }
        } else if ("student".equalsIgnoreCase(userType)) {
            Student student = studentService.getStudentById(id);
             if (student != null && student.getEmail() != null) {
                userName = student.getName();
                userLoginName = student.getEmail();
                userToReset = userRepo.findByUsername(userLoginName).orElse(null);
            }
        }
        
        if (userToReset == null) {
             redirectAttributes.addFlashAttribute("error", "Could not find login account associated with " + userName + ".");
             return "redirect:/admin/" + (userType.equals("teacher") ? "teachers" : "students");
        }
        
        model.addAttribute("userId", userToReset.getId());
        model.addAttribute("userType", userType);
        model.addAttribute("userName", userName);
        return "admin_reset_password";
    }
    
    @PostMapping("/users/reset-password")
    public String processAdminResetPassword(@RequestParam Long userId, @RequestParam String userType, @RequestParam String newPassword, @RequestParam String confirmPassword, RedirectAttributes redirectAttributes) {
        String redirectUrl = "redirect:/admin/" + (userType.equals("teacher") ? "teachers" : "students");
        
        if (newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("error", "New password must be at least 8 characters.");
            return redirectUrl;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return redirectUrl;
        }
        
        Optional<User> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "User account not found.");
            return redirectUrl;
        }
        
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepo.save(user);
        redirectAttributes.addFlashAttribute("success", "Password reset successfully for user " + user.getUsername());
        return redirectUrl;
    }

    // --- Helper Method for Password Generation (firstname@ddmmyyyy) ---
    private String generateInitialPassword(String fullName, LocalDate birthDate) {
        if (fullName == null || fullName.isBlank() || birthDate == null) {
            return null;
        }
        String[] names = fullName.trim().split("\\s+");
        if (names.length == 0) return null;
        String firstName = names[0].toLowerCase().replaceAll("[^a-z0-9]", "");
        String ddmmyyyy = birthDate.format(DateTimeFormatter.ofPattern("ddMMyyyy"));
        return firstName + "@" + ddmmyyyy;
    }
}