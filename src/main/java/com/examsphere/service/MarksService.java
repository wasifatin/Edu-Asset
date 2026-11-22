package com.examsphere.service;

import com.examsphere.exception.InvalidMarksException;
import com.examsphere.model.MarkRecord;
import com.examsphere.storage.FileStorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing assessment marks entry, component validation, boundary checks,
 * and automated total and letter grade calculations for Theory and Lab courses.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class MarksService {

    private static MarksService instance;

    private final FileStorageService storage;
    private final List<MarkRecord> marks;

    private MarksService() {
        this.storage = FileStorageService.getInstance();
        this.marks = new ArrayList<>(storage.loadMarks());
    }

    public static synchronized MarksService getInstance() {
        if (instance == null) {
            instance = new MarksService();
        }
        return instance;
    }

    /**
     * Enforces that no obtained marks component can exceed its defined maximum marks
     * or be negative.
     */
    public void validateMarks(MarkRecord record) throws InvalidMarksException {
        if (record == null) {
            throw new InvalidMarksException("Mark record cannot be null.");
        }

        if ("LAB".equalsIgnoreCase(record.getCourseType())) {
            if (record.getAttendance() < 0 || record.getAttendance() > 10.0) {
                throw new InvalidMarksException("Lab Attendance marks (" + record.getAttendance() +
                        ") must be between 0 and maximum 10.0");
            }
            if (record.getLabReport() < 0 || record.getLabReport() > 25.0) {
                throw new InvalidMarksException("Lab Report marks (" + record.getLabReport() +
                        ") must be between 0 and maximum 25.0");
            }
            if (record.getLabPerformance() < 0 || record.getLabPerformance() > 25.0) {
                throw new InvalidMarksException("Lab Performance marks (" + record.getLabPerformance() +
                        ") must be between 0 and maximum 25.0");
            }
            if (record.getFinalExam() < 0 || record.getFinalExam() > 40.0) {
                throw new InvalidMarksException("Lab Final Exam marks (" + record.getFinalExam() +
                        ") must be between 0 and maximum 40.0");
            }
        } else if ("THEORY".equalsIgnoreCase(record.getCourseType()) && (record.getAttendance() > 0 || record.getPresentation() > 0 || record.getQuizAverage() > 0)) {
            if (record.getAttendance() < 0 || record.getAttendance() > 7.0) {
                throw new InvalidMarksException("Theory Attendance marks (" + record.getAttendance() +
                        ") must be between 0 and maximum 7.0");
            }
            if (record.getAssignment() < 0 || record.getAssignment() > 5.0) {
                throw new InvalidMarksException("Assignment marks (" + record.getAssignment() +
                        ") must be between 0 and maximum 5.0");
            }
            if (record.getQuiz1() < 0 || record.getQuiz1() > 15.0) {
                throw new InvalidMarksException("Quiz 1 marks (" + record.getQuiz1() +
                        ") must be between 0 and maximum 15.0");
            }
            if (record.getQuiz2() < 0 || record.getQuiz2() > 15.0) {
                throw new InvalidMarksException("Quiz 2 marks (" + record.getQuiz2() +
                        ") must be between 0 and maximum 15.0");
            }
            if (record.getQuiz3() < 0 || record.getQuiz3() > 15.0) {
                throw new InvalidMarksException("Quiz 3 marks (" + record.getQuiz3() +
                        ") must be between 0 and maximum 15.0");
            }
            if (record.getQuizAverage() < 0 || record.getQuizAverage() > 15.0) {
                throw new InvalidMarksException("Quiz Average marks (" + record.getQuizAverage() +
                        ") must be between 0 and maximum 15.0");
            }
            if (record.getPresentation() < 0 || record.getPresentation() > 8.0) {
                throw new InvalidMarksException("Presentation marks (" + record.getPresentation() +
                        ") must be between 0 and maximum 8.0");
            }
            if (record.getMidterm() < 0 || record.getMidterm() > 25.0) {
                throw new InvalidMarksException("Midterm marks (" + record.getMidterm() +
                        ") must be between 0 and maximum 25.0");
            }
            if (record.getFinalExam() < 0 || record.getFinalExam() > 40.0) {
                throw new InvalidMarksException("Final Exam marks (" + record.getFinalExam() +
                        ") must be between 0 and maximum 40.0");
            }
        } else {
            // Standard / legacy validation
            if (record.getClassTest() < 0 || record.getClassTest() > record.getMaxClassTest()) {
                throw new InvalidMarksException("Class Test marks (" + record.getClassTest() +
                        ") must be between 0 and maximum " + record.getMaxClassTest());
            }
            if (record.getAssignment() < 0 || record.getAssignment() > record.getMaxAssignment()) {
                throw new InvalidMarksException("Assignment marks (" + record.getAssignment() +
                        ") must be between 0 and maximum " + record.getMaxAssignment());
            }
            if (record.getMidterm() < 0 || record.getMidterm() > record.getMaxMidterm()) {
                throw new InvalidMarksException("Midterm marks (" + record.getMidterm() +
                        ") must be between 0 and maximum " + record.getMaxMidterm());
            }
            if (record.getFinalExam() < 0 || record.getFinalExam() > record.getMaxFinalExam()) {
                throw new InvalidMarksException("Final Exam marks (" + record.getFinalExam() +
                        ") must be between 0 and maximum " + record.getMaxFinalExam());
            }
        }
    }

    public synchronized void enterMarks(MarkRecord record) throws InvalidMarksException {
        validateMarks(record);
        record.calculateTotal();

        for (int i = 0; i < marks.size(); i++) {
            MarkRecord existing = marks.get(i);
            if (existing.getCourseCode().equalsIgnoreCase(record.getCourseCode()) &&
                existing.getStudentId().equalsIgnoreCase(record.getStudentId())) {
                marks.set(i, record);
                storage.saveMarks(marks);
                return;
            }
        }

        marks.add(record);
        storage.saveMarks(marks);
    }

    public synchronized void updateMarks(MarkRecord record) throws InvalidMarksException {
        enterMarks(record);
    }

    public synchronized boolean deleteMarks(String courseCode, String studentId) {
        if (courseCode == null || studentId == null) return false;
        boolean removed = marks.removeIf(m -> m.getCourseCode().equalsIgnoreCase(courseCode) &&
                                              m.getStudentId().equalsIgnoreCase(studentId));
        if (removed) {
            storage.saveMarks(marks);
        }
        return removed;
    }

    public synchronized MarkRecord getMarks(String courseCode, String studentId) {
        if (courseCode == null || studentId == null) return null;
        for (MarkRecord m : marks) {
            if (m.getCourseCode().equalsIgnoreCase(courseCode) && m.getStudentId().equalsIgnoreCase(studentId)) {
                return m;
            }
        }
        return null;
    }

    public synchronized List<MarkRecord> getMarksByCourse(String courseCode) {
        if (courseCode == null) return new ArrayList<>();
        return marks.stream()
                .filter(m -> courseCode.equalsIgnoreCase(m.getCourseCode()))
                .collect(Collectors.toList());
    }

    public synchronized List<MarkRecord> getMarksByStudent(String studentId) {
        if (studentId == null) return new ArrayList<>();
        return marks.stream()
                .filter(m -> studentId.equalsIgnoreCase(m.getStudentId()))
                .collect(Collectors.toList());
    }

    public synchronized List<MarkRecord> getAllMarks() {
        return new ArrayList<>(marks);
    }
}
