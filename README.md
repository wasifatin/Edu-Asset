# EduAsset — University Examination & Academic Assessment Management System

> **Tagline:** Smart Examination Administration. Reliable Academic Assessment.

[![Java](https://img.shields.io/badge/Language-Java%2017%2B-orange.svg)](https://www.oracle.com/java/)
[![GUI](https://img.shields.io/badge/GUI-Java%20Swing-blue.svg)]()
[![Course](https://img.shields.io/badge/Course-CSE%20221%20OOP-green.svg)]()
[![University](https://img.shields.io/badge/Institution-Daffodil%20International%20University-red.svg)](https://daffodilvarsity.edu.bd/)

---

## 1. Project Overview & Problem Statement

University examination administration is one of the most critical operational workflows in tertiary education. A modern Controller of Examinations office must coordinate multi-departmental curriculums, resolve venue and faculty scheduling collisions, manage physical seat assignments, record real-time examinee attendance, calculate weighted grade point averages (GPA/CGPA), govern confidential result publication gates, and arbitrate student grievances.

Traditional paper-bound or ad-hoc spreadsheet methods introduce severe operational bottlenecks:
- **Scheduling Conflicts:** Double-booking examination rooms or assigning faculty members to multiple simultaneous examination sessions.
- **Venue Overflow:** Exceeding physical room capacity during seat allocation.
- **Evaluation Irregularities:** Assessment component scores inadvertently exceeding maximum marks.
- **Premature Disclosure:** Prematurely exposing unpublished grades prior to official institutional authorization.
- **Audit Deficits:** Inability to track historical schedule revisions, mark alterations, or grievance review lifecycles.

**EduAsset** resolves these challenges by providing an integrated, object-oriented desktop application built natively in **Java Swing**. Modeled after the modern **Nimbra** administrative dashboard architecture, EduAsset incorporates a desktop-first design with collapsible sidebar navigation, dynamic CardLayout views, responsive KPI cards, and full runtime Light/Dark theme switching.

---

## 2. Institutional Objectives

1. **Simulation of Real-World Administration:** Faithfully reproduce the operational responsibilities of the Examination Controller's Office at Daffodil International University.
2. **Object-Oriented Excellence:** Provide a benchmark implementation of the four pillars of OOP (Encapsulation, Inheritance, Polymorphism, Abstraction) for CSE 221.
3. **Automated Conflict Prevention:** Block venue overlaps, invigilator collisions, and room capacity violations in real time.
4. **Data Integrity & Security:** Enforce salted password hashing, role-based access control (RBAC), and persistent Java Object Serialization without external database dependencies.
5. **Modern Desktop UI/UX:** Deliver an interface featuring custom Graphics2D rounded corners, consistent 12–20px spacing, and Segoe UI typography.

---

## 3. Implemented Modules & Capabilities

| Module | Core Features |
|---|---|
| **Module 1: Authentication & User Management** | Salted SHA-256 login, 4 institutional roles (Admin, Coordinator, Teacher, Student), session management, account provisioning, activation toggle, password reset. |
| **Module 2: Student Management** | Student CRUD, ID/Name live search, Department and Semester filtering, profile inspection dialog, confirmation on deactivation. |
| **Module 3: Department & Course Management** | Dual-tab interface: Course catalog management (credits, syllabus, faculty assignment, student enrollment) and Department configuration (degrees, head of dept). |
| **Module 4: Examination Scheduling** | Examination slot creation (Midterm, Final, Class Test, Quiz), conflict engine blocking venue double-bookings, invigilator overlaps, and time order violations. |
| **Module 5: Room & Seat Allocation** | Venue capacity tracking (e.g. KT-320: 40 seats), auto-allocation engine, individual seat numbering (Seat 1..N), overflow prevention. |
| **Module 6: Attendance Management** | Invigilator roll-call tracker, Present/Absent/Late status marking, timestamp logging, duplicate record prevention, live KPI stats. |
| **Module 7: Marks & Assessment Management** | Multi-component entry (Class Test /20, Assignment /10, Midterm /30, Final /40), strict boundary validation (`marks <= max`), automated total and letter grade calculation. |
| **Module 8: Result Processing & Publication** | Official GPA and CGPA computation, DIU grading scale mapping, individual academic grade transcript view, two-tier authorization gate for publication. |
| **Module 9: Applications & Complaints** | Student grievance lodging (Recheck, Retake, Missing Marks, Complaints), administrative review workflow (Pending → Under Review → Approved/Rejected → Completed). |
| **Module 10: Reports & Analytics** | Executive dashboard, Java2D Pass/Fail donut chart, Department CGPA bar chart, asynchronous audit report export using background `SwingWorker`. |
| **Module 11: Examination History** | Centralized audit timeline tracking examination schedules, roll-calls, result releases, and grievance decisions. |

---

## 4. Object-Oriented Programming (OOP) Implementation

Developed specifically for **CSE 221 – Object Oriented Programming**, the codebase demonstrates the four foundational pillars:

### 4.1 Encapsulation
- All domain entity classes (`User`, `Student`, `Teacher`, `Admin`, `Course`, `Exam`, `ExamRoom`, `MarkRecord`, `Result`, `ApplicationRequest`) maintain strictly `private` instance variables.
- Access is provided exclusively through validated public getters and setters.
- Example (`MarkRecord.java`): Mutator methods `setClassTest(double)` and `setAssignment(double)` automatically trigger total and grade recalculation while `MarksService.validateMarks(MarkRecord)` enforces `0 <= marks <= max`.

### 4.2 Inheritance
- An abstract parent class `com.examsphere.model.User` defines common identity, credentials, activation status, and role metadata.
- Concrete subclasses extend `User`:
  - `Student extends User` (adds department, program, semester, contact number)
  - `Teacher extends User` (adds department, designation, assigned course list)
  - `Admin extends User` (adds administrative designation)
  - `DepartmentCoordinator extends User` (adds departmental oversight scope)
- Parameterized constructors utilize constructor chaining via `super(userId, fullName, email, passwordHash, role)`.

### 4.3 Polymorphism
- **Dynamic Method Dispatch:** `User` declares abstract methods `getDashboardDescription()` and `getDisplayRole()`. Each subclass overrides these methods to return role-specific output.
- **Polymorphic Collections:** Arrays of `User[]` are traversed polymorphically during authentication checks and user directory views.
- **Method Overloading:** Demonstrated across service layers, such as `StudentService.filterByDepartment(String)` vs. `StudentService.filterBySemester(int)`, and overloaded constructors in model and UI classes.

### 4.4 Abstraction & Interfaces
- Abstract parent classes (`User`) prevent direct instantiation of generic user objects.
- Business interfaces establish strict operational contracts:
  - `Searchable`: Implemented by `User` and subclasses for uniform keyword filtering (`matchesSearch(String query)`).
  - `Approvable`: Implemented by `ApplicationRequest` defining `approve(String)`, `reject(String)`, and `getApprovalStatus()`.
  - `ReportGenerator`: Defines contracts for generating and exporting institutional records.

---

## 5. Technology Stack

- **Platform:** Java Standard Edition (Java SE 17 / 21 / 26)
- **GUI Framework:** Native Java Swing (`JFrame`, `JPanel`, `CardLayout`, `JTable`, `Graphics2D`)
- **Persistence Tier:** Java Object Serialization (`ObjectOutputStream`, `ObjectInputStream`, `FileOutputStream`, `FileInputStream`)
- **Multithreading:** `javax.swing.SwingWorker` and background threads for asynchronous I/O and report generation
- **Security:** Salted SHA-256 cryptographic hashing (`MessageDigest`)
- **Look & Feel:** System Look-and-Feel with customized Nimbra-style light/dark color palettes

---

## 6. System Requirements

- **Operating System:** Windows 10/11, macOS 11+, or Linux (Ubuntu 20.04+)
- **Java Development Kit (JDK):** Version 17 or higher (Verified on Oracle JDK 26)
- **Display Resolution:** Recommended 1440 × 900 (Minimum supported: 1100 × 700)
- **Memory (RAM):** 512 MB minimum (1 GB recommended)
- **Disk Space:** 50 MB free space for code and serialized data files

---

## 7. Project Architecture & Directory Structure

```
EduAsset/
├── README.md                      # Primary project overview & instructions
├── SPEC.md                        # Master architectural specifications
├── .gitignore                     # Git ignore rules for Java, IDEs, and binary data
├── EduAsset.jar                   # Runnable packaged application JAR
│
├── docs/                          # Comprehensive Technical Documentation
│   ├── PRD.md                     # Product Requirements Document
│   ├── APPLICATION_ARCHITECTURE.md# High-level architecture & layers
│   ├── API.md                     # Internal Java service & model API reference
│   ├── DESIGN.md                  # UI/UX design language & color palette
│   ├── SECURITY.md                # Security, RBAC & hashing architecture
│   ├── INTEGRATION.md             # Inter-module data flow & event integration
│   ├── SETUP_AND_INITIALIZATION.md# Setup guide for NetBeans, IntelliJ & CLI
│   ├── TESTING_AND_QA.md          # 39-point unit & functional test suite
│   ├── UML_CLASS_DIAGRAM.md       # Mermaid UML class hierarchy & relationships
│   ├── DATA_MODEL.md              # Domain entities, attributes & schema
│   ├── USER_GUIDE.md              # End-user operational handbook
│   ├── DEPLOYMENT.md              # Compilation, packaging & distribution guide
│   └── CHANGELOG.md               # Version release notes & feature tracking
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── examsphere/
│   │   │           ├── Main.java              # Application launcher & EDT entry
│   │   │           ├── model/                 # 14 Serializable Domain Entities
│   │   │           ├── service/               # 10 Singleton Business Services
│   │   │           ├── ui/                    # 18 Swing Window & Panel Components
│   │   │           ├── components/            # 5 Reusable Custom Swing Controls
│   │   │           ├── storage/               # FileStorageService (Serialization)
│   │   │           ├── exception/             # 7 Custom Application Exceptions
│   │   │           ├── interfaces/            # Domain Business Interfaces
│   │   │           └── util/                  # ThemeManager, Validation, PasswordUtil
│   │   └── resources/
│   └── test/
│       └── java/
│           └── com/
│               └── examsphere/
│                   └── EduAssetTest.java      # Comprehensive automated test runner
│
└── data/                                      # Persistent binary storage (.dat)
    └── .gitkeep
```

---

## 8. Installation & Execution Guide

### Option A: Running the Pre-Packaged Runnable JAR (Fastest Desktop)

```powershell
# Open terminal in project root
java -jar EduAsset.jar
```

### Option B: Running the Interactive Web-Based Prototype

You can also run the full interactive web-based dashboard directly in any browser:
```powershell
# Simply double-click index.html or launch it via command line:
Start-Process index.html
```
*The web prototype features all 11 modules, interactive Light/Dark theme switching, live conflict detection, marks boundary validation, GPA computation, and role switching.*

### Option C: Compiling & Running from Source Code via CLI

```powershell
# 1. Navigate to project root directory
cd "d:\Programming\Antigravity IDE\EduAsset"

# 2. Compile all source files into the 'out' directory
javac -d out -sourcepath src/main/java src/main/java/com/examsphere/Main.java

# 3. Launch the application
java -cp out com.examsphere.Main
```

### Option C: Executing the Automated Unit Test Suite

```powershell
# Compile test suite
javac -d out -sourcepath "src/main/java;src/test/java" src/test/java/com/examsphere/EduAssetTest.java

# Execute test suite (runs 39 comprehensive validation tests)
java -cp out com.examsphere.EduAssetTest
```

---

## 9. Demonstration Login Credentials

EduAsset automatically initializes pre-configured demonstration accounts on initial launch for both the native **Java Swing Desktop Application** and the **Interactive Web-Based Dashboard**. Users can authenticate using either their **Institutional Email** or **User ID**:

| Role / Designation | Full Name | Institutional Email | User ID | Password | Scope & Primary Authorities |
|---|---|---|---|---|---|
| **Examination Controller (Admin)** | **Mr. Md. Ashraful Islam Talukder** | `ashrafulislam.cse@diu.edu.bd` | `ADMIN001` | `admin123` | Office of the Controller • Full System Authority, Audit Trails, Official Result Publication & Grading Governance |
| **Department Coordinator** | **Dewan Mamun Raza** | `dewan.cse@diu.edu.bd` | `COORD001` | `coord123` | Computer Science & Engineering • Curriculum Management, Exam Scheduling, Room & Seat Allocation |
| **Faculty Member / Invigilator** | **Pranto Protim Choudhury** | `prantoprotimchoudhury.cse@diu.edu.bd` | `TCH001` | `teach123` / `teacher123` | CSE Department • Marks Entry (Continuous Assessments & Final Exams), Hall Supervision, Attendance Roll Call |
| **Student Examinee** | **Fatin Sharhad** | `252-15-869@diu.edu.bd` | `STU001` / `252-15-869` | `student123` | B.Sc. in CSE (Semester 5) • Semester Results, Official Academic Transcripts, Continuous Evaluation Review & Grievance Lodging |
| **Student (Alternative Demo)** | **Nusrat Jahan** | `nusrat.stu@examsphere.diu.edu.bd` | `STU002` | `student123` | B.Sc. in CSE (Semester 5) • Secondary Student Testing Persona |

> [!NOTE]
> - **Dual Identifier Authentication:** In both the desktop application and web portal, you may sign in with either your **User ID** (e.g. `ADMIN001`) or your **Institutional Email** (e.g. `ashrafulislam.cse@diu.edu.bd`).
> - **Web Portal Quick Roleplay:** The web sign-in portal includes a quick-selector dropdown in the top corner to automatically fill credentials and switch personas with a single click.

---

## 10. User Interface Architecture

- **Window Sizing:** Default: 1440 × 900 px | Minimum: 1100 × 700 px
- **Sidebar Dimensions:** Expanded: 256 px | Collapsed: 84 px
- **Top Navigation Height:** 62 px with integrated live search, title display, and theme toggle.
- **Card Geometry:** Corner radius of 14–26 px with subtle borders.
- **Theme Palette:**
  - **Light Mode:** Canvas `#E9EEF8`, Surface `#FFFFFF`, Primary `#2F5CF0`, Text `#0D1630`.
  - **Dark Mode:** Canvas `#050A18`, Surface `#101A36`, Primary `#3D6BFF`, Text `#EEF3FF`.

---

## 11. Known Limitations & Scope Exclusions

1. **Standalone Desktop Scope:** As per CSE 221 requirements, EduAsset is an offline desktop Swing system; it does not connect to remote SQL database servers or web endpoints.
2. **Local Persistence:** Data persistence uses Java Object Serialization files stored in `./data/*.dat`. If files are deleted, the application seamlessly reinitializes default demonstration accounts.
3. **Typography Fallback:** If the preferred **Urbanist** font is absent from the host operating system, the system falls back seamlessly to **Segoe UI** or system sans-serif.

---

## 12. Course & Institutional Metadata

- **Course:** CSE 221 — Object Oriented Programming
- **Program:** B.Sc. in Computer Science & Engineering
- **Faculty:** Faculty of Science and Information Technology (FSIT)
- **Institution:** Daffodil International University (DIU), Dhaka, Bangladesh
- **Academic Term:** Spring 2026

---

## 13. License

Developed for academic evaluation and educational instruction at Daffodil International University. All rights reserved.

