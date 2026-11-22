# Release History & Version Changelog — EduAsset

## University Examination & Academic Assessment Management System

---

## [Version 1.0.0] — 2026-11-13 (Initial Official Release)

### Course: CSE 221 — Object Oriented Programming
### University: Daffodil International University (DIU)

---

### 1. Presentation & UI/UX Architecture
- **Nimbra Visual Reference:** Implemented modern administrative dashboard with clean, flat aesthetics.
- **Collapsible Sidebar:** Configured collapsible sidebar supporting expanded (256 px) and collapsed (84 px) modes with role-based visibility filtering.
- **CardLayout Master Window:** Engineered single-frame navigation using `BorderLayout` and `CardLayout` across 14 module views.
- **TopBar Component:** Designed 62 px top bar featuring contextual screen headers, search input, live user display, and theme toggling.
- **ThemeManager:** Created runtime palette manager supporting **Light Porcelain** (`#E9EEF8` canvas) and **Dark Obsidian** (`#050A18` canvas) modes with real-time observer notification.
- **Reusable Custom Components:**
  - `RoundedPanel`: Antialiased graphics rendering rounded corners with subtle borders.
  - `StatCard`: Color-accented KPI metric cards with big-value typography.
  - `DataTablePanel`: Styled tables with custom header heights, alternating row stripes, and live search filtering.
  - `CustomButton`: Rounded, hover-reactive action buttons.
  - `FormDialog`: Standardized modal dialog base for data entry.

---

### 2. Functional Modules Implemented
- **Module 1 (Authentication & User Management):** Salted SHA-256 password hashing, session context, 4 user roles (Admin, Coordinator, Teacher, Student), user activation toggle, and administrative password resets.
- **Module 2 (Student Records):** Full CRUD for student directory, real-time ID/Name search, Department and Semester filtering, and deactivation confirmation dialogs.
- **Module 3 (Departments & Courses):** Dual-tab view for curricular course catalog (credits, semester, instructor assignment, enrollment) and departmental program management.
- **Module 4 (Examination Scheduling):** Timetable creation with automated collision detection engine preventing venue double-bookings, invigilator schedule overlaps, and time order inversions.
- **Module 5 (Rooms & Seating):** Venue hall capacity controls, auto-seating allocation engine, individual seat numbering (`Seat 1..N`), and capacity overflow prevention.
- **Module 6 (Attendance Management):** Invigilator roll-call roster, Present/Absent/Late status marking, timestamp logging, duplicate record prevention, and live KPI stats.
- **Module 7 (Marks & Assessment):** Multi-component grading (Class Test, Assignment, Midterm, Final Exam), strict boundary validation (`marks <= max`), automated totals and letter grade derivation.
- **Module 8 (Results & Transcripts):** Semester Term GPA and cumulative CGPA calculation, official grade transcript dialog, and controller-gated result publication workflow.
- **Module 9 (Applications & Grievances):** Student grievance submission (Recheck, Retake, Missing Marks), administrative status lifecycle (`PENDING` → `UNDER_REVIEW` → `APPROVED`/`REJECTED` → `COMPLETED`) with feedback remarks.
- **Module 10 (Reports & Analytics):** Executive dashboard, Java2D Pass/Fail distribution donut chart, Department CGPA bar chart, and asynchronous audit report file export via `SwingWorker`.
- **Module 11 (Activity History):** Comprehensive chronological audit timeline aggregating examination schedules, roll-calls, result releases, and grievance decisions.

---

### 3. Object-Oriented Programming (OOP) Architecture
- **Encapsulation:** 100% private fields with public validated getters/setters across all 14 entity classes.
- **Inheritance:** Concrete subclasses (`Student`, `Teacher`, `Admin`, `DepartmentCoordinator`) extend abstract parent `User` with constructor chaining via `super()`.
- **Polymorphism:** Method overriding (`getDashboardDescription()`, `getDisplayRole()`, `matchesSearch()`) and runtime dynamic dispatch across `User[]` collections.
- **Abstraction:** Implemented `Searchable`, `Approvable`, and `ReportGenerator` behavioral interfaces.
- **Custom Exceptions:** Created structured hierarchy including `ScheduleConflictException`, `InvalidMarksException`, `DuplicateStudentIdException`, and `AuthorizationException`.

---

### 4. Data Storage & Testing
- **Java Object Serialization:** Native binary `.dat` persistence via `FileStorageService` with corrupted file handling and snapshot backup triggers.
- **Quality Assurance:** Developed automated test suite `EduAssetTest.java` verifying 39 business assertions with 100% pass rate.
- **Packaging:** Assembled runnable standalone `EduAsset.jar` (208 KB).

---

### 5. Future Roadmap (Post v1.0.0)
- Optional peer-to-peer TCP/IP socket notification daemon for real-time exam alerts between controller and invigilators.
- PDF generation export for official grade sheets.
- Biometric USB scanner support for hall roll-calls.

