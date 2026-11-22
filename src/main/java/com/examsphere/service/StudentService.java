package com.examsphere.service;

import com.examsphere.exception.DuplicateStudentIdException;
import com.examsphere.model.Student;
import com.examsphere.model.User;
import com.examsphere.storage.FileStorageService;
import com.examsphere.util.PasswordUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing student lifecycle, records, filtering, searching, and enrollment state.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class StudentService {

    private static StudentService instance;

    private final AuthService authService;
    private final FileStorageService storage;

    private StudentService() {
        this.authService = AuthService.getInstance();
        this.storage = FileStorageService.getInstance();
        ensureInitialStudents();
    }

    public static synchronized StudentService getInstance() {
        if (instance == null) {
            instance = new StudentService();
        }
        return instance;
    }

    private void ensureInitialStudents() {
        // Clean initial state: users input their own student records
    }

    public synchronized void addStudent(Student student) throws DuplicateStudentIdException {
        if (student == null || student.getStudentId() == null) {
            throw new IllegalArgumentException("Student information cannot be null.");
        }
        authService.createUser(student);
    }

    public synchronized void updateStudent(Student student) {
        if (student == null) return;
        authService.updateUser(student);
    }

    public synchronized void deleteStudent(String studentId) {
        if (studentId == null) return;
        User user = authService.getUserById(studentId);
        if (user instanceof Student) {
            user.setActive(false);
            authService.updateUser(user);
        }
    }

    public synchronized Student getStudentById(String studentId) {
        if (studentId == null) return null;
        User user = authService.getUserById(studentId);
        if (user instanceof Student) {
            return (Student) user;
        }
        return null;
    }

    public synchronized List<Student> getAllStudents() {
        return authService.getAllUsers().stream()
                .filter(u -> u instanceof Student)
                .map(u -> (Student) u)
                .collect(Collectors.toList());
    }

    public synchronized List<Student> searchStudents(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllStudents();
        }
        String q = query.trim().toLowerCase();
        return getAllStudents().stream()
                .filter(s -> s.matchesSearch(q))
                .collect(Collectors.toList());
    }

    public synchronized List<Student> filterByDepartment(String departmentId) {
        if (departmentId == null || departmentId.trim().isEmpty() || "ALL".equalsIgnoreCase(departmentId)) {
            return getAllStudents();
        }
        return getAllStudents().stream()
                .filter(s -> departmentId.equalsIgnoreCase(s.getDepartment()))
                .collect(Collectors.toList());
    }

    public synchronized List<Student> filterBySemester(int semester) {
        if (semester <= 0) {
            return getAllStudents();
        }
        return getAllStudents().stream()
                .filter(s -> s.getSemester() == semester)
                .collect(Collectors.toList());
    }
}

