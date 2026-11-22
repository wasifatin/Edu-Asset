# Internal Java API Reference — EduAsset

## University Examination & Academic Assessment Management System

> **Note:** EduAsset is a native Java desktop application. The following reference documents the internal Java class methods, parameters, return types, interface contracts, and exception signatures rather than HTTP web endpoints.

---

## 1. Service Layer API (`com.examsphere.service`)

### 1.1 `AuthService`
Singleton service managing user session state, authentication, role authorization, and account provisioning.

```java
public static synchronized AuthService getInstance()
```
Returns the global singleton instance.

```java
public synchronized User login(String userId, String password) throws InvalidLoginException
```
- **Parameters:** `userId` (String), `password` (String)
- **Returns:** Authenticated `User` instance.
- **Throws:** `InvalidLoginException` if credentials fail, user is missing, or account is deactivated.

```java
public synchronized void logout()
```
Clears session context and resets active user.

```java
public synchronized User getCurrentUser()
```
- **Returns:** Currently authenticated `User` object, or `null`.

```java
public synchronized boolean hasPermission(String action)
```
- **Parameters:** `action` (String) identifier (e.g. `"PUBLISH_RESULTS"`, `"ENTER_MARKS"`)
- **Returns:** `true` if current user role permits action, `false` otherwise.

```java
public synchronized void createUser(User user) throws DuplicateStudentIdException
```
- **Parameters:** `user` (`User` instance)
- **Throws:** `DuplicateStudentIdException` if ID already exists.

```java
public synchronized void updateUser(User user)
```
Updates user attributes in persistent storage and synchronizes active session if updated.

---

### 1.2 `StudentService`
Singleton service governing student profile CRUD operations and searches.

```java
public static synchronized StudentService getInstance()
```
```java
public synchronized void addStudent(Student student) throws DuplicateStudentIdException
```
- **Parameters:** `student` (`Student`)
- **Throws:** `DuplicateStudentIdException` if ID matches an existing account.

```java
public synchronized void updateStudent(Student student)
```
```java
public synchronized void deleteStudent(String studentId)
```
Soft-deactivates the student account (`active = false`).

```java
public synchronized Student getStudentById(String studentId)
```
- **Returns:** `Student` instance or `null`.

```java
public synchronized List<Student> searchStudents(String query)
```
- **Parameters:** `query` (String)
- **Returns:** List of students matching ID, name, email, or department.

```java
public synchronized List<Student> filterByDepartment(String departmentId)
```
- **Parameters:** `departmentId` (e.g. `"CSE"`)

```java
public synchronized List<Student> filterBySemester(int semester)
```
- **Parameters:** `semester` (int between 1 and 12)

---

### 1.3 `CourseService`
Singleton managing departments, degree programs, curriculum courses, and student enrollments.

```java
public static synchronized CourseService getInstance()
```
```java
public synchronized void addCourse(Course course)
```
```java
public synchronized void updateCourse(Course course)
```
```java
public synchronized void deleteCourse(String courseCode)
```
Removes course from system and cleans up departmental listings.

```java
public synchronized Course getCourseByCode(String courseCode)
```
```java
public synchronized List<Course> getCoursesByDepartment(String departmentId)
```
```java
public synchronized void enrollStudent(String courseCode, String studentId)
```
Registers student ID into course enrollment list.

```java
public synchronized void assignTeacher(String courseCode, String teacherId)
```
Assigns faculty member as the instructor for the course.

```java
public synchronized void addDepartment(Department dept)
```
```java
public synchronized Department getDepartmentById(String deptId)
```

---

### 1.4 `ExamService`
Singleton managing examination timetable slots and automated schedule conflict detection.

```java
public static synchronized ExamService getInstance()
```
```java
public synchronized void createExam(Exam exam) throws ScheduleConflictException
```
- **Parameters:** `exam` (`Exam`)
- **Throws:** `ScheduleConflictException` on:
  - Duplicate Exam ID
  - Date format error
  - Time ordering violation (`startTime >= endTime`)
  - Room double-booking on same date and overlapping time
  - Invigilator double-booking on same date and overlapping time

```java
public synchronized void updateExam(Exam exam) throws ScheduleConflictException
```
Updates slot with conflict detection excluding self.

```java
public synchronized void cancelExam(String examId)
```
Sets exam status to `"CANCELLED"`.

```java
public synchronized boolean checkRoomConflict(String roomId, String date, String startTime, String endTime, String excludeExamId)
```
- **Returns:** `true` if room is occupied by another non-cancelled exam during specified interval.

```java
public synchronized boolean checkInvigilatorConflict(String invigilatorId, String date, String startTime, String endTime, String excludeExamId)
```
- **Returns:** `true` if invigilator is assigned to another overlapping exam.

---

### 1.5 `SeatAllocationService`
Singleton managing room capacities and automated student seating layouts.

```java
public static synchronized SeatAllocationService getInstance()
```
```java
public synchronized void addRoom(ExamRoom room)
```
```java
public synchronized void allocateSeats(String examId, String roomId, List<String> studentIds)
```
Allocates examinees up to room capacity and generates sequential seat assignments (`Seat 1..N`).

```java
public synchronized List<SeatAssignment> getSeatsByExam(String examId)
```
```java
public synchronized int getAvailableSeats(String roomId, String examId)
```
- **Returns:** Number of unassigned seats remaining in hall for the exam.

---

### 1.6 `AttendanceService`
Singleton tracking examination hall attendance roll-calls.

```java
public static synchronized AttendanceService getInstance()
```
```java
public synchronized void markAttendance(AttendanceRecord record)
```
Records or updates attendance for examinee; prevents duplicate records for same exam and student.

```java
public synchronized AttendanceRecord getAttendance(String examId, String studentId)
```
```java
public synchronized Map<String, Integer> getAttendanceStats(String examId)
```
- **Returns:** Map containing `"PRESENT"`, `"ABSENT"`, and `"LATE"` counts.

---

### 1.7 `MarksService`
Singleton governing continuous assessment scores, boundary validation, and grade derivations.

```java
public static synchronized MarksService getInstance()
```
```java
public void validateMarks(MarkRecord record) throws InvalidMarksException
```
- **Throws:** `InvalidMarksException` if any score component is negative or exceeds component maximum:
  - Class Test: `0.0 <= mark <= 20.0`
  - Assignment: `0.0 <= mark <= 10.0`
  - Midterm: `0.0 <= mark <= 30.0`
  - Final Exam: `0.0 <= mark <= 40.0`

```java
public synchronized void enterMarks(MarkRecord record) throws InvalidMarksException
```
Validates marks, computes total, derives letter grade, and updates persistent storage.

```java
public synchronized MarkRecord getMarks(String courseCode, String studentId)
```

---

### 1.8 `ResultService`
Singleton calculating semester GPAs, maintaining publication status, and enforcing controller release gates.

```java
public static synchronized ResultService getInstance()
```
```java
public synchronized void calculateResults(String courseCode, int semester)
```
Computes credit-weighted GPAs for all enrolled candidates in a semester batch.

```java
public synchronized void publishResults(int semester, String adminUserId) throws AuthorizationException
```
- **Throws:** `AuthorizationException` if user is not an `ADMIN` (Examination Controller).
Marks results as official and records release timestamp.

```java
public synchronized List<Result> getPublishedResults()
```
```java
public synchronized List<Result> getUnpublishedResults()
```
```java
public synchronized Map<String, Object> getResultStats(int semester)
```
- **Returns:** Aggregated metrics: `total`, `passCount`, `failCount`, `passRate`, `failRate`, `avgGPA`.

---

### 1.9 `ApplicationService`
Singleton managing student recheck, retake, and grievance workflows.

```java
public static synchronized ApplicationService getInstance()
```
```java
public synchronized void submitApplication(ApplicationRequest app) throws InvalidApplicationException
```
- **Throws:** `InvalidApplicationException` if mandatory fields are missing.

```java
public synchronized void updateApplicationStatus(String appId, String status, String comments) throws AuthorizationException
```
Transitions status (`PENDING` → `UNDER_REVIEW` → `APPROVED`/`REJECTED` → `COMPLETED`) with feedback remarks.

---

### 1.10 `ReportService`
Singleton compiling institutional metrics, performance benchmarks, and audit reports.

```java
public static synchronized ReportService getInstance()
```
```java
public int getTotalStudents()
public int getTotalExams()
public int getScheduledExams()
public int getPendingResults()
public int getPublishedResults()
public int getPendingApplications()
public double getAttendancePercentage()
public double getPassPercentage()
public double getFailPercentage()
public Map<String, Double> getDepartmentPerformance()
public Map<String, Double> getCoursePerformance()
public String generateSummaryReport()
```

---

## 2. Model Layer API (`com.examsphere.model`)

All model entities implement `java.io.Serializable` with explicit `serialVersionUID`.

| Model Class | Hierarchy & Interfaces | Key Methods |
|---|---|---|
| `User` | `abstract`, implements `Serializable`, `Searchable` | `getDashboardDescription()`, `getDisplayRole()`, `matchesSearch(String)` |
| `Student` | `extends User` | `getDepartment()`, `getProgram()`, `getSemester()`, `getContactNumber()` |
| `Teacher` | `extends User` | `getDepartment()`, `getDesignation()`, `addAssignedCourse(String)` |
| `Admin` | `extends User` | `getDesignation()` |
| `DepartmentCoordinator` | `extends User` | `getDepartment()` |
| `Course` | implements `Serializable` | `getCourseCode()`, `getCredits()`, `enrollStudent(String)` |
| `Exam` | implements `Serializable` | `getExamId()`, `getStartTime()`, `getEndTime()`, `getStatus()` |
| `ExamRoom` | implements `Serializable` | `getRoomId()`, `getCapacity()`, `isAvailable()` |
| `SeatAssignment` | implements `Serializable` | `getSeatNumber()`, `getExamId()`, `getStudentId()` |
| `AttendanceRecord` | implements `Serializable` | `getStatus()`, `getTimestamp()`, `getRemarks()` |
| `MarkRecord` | implements `Serializable` | `calculateTotal()`, `calculateGrade()`, `getTotalMarks()` |
| `Result` | implements `Serializable` | `calculateGPA()`, `isPublished()`, `addCourseResult()` |
| `ApplicationRequest`| implements `Serializable`, `Approvable` | `approve(String)`, `reject(String)`, `getApprovalStatus()` |

---

## 3. Storage Layer API (`com.examsphere.storage`)

### `FileStorageService`
```java
public static synchronized FileStorageService getInstance()
public synchronized <T> void saveList(List<T> list, String filename)
public synchronized <T> List<T> loadList(String filename)
public synchronized boolean backupData(String backupDir)
public void initializeDataDirectory()
```
Saves and restores binary collections from `./data/<filename>.dat` using `ObjectOutputStream` and `ObjectInputStream`.

