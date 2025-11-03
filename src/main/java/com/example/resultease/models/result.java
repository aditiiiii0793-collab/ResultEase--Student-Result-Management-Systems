package com.example.resultease.models;
import jakarta.persistence.*;

@Entity
@Table(name = "result")
public class result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    private Integer ia1Marks;
    private Integer ia2Marks;
    private Integer termWorkMarks;
    private Integer finalExamMarks; // --- ADDED ---

    private Double percentage; // --- ADDED ---
    private String grade;      // --- ADDED ---

    public result() {}

    public result(Student student, Subject subject) {
        this.student = student;
        this.subject = subject;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public Integer getIa1Marks() { return ia1Marks; }
    public void setIa1Marks(Integer ia1Marks) { this.ia1Marks = ia1Marks; }
    public Integer getIa2Marks() { return ia2Marks; }
    public void setIa2Marks(Integer ia2Marks) { this.ia2Marks = ia2Marks; }
    public Integer getTermWorkMarks() { return termWorkMarks; }
    public void setTermWorkMarks(Integer termWorkMarks) { this.termWorkMarks = termWorkMarks; }
    public Integer getFinalExamMarks() { return finalExamMarks; } // --- ADDED ---
    public void setFinalExamMarks(Integer finalExamMarks) { this.finalExamMarks = finalExamMarks; } // --- ADDED ---
    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    // --- Helper methods (not stored in DB) ---
    @Transient
    public Integer getTotalMarksObtained() {
        int total = 0;
        if (ia1Marks != null) total += ia1Marks;
        if (ia2Marks != null) total += ia2Marks;
        if (termWorkMarks != null) total += termWorkMarks;
        if (finalExamMarks != null) total += finalExamMarks;
        return total;
    }
    @Transient
    public Integer getOverallMaxMarks() {
        if (subject != null) {
            return (subject.getIa1MaxMarks() != null ? subject.getIa1MaxMarks() : 0) +
                   (subject.getIa2MaxMarks() != null ? subject.getIa2MaxMarks() : 0) +
                   (subject.getTermWorkMaxMarks() != null ? subject.getTermWorkMaxMarks() : 0) +
                   (subject.getFinalExamMaxMarks() != null ? subject.getFinalExamMaxMarks() : 0);
        }
        return 100; // Default max marks if subject is null
    }
}