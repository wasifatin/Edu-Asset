package com.examsphere.service;

import com.examsphere.model.Course;
import com.examsphere.model.Department;
import com.examsphere.storage.FileStorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing departments, academic programs, courses, credit allocations,
 * instructor assignments, and student enrollments.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class CourseService {

    private static CourseService instance;

    private final FileStorageService storage;
    private final List<Department> departments;
    private final List<Course> courses;

    private CourseService() {
        this.storage = FileStorageService.getInstance();
        this.departments = new ArrayList<>(storage.loadDepartments());
        this.courses = new ArrayList<>(storage.loadCourses());
        // Clean initial state: users input their own departments and curriculum courses
    }

    public static synchronized CourseService getInstance() {
        if (instance == null) {
            instance = new CourseService();
        }
        return instance;
    }

    private void seedInitialData() {
        // Seed departments
        Department cse = new Department("CSE", "Computer Science and Engineering");
        cse.setHeadOfDepartment("Dr. Sheak Rashed Haider");
        cse.addProgram("B.Sc. in CSE");
        cse.addProgram("M.Sc. in CSE");

        Department swe = new Department("SWE", "Software Engineering");
        swe.setHeadOfDepartment("Dr. Imran Mahmud");
        swe.addProgram("B.Sc. in SWE");

        Department eee = new Department("EEE", "Electrical and Electronic Engineering");
        eee.setHeadOfDepartment("Dr. Md. Shahid Ullah");
        eee.addProgram("B.Sc. in EEE");

        departments.add(cse);
        departments.add(swe);
        departments.add(eee);
        storage.saveDepartments(departments);

        // Seed courses
        Course c1 = new Course("CSE221", "Object Oriented Programming", "CSE", 3, 5);
        c1.setTeacherId("TCH001");
        c1.enrollStudent("STU001");
        c1.enrollStudent("STU002");

        Course c2 = new Course("CSE222", "Object Oriented Programming Lab", "CSE", 1, 5);
        c2.setTeacherId("TCH001");
        c2.enrollStudent("STU001");
        c2.enrollStudent("STU002");

        Course c3 = new Course("CSE223", "Database Management Systems", "CSE", 3, 5);
        c3.enrollStudent("STU001");

        Course c4 = new Course("SWE211", "Software Requirements and Modeling", "SWE", 3, 4);
        c4.enrollStudent("STU003");

        Course c5 = new Course("EEE201", "Electronic Circuits", "EEE", 3, 6);
        c5.enrollStudent("STU004");

        courses.add(c1);
        courses.add(c2);
        courses.add(c3);
        courses.add(c4);
        courses.add(c5);

        cse.addCourse("CSE221");
        cse.addCourse("CSE222");
        cse.addCourse("CSE223");
        swe.addCourse("SWE211");
        eee.addCourse("EEE201");

        storage.saveDepartments(departments);
        storage.saveCourses(courses);
    }

    // ─── Department Operations ──────────────────────────────────────────

    public synchronized void addDepartment(Department dept) {
        if (dept == null || dept.getDepartmentId() == null) return;
        if (getDepartmentById(dept.getDepartmentId()) == null) {
            departments.add(dept);
            storage.saveDepartments(departments);
        }
    }

    public synchronized void updateDepartment(Department dept) {
        if (dept == null) return;
        for (int i = 0; i < departments.size(); i++) {
            if (departments.get(i).getDepartmentId().equalsIgnoreCase(dept.getDepartmentId())) {
                departments.set(i, dept);
                storage.saveDepartments(departments);
                return;
            }
        }
    }

    public synchronized List<Department> getAllDepartments() {
        return new ArrayList<>(departments);
    }

    public synchronized Department getDepartmentById(String deptId) {
        if (deptId == null) return null;
        for (Department d : departments) {
            if (d.getDepartmentId().equalsIgnoreCase(deptId.trim())) {
                return d;
            }
        }
        return null;
    }

    // ─── Course Operations ──────────────────────────────────────────────

    public synchronized void addCourse(Course course) {
        if (course == null || course.getCourseCode() == null) return;
        if (getCourseByCode(course.getCourseCode()) == null) {
            courses.add(course);
            storage.saveCourses(courses);

            Department d = getDepartmentById(course.getDepartmentId());
            if (d != null) {
                d.addCourse(course.getCourseCode());
                storage.saveDepartments(departments);
            }
        }
    }

    public synchronized void updateCourse(Course course) {
        if (course == null) return;
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseCode().equalsIgnoreCase(course.getCourseCode())) {
                courses.set(i, course);
                storage.saveCourses(courses);
                return;
            }
        }
    }

    public synchronized void deleteCourse(String courseCode) {
        if (courseCode == null) return;
        courses.removeIf(c -> c.getCourseCode().equalsIgnoreCase(courseCode.trim()));
        for (Department d : departments) {
            d.removeCourse(courseCode);
        }
        storage.saveDepartments(departments);
        storage.saveCourses(courses);
    }

    public synchronized Course getCourseByCode(String courseCode) {
        if (courseCode == null) return null;
        for (Course c : courses) {
            if (c.getCourseCode().equalsIgnoreCase(courseCode.trim())) {
                return c;
            }
        }
        return null;
    }

    public synchronized List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public synchronized List<Course> getCoursesByDepartment(String departmentId) {
        if (departmentId == null || departmentId.trim().isEmpty() || "ALL".equalsIgnoreCase(departmentId)) {
            return getAllCourses();
        }
        return courses.stream()
                .filter(c -> departmentId.equalsIgnoreCase(c.getDepartmentId()))
                .collect(Collectors.toList());
    }

    public synchronized List<Course> getCoursesBySemester(int semester) {
        if (semester <= 0) return getAllCourses();
        return courses.stream()
                .filter(c -> c.getSemester() == semester)
                .collect(Collectors.toList());
    }

    public synchronized void enrollStudent(String courseCode, String studentId) {
        Course c = getCourseByCode(courseCode);
        if (c != null && studentId != null) {
            c.enrollStudent(studentId);
            storage.saveCourses(courses);
        }
    }

    public synchronized void removeStudentFromCourse(String courseCode, String studentId) {
        Course c = getCourseByCode(courseCode);
        if (c != null && studentId != null) {
            c.removeStudent(studentId);
            storage.saveCourses(courses);
        }
    }

    public synchronized void assignTeacher(String courseCode, String teacherId) {
        Course c = getCourseByCode(courseCode);
        if (c != null) {
            c.setTeacherId(teacherId);
            storage.saveCourses(courses);
        }
    }
}

