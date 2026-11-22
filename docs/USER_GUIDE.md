# End-User Operational Handbook — EduAsset

## University Examination & Academic Assessment Management System

---

## 1. Getting Started & Logging In

1. Launch the application:
   ```bash
   java -jar EduAsset.jar
   ```
2. The branded **EduAsset Sign-In** screen appears:
   - Enter your assigned **User ID** (e.g. `ADMIN001` or `STU001`).
   - Enter your **Password** (e.g. `admin123` or `student123`).
   - Click **Sign In to EduAsset**.
3. On successful authentication, the main application window appears configured for your role.

---

## 2. Navigating the Workspace

### 2.1 Collapsible Sidebar
- The left sidebar organizes features into five sections: `WORKSPACE`, `EXAMINATION`, `ACADEMIC`, `INSIGHTS`, and `ADMINISTRATION`.
- Click the **◀ / ▶** button in the sidebar header to toggle between the **Expanded** (256 px) and **Compact** (84 px) views.
- Clicking any navigation item smoothly switches the active view using Swing `CardLayout` without opening separate windows.

### 2.2 Top Navigation Bar
- Displays the active screen title on the left.
- Search input field for quick keyword filtering.
- **☀ / 🌙 Theme Toggle Button:** Switch instantly between Light Porcelain and Dark Obsidian color palettes.
- User profile badge displaying your name and role.

---

## 3. Managing Student Records (Module 2)

*Permitted Roles: Admin, Coordinator, Teacher (View Only)*

1. Navigate to **Student Records** in the sidebar.
2. **Search & Filter:**
   - Type in the **Search** box to filter live by student name or ID.
   - Use the **Department** dropdown (`CSE`, `SWE`, `EEE`, etc.) and **Semester** dropdown to isolate student batches.
3. **Add New Student:**
   - Click **+ Add Student**.
   - Enter Student ID (e.g. `STU005`), Full Name, Email, Department, Program, Semester, and Contact.
   - Click **Save Student**. The new record appears in the directory table.
4. **Edit Student:**
   - Select a row in the table and click **Edit**.
   - Modify contact details, semester standing, or active account status, then click **Update Information**.
5. **Deactivate Student:**
   - Select a row and click **Delete**. Confirm the prompt to deactivate the account.

---

## 4. Department & Course Curriculum (Module 3)

*Permitted Roles: Admin, Coordinator*

1. Navigate to **Departments & Courses**.
2. **Academic Courses Tab:**
   - View all registered courses, credit hours, semesters, and instructors.
   - Click **+ Add Course** to introduce a new subject code (e.g. `CSE311`), title, and credits.
   - Select a course and click **Assign Teacher** to assign a faculty member as instructor.
3. **Academic Departments Tab:**
   - View faculty departments and degree programs.
   - Click **+ Add Department** to configure new academic units (e.g. `BBA` or `ENG`).

---

## 5. Examination Timetable Scheduling (Module 4)

*Permitted Roles: Admin, Coordinator*

1. Navigate to **Exam Scheduling**.
2. Click **+ Schedule Exam**.
3. Fill in the session parameters:
   - **Target Course:** Select from curriculum dropdown (e.g. `CSE221`).
   - **Exam Type:** Select `MIDTERM`, `FINAL`, `CLASS_TEST`, or `QUIZ`.
   - **Exam Date:** Enter date in `YYYY-MM-DD` format.
   - **Time Slot:** Enter 24-hr Start Time (`10:00`) and End Time (`12:00`).
   - **Allocated Room:** Select available hall (e.g. `KT-320`).
   - **Faculty Invigilator:** Select supervising teacher (e.g. `TCH001`).
4. Click **Confirm Schedule**.
5. **Conflict Resolution Engine:**
   - If the selected hall is already booked for an overlapping exam, an alert warning displays: `Room Conflict: Room 'KT-320' is already occupied...`
   - If the selected invigilator is assigned elsewhere at that time, an alert displays: `Invigilator Conflict: Faculty 'TCH001' is already assigned...`
   - Adjust the time, venue, or invigilator to save the slot.

---

## 6. Room Allocation & Seating Arrangements (Module 5)

*Permitted Roles: Admin, Coordinator*

1. Navigate to **Rooms & Seating**.
2. **Venues List (Left Panel):** Review examination hall capacities, building locations, and facilities (AC, CCTV).
3. **Seating Plan (Right Panel):**
   - Select the target examination session from the dropdown.
   - Click **Auto-Allocate Seating**.
   - The system checks room capacity, assigns candidates sequentially (`Seat 1..N`), and reports total allocated and remaining available seats.

---

## 7. Invigilator Roll-Call Attendance (Module 6)

*Permitted Roles: Admin, Coordinator, Teacher*

1. Navigate to **Attendance**.
2. Select your scheduled examination session from the top dropdown.
3. The examinee roster loads showing attendance status.
4. **Mark Attendance:**
   - Select an examinee row.
   - Click **Mark Present**, **Mark Absent**, or **Mark Late**.
   - The status updates immediately with a recorded timestamp.
   - Top KPI cards update present, absent, and late counts in real time.

---

## 8. Continuous Assessment Marks Entry (Module 7)

*Permitted Roles: Admin, Coordinator, Teacher*

1. Navigate to **Marks & Assessment**.
2. Select the course from the dropdown (e.g. `CSE221`).
3. Select an enrolled student row and click **Enter / Edit Marks**.
4. Enter component scores:
   - **Class Test:** Between `0.0` and `20.0`.
   - **Assignment:** Between `0.0` and `10.0`.
   - **Midterm:** Between `0.0` and `30.0`.
   - **Final Exam:** Between `0.0` and `40.0`.
5. Click **Save Assessment**.
   - If any score exceeds its maximum limit, an alert blocks the entry.
   - On valid entry, total score and letter grade (e.g. `A+`) are calculated automatically.

---

## 9. Computing GPAs & Publishing Results (Module 8)

*Permitted Roles: Admin (Sole Authority for Publication)*

1. Navigate to **Results & Transcripts**.
2. Select the semester batch (e.g. `Semester 5`).
3. Click **Compute GPA**. The system computes credit-weighted Term GPAs and CGPAs for all students.
4. **Review Status:** Results display as `UNPUBLISHED (DRAFT)`. Students cannot see unpublished grades.
5. **Publish Results:** Click **Publish Batch**. Confirm the authorization prompt. The status changes to `PUBLISHED`.
6. **Inspect Transcript:** Select any student row and click **View Transcript** to view a printable grade report dialog with subject breakdowns and official seals.

---

## 10. Processing Student Grievances (Module 9)

*Student Role: Submit Grievance | Admin/Coordinator: Review & Decide*

### Submitting a Grievance (Student)
1. Navigate to **Applications**.
2. Click **+ Submit Application**.
3. Select category (`RECHECK`, `RETAKE`, `MISSING_MARKS`, `COMPLAINT`), choose course, and enter your statement.
4. Click **Submit Form**. Application is assigned a tracking ID in `PENDING` status.

### Reviewing a Grievance (Admin / Coordinator)
1. Select an application row in the table.
2. Click **Review / Decision**.
3. Review student statement and previous marks.
4. Change status to `APPROVED` or `REJECTED`, input controller feedback remarks, and click **Save Decision**.

---

## 11. Institutional Analytics & Audit Export (Module 10)

*Permitted Roles: Admin, Coordinator*

1. Navigate to **Reports & Analytics**.
2. Review top KPI metrics: Exam Attendance Rate, Institutional Pass Rate, Unsatisfactory (Fail) Rate.
3. Review visual Java2D charts:
   - **Pass/Fail Distribution Donut Chart.**
   - **Departmental Average CGPA Benchmark Bar Chart.**
4. Click **Export Institutional Audit Report**. Choose a destination file (e.g. `audit_report.txt`). A background worker thread compiles and exports the report without freezing the UI.

---

## 12. Settings, Theming & Data Backup

1. Navigate to **Settings** in the sidebar.
2. **Theme Selection:** Click **Switch to Dark Mode** or **Switch to Light Mode** to change theme palettes.
3. **Data Backup:** Click **Trigger Immediate Local Data Backup** to snapshot all system files to `./backup/`.
4. Review system versioning and Daffodil International University course metadata.

