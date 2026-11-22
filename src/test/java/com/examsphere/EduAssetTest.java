package com.examsphere;

import com.examsphere.exception.*;
import com.examsphere.model.*;
import com.examsphere.service.*;
import com.examsphere.storage.FileStorageService;
import com.examsphere.util.*;

import java.util.*;

/**
 * Automated test suite for EduAsset — verifying core business rules,
 * OOP inheritance and polymorphism, scheduling conflict detection, marks boundary validation,
 * result calculation, security authorization, and object serialization persistence.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class EduAssetTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("====================================================================");
        System.out.println("       EduAsset System Test Suite — Execution Starting              ");
        System.out.println("====================================================================");

        testAuthenticationAndRoles();
        testOOPInheritanceAndPolymorphism();
        testStudentCrudAndSearch();
        testCourseAndDepartmentManagement();
        testExamSchedulingConflicts();
        testRoomAndSeatAllocationLimits();
        testAttendanceTrackingAndDuplicatePrevention();
        testMarksValidationAndBoundaryChecks();
        testResultCalculationAndGPA();
        testResultPublicationAuthorization();
        testApplicationLifecycleAndApproval();
        testSerializationPersistence();
        testThemeManagerSwitching();

        System.out.println("====================================================================");
        System.out.printf("Test Suite Results: %d / %d Tests Passed (%.1f%% Success Rate)%n",
                testsPassed, testsRun, (testsPassed * 100.0 / testsRun));
        System.out.println("====================================================================");

        if (testsPassed != testsRun) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.printf("  [PASS] %s%n", testName);
        } else {
            System.err.printf("  [FAIL] %s%n", testName);
        }
    }

    // ─── 1. Authentication & Roles ──────────────────────────────────────
    private static void testAuthenticationAndRoles() {
        System.out.println("\n--- 1. Testing Authentication and Role Permissions ---");
        AuthService auth = AuthService.getInstance();

        try {
            User admin = auth.login("ADMIN001", "admin123");
            assertTrue("Admin login succeeds", admin != null && "ADMIN".equals(admin.getRole()));
            assertTrue("Admin has permission to PUBLISH_RESULTS", auth.hasPermission("PUBLISH_RESULTS"));

            auth.login("STU001", "student123");
            assertTrue("Student login succeeds", auth.isLoggedIn() && "STUDENT".equals(auth.getCurrentUser().getRole()));
            assertTrue("Student cannot PUBLISH_RESULTS", !auth.hasPermission("PUBLISH_RESULTS"));
            assertTrue("Student can SUBMIT_APPLICATION", auth.hasPermission("SUBMIT_APPLICATION"));
        } catch (InvalidLoginException e) {
            assertTrue("Valid login threw unexpected exception: " + e.getMessage(), false);
        }

        try {
            auth.login("ADMIN001", "wrong_password");
            assertTrue("Invalid password should throw InvalidLoginException", false);
        } catch (InvalidLoginException e) {
            assertTrue("Invalid password correctly rejected with InvalidLoginException", true);
        }
    }

    // ─── 2. OOP Inheritance & Polymorphism ──────────────────────────────
    private static void testOOPInheritanceAndPolymorphism() {
        System.out.println("\n--- 2. Testing OOP Inheritance, Polymorphism & Abstraction ---");
        User[] users = new User[]{
                new Admin("ADM-99", "Admin Test", "adm@test.com", "hash", "Controller"),
                new Teacher("TCH-99", "Prof Test", "tch@test.com", "hash", "CSE", "Lecturer"),
                new Student("STU-99", "Student Test", "stu@test.com", "hash", "CSE", "B.Sc.", 1, "017")
        };

        boolean allPolymorphic = true;
        for (User u : users) {
            if (u.getDashboardDescription() == null || u.getDisplayRole() == null) {
                allPolymorphic = false;
            }
        }
        assertTrue("Polymorphic call to abstract methods across User hierarchy", allPolymorphic);
        assertTrue("Admin display role matches", "Examination Controller".equals(users[0].getDisplayRole()));
        assertTrue("Teacher display role matches", "Teacher / Invigilator".equals(users[1].getDisplayRole()));
        assertTrue("Student display role matches", "Student".equals(users[2].getDisplayRole()));
    }

    // ─── 3. Student CRUD & Search ───────────────────────────────────────
    private static void testStudentCrudAndSearch() {
        System.out.println("\n--- 3. Testing Student CRUD and Filter Capabilities ---");
        StudentService ss = StudentService.getInstance();

        Student s = new Student("STU999", "Testing Student", "test.stu@examsphere.diu.edu.bd",
                PasswordUtil.hashPassword("pass123"), "CSE", "B.Sc. in CSE", 3, "+8801799999999");
        try {
            ss.addStudent(s);
            assertTrue("Student record successfully created", ss.getStudentById("STU999") != null);
        } catch (DuplicateStudentIdException e) {
            assertTrue("Existing student STU999 retrieved", ss.getStudentById("STU999") != null);
        }

        List<Student> searchResult = ss.searchStudents("Testing Student");
        assertTrue("Search by student name returns correct record", !searchResult.isEmpty());

        List<Student> cseStudents = ss.filterByDepartment("CSE");
        assertTrue("Filter by CSE department returns students", !cseStudents.isEmpty());
    }

    // ─── 4. Course & Department Management ──────────────────────────────
    private static void testCourseAndDepartmentManagement() {
        System.out.println("\n--- 4. Testing Course and Department Operations ---");
        CourseService cs = CourseService.getInstance();

        Course c = new Course("CSE311", "Database Systems", "CSE", 3, 5);
        cs.addCourse(c);
        assertTrue("Course added and retrieved", cs.getCourseByCode("CSE311") != null);

        cs.enrollStudent("CSE311", "STU001");
        Course updated = cs.getCourseByCode("CSE311");
        assertTrue("Student enrollment registered in course", updated.getEnrolledStudentIds().contains("STU001"));
    }

    // ─── 5. Exam Scheduling Conflict Detection ──────────────────────────
    private static void testExamSchedulingConflicts() {
        System.out.println("\n--- 5. Testing Examination Scheduling Conflict Engine ---");
        ExamService es = ExamService.getInstance();

        Exam e1 = new Exam("EX-CONFLICT-1", "CSE221", "MIDTERM", 5, "2026-12-10", "10:00", "12:00");
        e1.setRoomId("KT-320");
        e1.setInvigilatorId("TCH001");
        try {
            es.createExam(e1);
            assertTrue("Primary exam slot created successfully", true);
        } catch (ScheduleConflictException e) {
            assertTrue("Primary slot already present", true);
        }

        // Room conflict test: same room, overlapping time
        Exam eConflictRoom = new Exam("EX-CONFLICT-2", "CSE222", "MIDTERM", 5, "2026-12-10", "11:00", "13:00");
        eConflictRoom.setRoomId("KT-320");
        eConflictRoom.setInvigilatorId("TCH_OTHER");
        try {
            es.createExam(eConflictRoom);
            assertTrue("Room conflict should have been blocked", false);
        } catch (ScheduleConflictException e) {
            assertTrue("Room conflict correctly caught and prevented: " + e.getMessage(), true);
        }

        // Invigilator conflict test: same teacher, different room, overlapping time
        Exam eConflictInvigilator = new Exam("EX-CONFLICT-3", "CSE223", "MIDTERM", 5, "2026-12-10", "10:30", "12:30");
        eConflictInvigilator.setRoomId("KT-999");
        eConflictInvigilator.setInvigilatorId("TCH001");
        try {
            es.createExam(eConflictInvigilator);
            assertTrue("Invigilator conflict should have been blocked", false);
        } catch (ScheduleConflictException e) {
            assertTrue("Invigilator conflict correctly caught and prevented: " + e.getMessage(), true);
        }

        // Invalid time order test: start >= end
        Exam eInvalidTime = new Exam("EX-INVALID-TIME", "CSE221", "MIDTERM", 5, "2026-12-10", "14:00", "10:00");
        try {
            es.createExam(eInvalidTime);
            assertTrue("Invalid time order should have been blocked", false);
        } catch (ScheduleConflictException e) {
            assertTrue("Invalid time order correctly rejected", true);
        }
    }

    // ─── 6. Room & Seat Allocation Limits ───────────────────────────────
    private static void testRoomAndSeatAllocationLimits() {
        System.out.println("\n--- 6. Testing Room Capacity Limits and Seat Allocation ---");
        SeatAllocationService sas = SeatAllocationService.getInstance();

        ExamRoom room = new ExamRoom("ROOM-SMALL", "Main Campus", 2);
        sas.addRoom(room);

        List<String> students = Arrays.asList("STU001", "STU002", "STU003", "STU004");
        sas.allocateSeats("EX-CAP-TEST", "ROOM-SMALL", students);

        List<SeatAssignment> allocated = sas.getSeatsByExam("EX-CAP-TEST");
        assertTrue("Seat allocation strictly capped to room capacity (2)", allocated.size() == 2);
        int available = sas.getAvailableSeats("ROOM-SMALL", "EX-CAP-TEST");
        assertTrue("Available seats correctly calculated as 0", available == 0);
    }

    // ─── 7. Attendance Tracking & Duplicate Prevention ──────────────────
    private static void testAttendanceTrackingAndDuplicatePrevention() {
        System.out.println("\n--- 7. Testing Attendance Tracking and Duplicate Prevention ---");
        AttendanceService as = AttendanceService.getInstance();

        AttendanceRecord r1 = new AttendanceRecord("ATT-T1", "EX-ATT-1", "STU001", "PRESENT");
        as.markAttendance(r1);

        // Update with another status for same student + exam
        AttendanceRecord r2 = new AttendanceRecord("ATT-T2", "EX-ATT-1", "STU001", "LATE");
        as.markAttendance(r2);

        List<AttendanceRecord> list = as.getAttendanceByExam("EX-ATT-1");
        long stu1Count = list.stream().filter(r -> "STU001".equals(r.getStudentId())).count();
        assertTrue("Attendance records for same student & exam updated without duplication", stu1Count == 1);
        AttendanceRecord updated = as.getAttendance("EX-ATT-1", "STU001");
        assertTrue("Status updated to LATE", updated != null && "LATE".equals(updated.getStatus()));
    }

    // ─── 8. Marks Validation & Boundary Checks ──────────────────────────
    private static void testMarksValidationAndBoundaryChecks() {
        System.out.println("\n--- 8. Testing Marks Validation and Boundary Checks ---");
        MarksService ms = MarksService.getInstance();

        MarkRecord valid = new MarkRecord("MRK-V", "CSE221", "STU001");
        valid.setClassTest(18.0);
        valid.setAssignment(9.0);
        valid.setMidterm(26.0);
        valid.setFinalExam(35.0);
        try {
            ms.enterMarks(valid);
            assertTrue("Valid marks entered successfully", true);
            assertTrue("Total marks correctly computed (88.0)", valid.getTotalMarks() == 88.0);
            assertTrue("Grade correctly derived as A+", "A+".equals(valid.getGrade()));
        } catch (InvalidMarksException e) {
            assertTrue("Valid marks failed: " + e.getMessage(), false);
        }

        // Test boundary violation: Class Test > 20
        MarkRecord invalid = new MarkRecord("MRK-INV", "CSE221", "STU002");
        invalid.setClassTest(25.0); // Exceeds 20
        try {
            ms.enterMarks(invalid);
            assertTrue("Exceeded marks should have been rejected", false);
        } catch (InvalidMarksException e) {
            assertTrue("Exceeded marks correctly rejected with InvalidMarksException: " + e.getMessage(), true);
        }
    }

    // ─── 9. Result Calculation & GPA ────────────────────────────────────
    private static void testResultCalculationAndGPA() {
        System.out.println("\n--- 9. Testing Result Processing & Weighted GPA Computation ---");
        Result res = new Result("RES-TEST", "STU001", 5);
        res.addCourseResult("CSE221", "A+", 4.00, 3); // 12 points
        res.addCourseResult("CSE222", "A", 3.75, 1);   // 3.75 points
        // Total points = 15.75 / 4 credits = 3.9375 -> rounded to 3.94

        double gpa = res.calculateGPA();
        assertTrue("GPA calculated accurately to 2 decimal places (3.94)", Math.abs(gpa - 3.94) < 0.01);
    }

    // ─── 10. Result Publication Authorization ───────────────────────────
    private static void testResultPublicationAuthorization() {
        System.out.println("\n--- 10. Testing Result Publication Authorization Workflow ---");
        ResultService rs = ResultService.getInstance();

        // Non-admin user attempting publication
        try {
            rs.publishResults(5, "STU001");
            assertTrue("Student must not be allowed to publish results", false);
        } catch (AuthorizationException e) {
            assertTrue("Unauthorized publication correctly blocked: " + e.getMessage(), true);
        }

        // Admin publishing
        try {
            rs.publishResults(5, "ADMIN001");
            assertTrue("Admin authorized to publish results", true);
        } catch (AuthorizationException e) {
            assertTrue("Admin publication failed unexpectedly: " + e.getMessage(), false);
        }
    }

    // ─── 11. Application Lifecycle & Approval ───────────────────────────
    private static void testApplicationLifecycleAndApproval() {
        System.out.println("\n--- 11. Testing Application Lifecycle and Approvable Interface ---");
        ApplicationService as = ApplicationService.getInstance();

        ApplicationRequest app = new ApplicationRequest("APP-T1", "STU001", "RECHECK", "CSE221", "Please recheck answer 4");
        try {
            as.submitApplication(app);
            assertTrue("Application submitted in PENDING state", "PENDING".equals(app.getStatus()));
        } catch (InvalidApplicationException e) {
            assertTrue("Valid application submission failed", false);
        }

        // Test Approvable interface polymorphism
        app.approve("Marks increased by 2");
        assertTrue("Approvable.approve() transitions status to APPROVED", "APPROVED".equals(app.getApprovalStatus()));
        assertTrue("Admin comments recorded on approval", "Marks increased by 2".equals(app.getAdminComments()));
    }

    // ─── 12. Serialization Persistence ──────────────────────────────────
    private static void testSerializationPersistence() {
        System.out.println("\n--- 12. Testing File I/O and Java Object Serialization ---");
        FileStorageService storage = FileStorageService.getInstance();

        List<Department> testList = new ArrayList<>();
        testList.add(new Department("ARCH", "Architecture"));
        testList.add(new Department("PHARM", "Pharmacy"));

        storage.saveList(testList, "test_depts.dat");
        List<Department> reloaded = storage.loadList("test_depts.dat");

        assertTrue("Deserialized list contains same item count (2)", reloaded.size() == 2);
        assertTrue("Deserialized department details match original",
                "ARCH".equals(reloaded.get(0).getDepartmentId()) && "Pharmacy".equals(reloaded.get(1).getDepartmentName()));
    }

    // ─── 13. ThemeManager Switching ─────────────────────────────────────
    private static void testThemeManagerSwitching() {
        System.out.println("\n--- 13. Testing ThemeManager Palette Toggling ---");
        ThemeManager tm = ThemeManager.getInstance();

        boolean initial = tm.isDarkTheme();
        tm.toggleTheme();
        assertTrue("Theme state toggled", tm.isDarkTheme() != initial);

        // Verify colors are non-null in both modes
        assertTrue("Background color is valid", tm.getBackground() != null);
        assertTrue("Primary accent color is valid", tm.getPrimaryAccent() != null);

        // Restore to light mode
        tm.setDarkTheme(false);
        assertTrue("Restored to light theme", !tm.isDarkTheme());
    }
}

