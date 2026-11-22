package com.examsphere.service;

import com.examsphere.exception.AuthorizationException;
import com.examsphere.model.*;
import com.examsphere.storage.FileStorageService;
import com.examsphere.util.GradeCalculator;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service managing semester result calculation, letter grade and grade point assignment,
 * GPA/CGPA computation, and authorized publication workflows.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ResultService {

    private static ResultService instance;

    private final FileStorageService storage;
    private final List<Result> results;

    private ResultService() {
        this.storage = FileStorageService.getInstance();
        this.results = new ArrayList<>(storage.loadResults());
        // Clean initial state: users input and calculate their own results
    }

    public static synchronized ResultService getInstance() {
        if (instance == null) {
            instance = new ResultService();
        }
        return instance;
    }

    private void seedInitialResults() {
        Result r1 = new Result("RES-2026-001", "STU001", 5);
        r1.addCourseResult("CSE221", "A+", 4.00, 3);
        r1.addCourseResult("CSE222", "A", 3.75, 1);
        r1.setCgpa(3.92);
        r1.setPublished(true);
        r1.setPublishedBy("ADMIN001");
        r1.setPublishDate("2026-11-10");

        Result r2 = new Result("RES-2026-002", "STU002", 5);
        r2.addCourseResult("CSE221", "A-", 3.50, 3);
        r2.addCourseResult("CSE222", "B+", 3.25, 1);
        r2.setCgpa(3.45);
        r2.setPublished(true);
        r2.setPublishedBy("ADMIN001");
        r2.setPublishDate("2026-11-10");

        results.add(r1);
        results.add(r2);
        storage.saveResults(results);
    }

    /**
     * Calculates results for all students enrolled in courses for a given semester.
     */
    public synchronized void calculateResults(String courseCode, int semester) {
        CourseService courseService = CourseService.getInstance();
        MarksService marksService = MarksService.getInstance();

        List<Course> semesterCourses = courseService.getCoursesBySemester(semester);
        if (courseCode != null && !courseCode.isEmpty()) {
            semesterCourses = semesterCourses.stream()
                    .filter(c -> courseCode.equalsIgnoreCase(c.getCourseCode()))
                    .collect(Collectors.toList());
        }

        // Gather all enrolled students
        Set<String> studentIds = new HashSet<>();
        for (Course c : semesterCourses) {
            studentIds.addAll(c.getEnrolledStudentIds());
        }

        for (String studentId : studentIds) {
            Result result = getResult(studentId, semester);
            if (result == null) {
                String resultId = "RES-" + semester + "-" + studentId;
                result = new Result(resultId, studentId, semester);
                results.add(result);
            }

            for (Course c : semesterCourses) {
                if (c.getEnrolledStudentIds().contains(studentId)) {
                    MarkRecord mrk = marksService.getMarks(c.getCourseCode(), studentId);
                    if (mrk != null) {
                        String grade = mrk.calculateGrade();
                        double gp = GradeCalculator.getGradePoint(grade);
                        result.addCourseResult(c.getCourseCode(), grade, gp, c.getCredits());
                    }
                }
            }
            result.setCgpa(result.getGpa());
        }
        storage.saveResults(results);
    }

    public synchronized Result getResult(String studentId, int semester) {
        if (studentId == null) return null;
        for (Result r : results) {
            if (r.getStudentId().equalsIgnoreCase(studentId) && r.getSemester() == semester) {
                return r;
            }
        }
        return null;
    }

    public synchronized List<Result> getResultsBySemester(int semester) {
        if (semester <= 0) return new ArrayList<>(results);
        return results.stream()
                .filter(r -> r.getSemester() == semester)
                .collect(Collectors.toList());
    }

    public synchronized List<Result> getResultsByStudent(String studentId) {
        if (studentId == null) return new ArrayList<>();
        return results.stream()
                .filter(r -> studentId.equalsIgnoreCase(r.getStudentId()))
                .collect(Collectors.toList());
    }

    /**
     * Publishes results for a semester with strict Admin authorization check.
     */
    public synchronized void publishResults(int semester, String adminUserId) throws AuthorizationException {
        User user = AuthService.getInstance().getUserById(adminUserId);
        if (user == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            throw new AuthorizationException("Only the Examination Controller (Admin) can publish results.");
        }

        for (Result r : results) {
            if (semester <= 0 || r.getSemester() == semester) {
                r.setPublished(true);
                r.setPublishedBy(adminUserId);
                r.setPublishDate(LocalDate.now().toString());
            }
        }
        storage.saveResults(results);
    }

    public synchronized void unpublishResults(int semester, String adminUserId) {
        for (Result r : results) {
            if (semester <= 0 || r.getSemester() == semester) {
                r.setPublished(false);
            }
        }
        storage.saveResults(results);
    }

    public synchronized List<Result> getPublishedResults() {
        return results.stream()
                .filter(Result::isPublished)
                .collect(Collectors.toList());
    }

    public synchronized List<Result> getUnpublishedResults() {
        return results.stream()
                .filter(r -> !r.isPublished())
                .collect(Collectors.toList());
    }

    public synchronized Map<String, Object> getResultStats(int semester) {
        Map<String, Object> stats = new HashMap<>();
        List<Result> list = getResultsBySemester(semester);
        int total = list.size();
        if (total == 0) {
            stats.put("total", 0);
            stats.put("passCount", 0);
            stats.put("failCount", 0);
            stats.put("passRate", 0.0);
            stats.put("failRate", 0.0);
            stats.put("avgGPA", 0.0);
            return stats;
        }

        int pass = 0;
        double gpaSum = 0;
        for (Result r : list) {
            gpaSum += r.getGpa();
            if (r.getGpa() >= 2.0) {
                pass++;
            }
        }
        int fail = total - pass;
        double passRate = Math.round((pass * 100.0 / total) * 10.0) / 10.0;
        double failRate = Math.round((fail * 100.0 / total) * 10.0) / 10.0;
        double avgGPA = Math.round((gpaSum / total) * 100.0) / 100.0;

        stats.put("total", total);
        stats.put("passCount", pass);
        stats.put("failCount", fail);
        stats.put("passRate", passRate);
        stats.put("failRate", failRate);
        stats.put("avgGPA", avgGPA);
        return stats;
    }
}

