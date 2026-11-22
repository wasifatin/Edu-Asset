# Product Requirements Document (PRD) — EduAsset

## University Examination & Academic Assessment Management System

---

## 1. Product Vision

To establish an intuitive, conflict-free, and mathematically rigorous desktop examination administration system that replicates the real-world operational workflows of a university Examination Controller's Office. EduAsset automates curriculum tracking, examination venue allocation, invigilator duty scheduling, continuous assessment recording, GPA/CGPA computation, confidential result publication, and student grievance arbitration through native Java Swing.

---

## 2. Problem Definition & Market Opportunity

Higher educational institutions face critical operational risks during semester examinations:
1. **Venue Double-Booking:** Simultaneous assignment of different courses or programs to the same hall.
2. **Faculty Duty Clashes:** Scheduling invigilators for two exams occurring at overlapping times.
3. **Physical Capacity Overflows:** Assigning more students to an examination room than its physical seating capacity supports.
4. **Grading Vulnerabilities:** Human error during manual marks transcription resulting in scores higher than the maximum allowed component mark.
5. **Unauthorized Disclosure:** Early or accidental exposure of semester results before official review and executive controller approval.
6. **Unstructured Grievances:** Lost or untracked student recheck and retake applications across paper forms.

EduAsset solves these problems through an automated desktop architecture with built-in conflict detection, boundary validation, and role-based permissions.

---

## 3. Target Users & Stakeholders

| Stakeholder Group | Primary Goals & Responsibilities |
|---|---|
| **Examination Controller (Admin)** | Final authority on timetable approval, result publication, user access provisioning, and institutional audit reporting. |
| **Department Coordinator** | Curricular oversight, course credit assignments, faculty instructor allocation, and departmental student record administration. |
| **Teachers & Invigilators** | Assessment marks entry, examination hall invigilation, roll-call attendance verification, and student conduct logging. |
| **Students** | Examination timetable inspection, seating location verification, published transcript review, and academic grievance lodging. |
| **Academic Council** | Review of institutional pass/fail metrics, department performance benchmarks, and degree completion thresholds. |

---

## 4. User Roles & Permission Matrix

| Module / Action | Admin | Coordinator | Teacher | Student |
|---|:---:|:---:|:---:|:---:|
| **Login & Profile Display** | ✅ | ✅ | ✅ | ✅ |
| **Student Directory Management** | ✅ Full CRUD | ✅ View & Edit | ✅ View Only | ❌ |
| **Department & Course Management**| ✅ Full CRUD | ✅ Full CRUD | ✅ View Assigned | ❌ |
| **Examination Slot Scheduling** | ✅ Full CRUD | ✅ Full CRUD | ✅ View Assigned | ❌ View Published |
| **Room & Seating Allocation** | ✅ Full CRUD | ✅ Full CRUD | ✅ View Roster | ❌ View Own Seat |
| **Attendance Roll-Call Tracking** | ✅ Full CRUD | ✅ Full CRUD | ✅ Mark Assigned | ❌ |
| **Assessment Marks Entry** | ✅ Override | ✅ Verify | ✅ Input Assigned | ❌ View Published |
| **Calculate Semester GPA/CGPA** | ✅ Execute | ✅ Execute | ❌ | ❌ |
| **Publish Official Results** | ✅ Sole Authority | ❌ | ❌ | ❌ |
| **View Official Transcripts** | ✅ All | ✅ Department | ❌ | ✅ Own Only |
| **Submit Academic Grievances** | ❌ | ❌ | ❌ | ✅ Full Access |
| **Review & Approve Grievances** | ✅ Final Decision| ✅ Review | ❌ | ❌ |
| **System Analytics & Reports** | ✅ Full Access | ✅ Departmental| ❌ | ❌ |
| **User Account Provisioning** | ✅ Full Access | ❌ | ❌ | ❌ |

---

## 5. Functional Requirements by Module

### Module 1: Authentication and Session Control
- **FR-1.1:** System shall authenticate users via unique alphanumeric User ID and salted SHA-256 hashed password.
- **FR-1.2:** System shall enforce account status checks; deactivated accounts are denied entry.
- **FR-1.3:** System shall maintain session context in `AuthService` and display user role on top navigation bar.
- **FR-1.4:** Session termination (logout) clears credentials and returns the user to `LoginFrame`.

### Module 2: Student Record Administration
- **FR-2.1:** Administrative users can create, update, and soft-delete student profiles.
- **FR-2.2:** Real-time search filters by student identifier, full name, and email address.
- **FR-2.3:** Dropdown filtering by academic department (e.g. CSE, SWE, EEE) and semester (1–12).
- **FR-2.4:** Student deactivation requires explicit confirmation via modal dialog.

### Module 3: Department and Course Management
- **FR-3.1:** Support multi-department configuration with degree programs and assigned department heads.
- **FR-3.2:** Course catalog with course codes (e.g. CSE221), titles, credit hours (1–4), and semester mappings.
- **FR-3.3:** Faculty instructor assignment to courses.
- **FR-3.4:** Maintenance of student enrollments within courses.

### Module 4: Examination Scheduling & Conflict Prevention
- **FR-4.1:** Schedule exams by assigning course, exam category (Midterm, Final, Class Test, Quiz), date, time window, room, and invigilator.
- **FR-4.2:** Automated conflict detection preventing two exams in the same room on the same date with overlapping times (`[s1, e1] ∩ [s2, e2] ≠ ∅`).
- **FR-4.3:** Automated conflict detection preventing an invigilator from being assigned to overlapping exams.
- **FR-4.4:** Time validation enforcing start time strictly preceding end time.

### Module 5: Room and Seating Allocation
- **FR-5.1:** Venue configuration specifying building, room code, and physical seating capacity.
- **FR-5.2:** Auto-allocation engine allocating examinees to numbered seats (Seat 1..N).
- **FR-5.3:** Capacity overflow prevention: allocated students cannot exceed room capacity.
- **FR-5.4:** Real-time remaining seat capacity calculation.

### Module 6: Attendance Management
- **FR-6.1:** Generate roll-call roster of enrolled examinees for scheduled examination sessions.
- **FR-6.2:** Support status states: `PRESENT`, `ABSENT`, and `LATE`.
- **FR-6.3:** Enforce unique student attendance per exam to prevent duplicate records.
- **FR-6.4:** Live aggregation of present, absent, and late counts and percentages.

### Module 7: Continuous Assessment & Marks Registry
- **FR-7.1:** Support assessment components: Class Test (Max 20), Assignment (Max 10), Midterm (Max 30), Final Exam (Max 40).
- **FR-7.2:** Strict validation: `0 <= componentMark <= maxComponentMark`.
- **FR-7.3:** Automated total calculation (`ClassTest + Assignment + Midterm + Final`).
- **FR-7.4:** Automatic derivation of letter grades (A+, A, A-, B+, B, B-, C+, C, D, F) based on percentage.

### Module 8: Result Processing and Publication Gate
- **FR-8.1:** Compute semester Term GPA and cumulative CGPA based on credit-weighted grade points:
  $$\text{GPA} = \frac{\sum (\text{GradePoint}_i \times \text{Credits}_i)}{\sum \text{Credits}_i}$$
- **FR-8.2:** Result drafts remain unpublished and hidden from students until authorized.
- **FR-8.3:** Official publication requires Examination Controller (Admin) authorization.
- **FR-8.4:** Modal transcript dialog displaying subject grades, credit points, term GPA, and official status.

### Module 9: Student Applications and Grievances
- **FR-9.1:** Students can submit applications for Result Recheck, Exam Retake, Missing Marks, and General Grievances.
- **FR-9.2:** Workflow state progression: `PENDING` → `UNDER_REVIEW` → `APPROVED` / `REJECTED` → `COMPLETED`.
- **FR-9.3:** Controllers can record administrative feedback and rationale with decisions.

### Module 10: Reports and Institutional Analytics
- **FR-10.1:** Executive dashboard aggregating total students, exams, pass rate, and pending grievances.
- **FR-10.2:** Custom Java2D visual charts for pass/fail distribution and department CGPA benchmarks.
- **FR-10.3:** Asynchronous background file export using `SwingWorker` to prevent UI thread lockup.

### Module 11: Audit History and Logging
- **FR-11.1:** Central chronological audit timeline aggregating examination events, attendance updates, result releases, and grievance decisions.

---

## 6. Non-Functional Requirements

### 6.1 Performance & Responsiveness
- All UI transitions using `CardLayout` must execute under 50 ms.
- Table filtering and keyword search must update the visible view within 100 ms for up to 5,000 records.
- Background I/O and report file generation must run on worker threads without freezing the Swing Event Dispatch Thread (EDT).

### 6.2 Reliability & Fault Tolerance
- Corrupted or missing data files in `./data/` must not crash the application; system must handle exceptions gracefully and fall back to default demo seeds.
- Automated backup capabilities must copy data files to a designated backup location on command.

### 6.3 Security & Access Control
- Passwords must be protected using salted SHA-256 cryptographic hashing.
- Role-based authorization must be enforced in the service layer, not solely by hiding UI buttons.
- Result release must be gated behind administrative authorization checks.

### 6.4 Usability & Accessibility
- Interface must support both modern Light (`#E9EEF8` canvas) and Dark (`#050A18` canvas) themes with runtime palette toggling.
- High-contrast typography using Segoe UI font ensures legibility across desktop monitors.

---

## 7. Detailed Use Cases

### Use Case UC-01: Authenticate User
- **Primary Actor:** Any Registered User (Admin, Coordinator, Teacher, Student)
- **Precondition:** Application is running and `LoginFrame` is displayed.
- **Flow:**
  1. User enters User ID and Password.
  2. User clicks "Sign In to EduAsset".
  3. `AuthService.login()` hashes password with salt and validates against stored credentials.
  4. System verifies account `active` status.
  5. `LoginFrame` disposes and `MainFrame` initializes with role-specific navigation permissions.
- **Alternative Flow:** Invalid credentials display an alert dialog; password field is cleared.

### Use Case UC-02: Schedule Examination with Conflict Detection
- **Primary Actor:** Department Coordinator / Admin
- **Precondition:** User is authenticated and navigates to "Exam Scheduling".
- **Flow:**
  1. Actor clicks "+ Schedule Exam".
  2. Actor selects Course, Exam Type, Semester, Date, Start Time, End Time, Room, and Invigilator.
  3. System validates date format, time sequence (`startTime < endTime`), venue availability, and invigilator availability.
  4. Slot is saved and table refreshes.
- **Alternative Flow:** Venue or invigilator conflict throws `ScheduleConflictException`; a modal warning specifies the conflicting slot.

### Use Case UC-03: Room Seating Allocation
- **Primary Actor:** Examination Controller / Admin
- **Precondition:** Scheduled exams exist.
- **Flow:**
  1. Actor selects exam session in "Rooms & Seating".
  2. Actor clicks "Auto-Allocate Seating".
  3. System checks room capacity ($C$) and examinee count ($N$).
  4. System allocates $\min(C, N)$ students to numbered seats.
  5. System computes remaining free seats and displays the plan.

### Use Case UC-04: Record Hall Attendance
- **Primary Actor:** Teacher / Invigilator
- **Precondition:** Examination session is underway.
- **Flow:**
  1. Invigilator selects exam session in "Attendance".
  2. Invigilator selects student row.
  3. Invigilator clicks "Mark Present", "Mark Absent", or "Mark Late".
  4. System updates attendance record and refreshes KPI statistics.

### Use Case UC-05: Enter Assessment Marks
- **Primary Actor:** Teacher
- **Precondition:** Course has enrolled students.
- **Flow:**
  1. Actor selects course in "Marks & Assessment".
  2. Actor clicks "Enter / Edit Marks" on student row.
  3. Actor enters Class Test, Assignment, Midterm, and Final Exam scores.
  4. System verifies each score against maximum bounds.
  5. System calculates total score and derives letter grade.

### Use Case UC-06: Compute Semester GPA & Publish Results
- **Primary Actor:** Examination Controller (Admin)
- **Precondition:** Marks entered for semester courses.
- **Flow:**
  1. Controller selects semester in "Results & Transcripts".
  2. Controller clicks "Compute GPA".
  3. System calculates credit-weighted GPA for each student.
  4. Controller clicks "Publish Batch".
  5. System prompts for confirmation and marks results as published with audit timestamps.

### Use Case UC-07: Submit Academic Grievance
- **Primary Actor:** Student
- **Precondition:** Student is logged in.
- **Flow:**
  1. Student navigates to "Applications".
  2. Student clicks "+ Submit Application".
  3. Student selects category (e.g. Recheck), course, and enters grievance statement.
  4. Application is saved with status `PENDING`.

### Use Case UC-08: Export Institutional Audit Report
- **Primary Actor:** Admin
- **Precondition:** User is in "Reports & Analytics".
- **Flow:**
  1. Admin clicks "Export Institutional Audit Report".
  2. Admin selects file destination via `JFileChooser`.
  3. Background `SwingWorker` generates summary report and writes to disk.
  4. Success dialog alerts admin upon completion.

---

## 8. User Stories

1. **US-01 (Admin):** As an Examination Controller, I want to review candidate GPAs before official release so that errors can be corrected before students view grades.
2. **US-02 (Coordinator):** As a Department Coordinator, I want the system to alert me if an examination room is already booked so that exam schedule clashes are prevented.
3. **US-03 (Coordinator):** As a Coordinator, I want to be warned if a faculty member is assigned to two exams at once so that every hall has a dedicated invigilator.
4. **US-04 (Teacher):** As a Course Teacher, I want the system to prevent me from entering marks higher than the component maximum so that typographical errors are avoided.
5. **US-05 (Teacher):** As an Invigilator, I want to mark students present, absent, or late during roll call so that examination hall presence is officially recorded.
6. **US-06 (Student):** As a Student, I want to view my semester transcript with course grades and CGPA so that I can track my academic standing.
7. **US-07 (Student):** As a Student, I want to submit a recheck application online if I believe a grade was miscalculated so that my paper can be reviewed.
8. **US-08 (Admin):** As an Admin, I want to see department-wise CGPA and pass/fail metrics on an analytics dashboard so that institutional performance can be evaluated.
9. **US-09 (Admin):** As an Admin, I want to export institutional reports in the background without UI lag so that my workflow remains uninterrupted.
10. **US-10 (All Users):** As a User, I want to toggle between light and dark themes so that I can work comfortably in any lighting environment.

---

## 9. Feature Priorities (MoSCoW Framework)

- **Must Have:**
  - Salted password authentication with role-based access control.
  - Student and Course CRUD with search and department filtering.
  - Examination scheduling with automated room and invigilator conflict detection.
  - Marks entry with component boundary validation.
  - GPA/CGPA computation with authorized publication gate.
  - Native Java Object Serialization file persistence.
- **Should Have:**
  - Room capacity tracking and automatic seat numbering.
  - Roll-call attendance tracking with Present/Absent/Late statistics.
  - Student grievance submission and review workflow.
  - Dual Light and Dark theme engine.
- **Could Have:**
  - Background asynchronous report export via `SwingWorker`.
  - Java2D donut and bar charts for academic analytics.
  - Automated local data backup triggers.
- **Won't Have (Out of Scope for CSE 221 Desktop Application):**
  - Web frontend (HTML/CSS/JS) or REST HTTP microservices.
  - External SQL database server (MySQL, PostgreSQL).
  - External biometric hardware integration.

---

## 10. Acceptance & Success Criteria

1. **100% Compilation:** Clean compilation using standard JDK 17+ with zero errors.
2. **Zero Schedule Overlaps:** 100% prevention of room and faculty invigilator double-bookings.
3. **Valid Marks Only:** 100% rejection of marks exceeding maximum limits.
4. **Guaranteed Confidentiality:** 0% visibility of unpublished result batches to non-administrative roles.
5. **Complete Data Persistence:** All records survive application restart via serialized `.dat` files.
6. **Passing Test Suite:** All 39 automated unit and integration tests execute successfully with 100% pass rate.

