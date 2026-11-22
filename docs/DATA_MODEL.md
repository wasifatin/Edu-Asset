# Data Model & Storage Schema Specification — EduAsset

## University Examination & Academic Assessment Management System

---

## 1. Storage Architecture Overview

EduAsset employs native **Java Object Serialization** to persist domain state to binary files (`.dat`) within the `./data/` directory. Each entity class defines an explicit `private static final long serialVersionUID` to ensure binary compatibility across application lifecycles.

| Entity Class | Persistent File | serialVersionUID | Primary Key | Description |
|---|---|---|---|---|
| `User` (and subclasses) | `users.dat` | `1L` (`2L`-`5L`) | `userId` | Accounts, credentials & roles |
| `Department` | `departments.dat` | `6L` | `departmentId` | Academic departments & degrees |
| `Course` | `courses.dat` | `7L` | `courseCode` | Curricular subjects & enrollments |
| `Exam` | `exams.dat` | `8L` | `examId` | Examination sessions & slots |
| `ExamRoom` | `rooms.dat` | `9L` | `roomId` | Examination halls & capacities |
| `SeatAssignment` | `seat_assignments.dat`| `10L` | `seatId` | Physical seat numbers per hall |
| `AttendanceRecord` | `attendance.dat` | `11L` | `attendanceId` | Examinee roll-call statuses |
| `MarkRecord` | `marks.dat` | `12L` | `markId` | Multi-component assessment marks |
| `Result` | `results.dat` | `13L` | `resultId` | Semester Term GPAs & CGPAs |
| `ApplicationRequest` | `applications.dat` | `14L` | `applicationId` | Rechecks, retakes & grievances |

---

## 2. Entity Attribute Definitions & Constraints

### 2.1 `User` (Abstract Base) & Subclasses
- **File:** `users.dat`
- **Fields:**
  - `userId` (String): Unique identifier, alphanumeric (e.g. `ADMIN001`, `STU001`). Mandatory.
  - `fullName` (String): Display name of user. Mandatory.
  - `email` (String): Institutional email format `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$`. Mandatory.
  - `passwordHash` (String): 64-char hex SHA-256 salted hash.
  - `role` (String): `ADMIN`, `COORDINATOR`, `TEACHER`, `STUDENT`.
  - `active` (boolean): `true` if account is enabled.
  - `createdDate` (String): ISO date `YYYY-MM-DD`.
- **Subclass Fields:**
  - `Student`: `department` (String), `program` (String), `semester` (int: 1–12), `contactNumber` (String).
  - `Teacher`: `department` (String), `designation` (String), `assignedCourseIds` (List<String>).
  - `Admin`: `designation` (String, e.g. "Examination Controller").
  - `DepartmentCoordinator`: `department` (String).

### 2.2 `Department`
- **File:** `departments.dat`
- **Fields:**
  - `departmentId` (String): Alphanumeric code (e.g. `CSE`, `SWE`, `EEE`). Primary Key.
  - `departmentName` (String): Full official title.
  - `headOfDepartment` (String): Name of current department head.
  - `programs` (List<String>): List of offered degree titles (e.g. `["B.Sc. in CSE", "M.Sc. in CSE"]`).
  - `courseIds` (List<String>): List of course codes under department catalog.

### 2.3 `Course`
- **File:** `courses.dat`
- **Fields:**
  - `courseCode` (String): Unique course identifier (e.g. `CSE221`). Primary Key.
  - `courseName` (String): Descriptive course title.
  - `departmentId` (String): Foreign reference to `Department.departmentId`.
  - `credits` (int): Academic credit units, range: $1 \le \text{credits} \le 4$.
  - `semester` (int): Recommended academic semester, range: $1 \le \text{semester} \le 12$.
  - `teacherId` (String): Assigned faculty member ID.
  - `enrolledStudentIds` (List<String>): List of registered student IDs.

### 2.4 `Exam`
- **File:** `exams.dat`
- **Fields:**
  - `examId` (String): Unique code (e.g. `EX-2026-001`). Primary Key.
  - `courseCode` (String): Evaluated course.
  - `examType` (String): Category: `MIDTERM`, `FINAL`, `CLASS_TEST`, `QUIZ`.
  - `semester` (int): Target academic semester.
  - `examDate` (String): Date format `YYYY-MM-DD`.
  - `startTime` (String): 24-hr time `HH:mm`.
  - `endTime` (String): 24-hr time `HH:mm`. Constraint: `startTime < endTime`.
  - `roomId` (String): Assigned venue hall.
  - `invigilatorId` (String): Assigned faculty invigilator.
  - `status` (String): Lifecycle state: `SCHEDULED`, `ONGOING`, `COMPLETED`, `CANCELLED`.

### 2.5 `ExamRoom`
- **File:** `rooms.dat`
- **Fields:**
  - `roomId` (String): Unique room code (e.g. `KT-320`). Primary Key.
  - `building` (String): Campus building location.
  - `capacity` (int): Maximum student desks, $10 \le \text{capacity} \le 200$.
  - `available` (boolean): Operational status (`true` for operational, `false` for maintenance).
  - `facilities` (List<String>): Amenities (e.g. `Air Conditioned`, `CCTV Surveillance`).

### 2.6 `SeatAssignment`
- **File:** `seat_assignments.dat`
- **Fields:**
  - `seatId` (String): Unique allocation code (e.g. `SA-EX-2026-001-1`). Primary Key.
  - `examId` (String): Associated exam session.
  - `roomId` (String): Examination hall.
  - `studentId` (String): Allocated examinee.
  - `seatNumber` (int): Sequential seat index ($1 \le \text{seatNumber} \le \text{capacity}$).

### 2.7 `AttendanceRecord`
- **File:** `attendance.dat`
- **Fields:**
  - `attendanceId` (String): Unique identifier. Primary Key.
  - `examId` (String): Associated exam session.
  - `studentId` (String): Associated examinee.
  - `status` (String): `PRESENT`, `ABSENT`, `LATE`.
  - `remarks` (String): Observational notes by invigilator.
  - `timestamp` (String): ISO timestamp when status was updated.

### 2.8 `MarkRecord`
- **File:** `marks.dat`
- **Fields:**
  - `markId` (String): Unique identifier. Primary Key.
  - `courseCode` (String): Evaluated subject.
  - `studentId` (String): Associated candidate.
  - `classTest` (double): $0.0 \le \text{score} \le 20.0$.
  - `assignment` (double): $0.0 \le \text{score} \le 10.0$.
  - `midterm` (double): $0.0 \le \text{score} \le 30.0$.
  - `finalExam` (double): $0.0 \le \text{score} \le 40.0$.
  - `totalMarks` (double): $0.0 \le \text{score} \le 100.0$.
  - `grade` (String): Derived letter grade (`A+`, `A`, `A-`, `B+`, `B`, `B-`, `C+`, `C`, `D`, `F`).

### 2.9 `Result`
- **File:** `results.dat`
- **Fields:**
  - `resultId` (String): Unique identifier (e.g. `RES-2026-001`). Primary Key.
  - `studentId` (String): Evaluated student.
  - `semester` (int): Completed term.
  - `gpa` (double): Term Grade Point Average ($0.00 \le \text{gpa} \le 4.00$).
  - `cgpa` (double): Cumulative Grade Point Average.
  - `published` (boolean): `true` if officially released by Controller.
  - `publishedBy` (String): Administrator ID who authorized release.
  - `publishDate` (String): Date of publication.
  - `courseGrades` (Map<String, String>): Course code to letter grade.
  - `courseGradePoints` (Map<String, Double>): Course code to numeric grade point.
  - `courseCredits` (Map<String, Integer>): Course code to credit units.

### 2.10 `ApplicationRequest`
- **File:** `applications.dat`
- **Fields:**
  - `applicationId` (String): Unique grievance code (e.g. `APP-2026-001`). Primary Key.
  - `studentId` (String): Lodging candidate.
  - `applicationType` (String): `RECHECK`, `RETAKE`, `MISSING_MARKS`, `COMPLAINT`.
  - `courseCode` (String): Associated subject.
  - `description` (String): Explanatory statement.
  - `status` (String): `PENDING`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, `COMPLETED`.
  - `adminComments` (String): Administrative remarks and resolution notes.
  - `submissionDate` (String): ISO date of lodgment.
  - `lastUpdated` (String): ISO date of latest review action.

---

## 3. Sample Demonstration Records

```json
// Sample Serialized User Record (Student)
{
  "userId": "STU001",
  "fullName": "Aminul Islam",
  "email": "aminul.stu@examsphere.diu.edu.bd",
  "role": "STUDENT",
  "department": "CSE",
  "program": "B.Sc. in CSE",
  "semester": 5,
  "contactNumber": "+8801711223344",
  "active": true
}

// Sample Serialized Exam Record
{
  "examId": "EX-2026-001",
  "courseCode": "CSE221",
  "examType": "MIDTERM",
  "semester": 5,
  "examDate": "2026-11-20",
  "startTime": "10:00",
  "endTime": "12:00",
  "roomId": "KT-320",
  "invigilatorId": "TCH001",
  "status": "SCHEDULED"
}

// Sample Serialized Mark Record
{
  "markId": "MRK-001",
  "courseCode": "CSE221",
  "studentId": "STU001",
  "classTest": 18.0,
  "assignment": 9.5,
  "midterm": 27.0,
  "finalExam": 36.0,
  "totalMarks": 90.5,
  "grade": "A+"
}
```

