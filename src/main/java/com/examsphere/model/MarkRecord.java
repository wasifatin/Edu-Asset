package com.examsphere.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents assessment marks for a student in a course across multiple components.
 * Supports Theory courses, Lab courses, boundary validation, and letter grade derivation.
 *
 * Theory Distribution: Attendance (7), Assignment (5), Quiz 1 (15), Quiz 2 (15), Quiz 3 (15),
 *                      Quiz Average (15), Presentation (8), Midterm (25), Final (40) = 100
 * Lab Distribution:    Attendance (10), Lab Report (25), Lab Performance (25), Final Exam (40) = 100
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class MarkRecord implements Serializable {

    private static final long serialVersionUID = 14L;

    private String markId;
    private String courseCode;
    private String studentId;
    private String courseType; // "THEORY" or "LAB"

    // Common / Theory components
    private double attendance;
    private double assignment;
    private double quiz1;
    private double quiz2;
    private double quiz3;
    private double quizAverage;
    private double presentation;
    private double midterm;
    private double finalExam;

    // Lab components
    private double labReport;
    private double labPerformance;

    // Legacy / Class Test support
    private double classTest;
    private double maxClassTest;
    private double maxAssignment;
    private double maxMidterm;
    private double maxFinalExam;

    private double totalMarks;
    private String grade;

    /**
     * Default constructor.
     */
    public MarkRecord() {
        this.courseType = "THEORY";
        this.maxClassTest = 20.0;
        this.maxAssignment = 10.0;
        this.maxMidterm = 30.0;
        this.maxFinalExam = 40.0;
        this.grade = "F";
    }

    /**
     * Parameterized constructor.
     */
    public MarkRecord(String markId, String courseCode, String studentId) {
        this();
        this.markId = markId;
        this.courseCode = courseCode;
        this.studentId = studentId;
    }

    /**
     * Parameterized constructor with course type.
     */
    public MarkRecord(String markId, String courseCode, String studentId, String courseType) {
        this(markId, courseCode, studentId);
        this.courseType = courseType != null ? courseType.toUpperCase() : "THEORY";
    }

    public static MarkRecord createTheoryRecord(String markId, String courseCode, String studentId,
                                                double att, double asg, double q1, double q2, double q3, double qAvg,
                                                double pres, double mid, double fin) {
        MarkRecord r = new MarkRecord(markId, courseCode, studentId, "THEORY");
        r.setAttendance(att);
        r.setAssignment(asg);
        r.setQuiz1(q1);
        r.setQuiz2(q2);
        r.setQuiz3(q3);
        r.setQuizAverage(qAvg);
        r.setPresentation(pres);
        r.setMidterm(mid);
        r.setFinalExam(fin);
        r.calculateTotal();
        return r;
    }

    public static MarkRecord createLabRecord(String markId, String courseCode, String studentId,
                                             double att, double rep, double perf, double fin) {
        MarkRecord r = new MarkRecord(markId, courseCode, studentId, "LAB");
        r.setAttendance(att);
        r.setLabReport(rep);
        r.setLabPerformance(perf);
        r.setFinalExam(fin);
        r.calculateTotal();
        return r;
    }

    /**
     * Calculates and updates total marks based on course evaluation scheme.
     */
    public double calculateTotal() {
        if ("LAB".equalsIgnoreCase(this.courseType)) {
            this.totalMarks = this.attendance + this.labReport + this.labPerformance + this.finalExam;
        } else if (this.attendance > 0 || this.presentation > 0 || this.quizAverage > 0) {
            this.totalMarks = this.attendance + this.assignment + this.quizAverage + this.presentation + this.midterm + this.finalExam;
        } else if (this.classTest > 0) {
            this.totalMarks = this.classTest + this.assignment + this.midterm + this.finalExam;
        } else if ("THEORY".equalsIgnoreCase(this.courseType)) {
            this.totalMarks = this.attendance + this.assignment + this.quizAverage + this.presentation + this.midterm + this.finalExam;
        } else {
            this.totalMarks = this.classTest + this.assignment + this.midterm + this.finalExam;
        }
        calculateGrade();
        return this.totalMarks;
    }

    /**
     * Derives letter grade based on percentage of component maximum marks (both Theory and Lab total 100).
     */
    public String calculateGrade() {
        double maxTotal = 100.0;
        double percentage = (totalMarks / maxTotal) * 100.0;

        if (percentage >= 80.0) {
            this.grade = "A+";
        } else if (percentage >= 75.0) {
            this.grade = "A";
        } else if (percentage >= 70.0) {
            this.grade = "A-";
        } else if (percentage >= 65.0) {
            this.grade = "B+";
        } else if (percentage >= 60.0) {
            this.grade = "B";
        } else if (percentage >= 55.0) {
            this.grade = "B-";
        } else if (percentage >= 50.0) {
            this.grade = "C+";
        } else if (percentage >= 45.0) {
            this.grade = "C";
        } else if (percentage >= 40.0) {
            this.grade = "D";
        } else {
            this.grade = "F";
        }
        return this.grade;
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    public String getMarkId() { return markId; }
    public void setMarkId(String markId) { this.markId = markId; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    public double getAttendance() { return attendance; }
    public void setAttendance(double attendance) { this.attendance = attendance; calculateTotal(); }

    public double getAssignment() { return assignment; }
    public void setAssignment(double assignment) { this.assignment = assignment; calculateTotal(); }

    public double getQuiz1() { return quiz1; }
    public void setQuiz1(double quiz1) { this.quiz1 = quiz1; calculateTotal(); }

    public double getQuiz2() { return quiz2; }
    public void setQuiz2(double quiz2) { this.quiz2 = quiz2; calculateTotal(); }

    public double getQuiz3() { return quiz3; }
    public void setQuiz3(double quiz3) { this.quiz3 = quiz3; calculateTotal(); }

    public double getQuizAverage() { return quizAverage; }
    public void setQuizAverage(double quizAverage) { this.quizAverage = quizAverage; calculateTotal(); }

    public double getPresentation() { return presentation; }
    public void setPresentation(double presentation) { this.presentation = presentation; calculateTotal(); }

    public double getMidterm() { return midterm; }
    public void setMidterm(double midterm) { this.midterm = midterm; calculateTotal(); }

    public double getFinalExam() { return finalExam; }
    public void setFinalExam(double finalExam) { this.finalExam = finalExam; calculateTotal(); }

    public double getLabReport() { return labReport; }
    public void setLabReport(double labReport) { this.labReport = labReport; calculateTotal(); }

    public double getLabPerformance() { return labPerformance; }
    public void setLabPerformance(double labPerformance) { this.labPerformance = labPerformance; calculateTotal(); }

    public double getClassTest() { return classTest; }
    public void setClassTest(double classTest) { this.classTest = classTest; calculateTotal(); }

    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double totalMarks) { this.totalMarks = totalMarks; calculateGrade(); }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public double getMaxClassTest() { return maxClassTest; }
    public void setMaxClassTest(double maxClassTest) { this.maxClassTest = maxClassTest; }

    public double getMaxAssignment() { return maxAssignment; }
    public void setMaxAssignment(double maxAssignment) { this.maxAssignment = maxAssignment; }

    public double getMaxMidterm() { return maxMidterm; }
    public void setMaxMidterm(double maxMidterm) { this.maxMidterm = maxMidterm; }

    public double getMaxFinalExam() { return maxFinalExam; }
    public void setMaxFinalExam(double maxFinalExam) { this.maxFinalExam = maxFinalExam; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkRecord that = (MarkRecord) o;
        return Objects.equals(markId, that.markId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(markId);
    }

    @Override
    public String toString() {
        return "MarkRecord{" +
                "markId=" + markId +
                ", courseCode=" + courseCode +
                ", studentId=" + studentId +
                ", type=" + courseType +
                ", totalMarks=" + totalMarks +
                ", grade=" + grade +
                '}';
    }
}
