package com.example.resultease.models;

import jakarta.persistence.*;

@Entity
@Table(name = "subject")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    // --- NEW FIELDS FOR MAX MARKS ---
    private Integer ia1MaxMarks = 20;       // Default to 20
    private Integer ia2MaxMarks = 20;       // Default to 20
    private Integer termWorkMaxMarks = 20;  // Default to 20
    private Integer finalExamMaxMarks = 60; // Default to 60 for end semester

    // --- Constructors ---
    public Subject() {}

    public Subject(String name, course course, Teacher teacher) {
        this.name = name;
        this.course = course;
        this.teacher = teacher;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public course getCourse() { return course; }
    public void setCourse(course course) { this.course = course; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public Integer getIa1MaxMarks() { return ia1MaxMarks; }
    public void setIa1MaxMarks(Integer ia1MaxMarks) { this.ia1MaxMarks = ia1MaxMarks; }

    public Integer getIa2MaxMarks() { return ia2MaxMarks; }
    public void setIa2MaxMarks(Integer ia2MaxMarks) { this.ia2MaxMarks = ia2MaxMarks; }

    public Integer getTermWorkMaxMarks() { return termWorkMaxMarks; }
    public void setTermWorkMaxMarks(Integer termWorkMaxMarks) { this.termWorkMaxMarks = termWorkMaxMarks; }

    public Integer getFinalExamMaxMarks() { return finalExamMaxMarks; }
    public void setFinalExamMaxMarks(Integer finalExamMaxMarks) { this.finalExamMaxMarks = finalExamMaxMarks; }

    // --- toString for debugging (optional) ---
    @Override
    public String toString() {
        return "Subject{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", course=" + (course != null ? course.getName() : "null") +
               ", teacher=" + (teacher != null ? teacher.getName() : "null") +
               ", ia1MaxMarks=" + ia1MaxMarks +
               ", ia2MaxMarks=" + ia2MaxMarks +
               ", termWorkMaxMarks=" + termWorkMaxMarks +
               ", finalExamMaxMarks=" + finalExamMaxMarks +
               '}';
    }
}