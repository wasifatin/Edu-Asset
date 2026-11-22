# EduAsset — Shared API Contract & Specification

## Project Root: `d:\Programming\Antigravity IDE\EduAsset`
## Base Package: `com.examsphere`

---

## 1. MODEL CLASSES (package: com.examsphere.model)

All model classes implement `java.io.Serializable` with explicit `serialVersionUID`.

### User.java (ABSTRACT)
```java
package com.examsphere.model;
public abstract class User implements Serializable, com.examsphere.interfaces.Searchable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private String fullName;
    private String email;
    private String passwordHash;
    private String role; // "ADMIN", "COORDINATOR", "TEACHER", "STUDENT"
    private boolean active;
    private String createdDate; // ISO format "YYYY-MM-DD"
    
    // Constructors
    public User() {} // default
    public User(String userId, String fullName, String email, String passwordHash, String role) // parameterized
    
    // Getters/setters for ALL fields
    // abstract methods:
    public abstract String getDashboardDescription();
    public abstract String getDisplayRole();
    
    // toString, equals (by userId), hashCode
}
```

### Student.java extends User
```java
private String studentId; // same as userId, set via super
private String department;
private String program;
private int semester;
private String contactNumber;
private String enrollmentDate; // "YYYY-MM-DD"

// Constructor: Student(String studentId, String fullName, String email, String passwordHash, String department, String program, int semester, String contactNumber)
//   → calls super(studentId, fullName, email, passwordHash, "STUDENT")
// Getters/setters for student-specific fields
// getDashboardDescription() → "Student Dashboard — " + department
// getDisplayRole() → "Student"
// matchesSearch(String query) → checks studentId, fullName, email, department (case-insensitive)
```

### Teacher.java extends User
```java
private String teacherId;
private String department;
private String designation; // e.g. "Lecturer", "Professor"
private List<String> assignedCourseIds; // list of course codes

// Constructor: Teacher(String teacherId, String fullName, String email, String passwordHash, String department, String designation)
//   → super(teacherId, fullName, email, passwordHash, "TEACHER")
// getDashboardDescription() → "Faculty Dashboard — " + department
// getDisplayRole() → "Teacher / Invigilator"
```

### Admin.java extends User
```java
private String adminId;
private String designation; // e.g. "Examination Controller"

// Constructor: Admin(String adminId, String fullName, String email, String passwordHash, String designation)
//   → super(adminId, fullName, email, passwordHash, "ADMIN")
// getDashboardDescription() → "Administration Dashboard"
// getDisplayRole() → "Examination Controller"
```

### DepartmentCoordinator.java extends User
```java
private String coordinatorId;
private String department;

// Constructor: DepartmentCoordinator(String coordinatorId, String fullName, String email, String passwordHash, String department)
//   → super(coordinatorId, fullName, email, passwordHash, "COORDINATOR")
// getDashboardDescription() → "Department Coordinator — " + department
// getDisplayRole() → "Department Coordinator"
```

### Department.java
```java
private String departmentId;     // e.g. "CSE"
private String departmentName;   // e.g. "Computer Science and Engineering"
private String headOfDepartment;
private List<String> programs;   // e.g. ["B.Sc. in CSE", "M.Sc. in CSE"]
private List<String> courseIds;  // course codes in this dept

// Constructor: Department(String departmentId, String departmentName)
// Getters/setters, add/remove programs and courses
```

### Course.java
```java
private String courseCode;      // e.g. "CSE221"
private String courseName;     // e.g. "Object Oriented Programming"
private String departmentId;
private int credits;
private int semester;
private String teacherId;      // assigned teacher userId
private List<String> enrolledStudentIds;

// Constructor: Course(String courseCode, String courseName, String departmentId, int credits, int semester)
// enrollStudent(String studentId), removeStudent(String studentId)
// Getters/setters
```

### Exam.java
```java
private String examId;          // e.g. "EX-2024-001"
private String courseCode;
private String examType;        // "MIDTERM", "FINAL", "CLASS_TEST", "QUIZ"
private int semester;
private String examDate;        // "YYYY-MM-DD"
private String startTime;       // "HH:mm"
private String endTime;         // "HH:mm"
private String roomId;
private String invigilatorId;   // teacher userId
private String status;          // "SCHEDULED", "ONGOING", "COMPLETED", "CANCELLED"

// Constructor: Exam(String examId, String courseCode, String examType, int semester, String examDate, String startTime, String endTime)
// Getters/setters
```

### ExamRoom.java
```java
private String roomId;         // e.g. "KT-320"
private String building;       // e.g. "KT Building"
private int capacity;
private boolean available;
private List<String> facilities; // e.g. ["Projector", "AC"]

// Constructor: ExamRoom(String roomId, String building, int capacity)
```

### SeatAssignment.java
```java
private String seatId;
private String examId;
private String roomId;
private String studentId;
private int seatNumber;

// Constructor: SeatAssignment(String seatId, String examId, String roomId, String studentId, int seatNumber)
```

### AttendanceRecord.java
```java
private String attendanceId;
private String examId;
private String studentId;
private String status;         // "PRESENT", "ABSENT", "LATE"
private String remarks;
private String timestamp;      // ISO datetime

// Constructor: AttendanceRecord(String attendanceId, String examId, String studentId, String status)
```

### MarkRecord.java
```java
private String markId;
private String courseCode;
private String studentId;
private double classTest;
private double assignment;
private double midterm;
private double finalExam;
private double totalMarks;
private String grade;
private double maxClassTest;    // default 20
private double maxAssignment;   // default 10
private double maxMidterm;      // default 30
private double maxFinalExam;    // default 40

// Constructor: MarkRecord(String markId, String courseCode, String studentId)
// calculateTotal() → sets totalMarks = classTest + assignment + midterm + finalExam
// calculateGrade() → sets grade based on totalMarks percentage
```

### Result.java
```java
private String resultId;
private String studentId;
private int semester;
private double gpa;
private double cgpa;
private boolean published;
private String publishedBy;    // admin userId who published
private String publishDate;
private Map<String, String> courseGrades; // courseCode → grade
private Map<String, Double> courseGradePoints; // courseCode → gradePoint
private Map<String, Integer> courseCredits; // courseCode → credits

// Constructor: Result(String resultId, String studentId, int semester)
// calculateGPA(), isPublished()
```

### ApplicationRequest.java (implements Approvable)
```java
private String applicationId;  // e.g. "APP-001"
private String studentId;
private String applicationType; // "RECHECK", "RETAKE", "MISSING_MARKS", "COMPLAINT"
private String courseCode;      // related course (nullable)
private String description;
private String status;          // "PENDING", "UNDER_REVIEW", "APPROVED", "REJECTED", "COMPLETED"
private String adminComments;
private String submissionDate;  // "YYYY-MM-DD"
private String lastUpdated;

// Constructor: ApplicationRequest(String applicationId, String studentId, String applicationType, String description)
// Approvable methods: approve(String comments), reject(String comments), getApprovalStatus()
```

---

## 2. INTERFACES (package: com.examsphere.interfaces)

### Searchable.java
```java
boolean matchesSearch(String query);
```

### Approvable.java
```java
void approve(String comments);
void reject(String comments);
String getApprovalStatus();
```

### ReportGenerator.java
```java
String generateReport();
void exportReport(String filePath);
```

---

## 3. EXCEPTIONS (package: com.examsphere.exception)

All extend `Exception`:
- `DuplicateStudentIdException(String message)`
- `InvalidMarksException(String message)`
- `ScheduleConflictException(String message)`
- `InvalidLoginException(String message)`
- `InvalidApplicationException(String message)`
- `DataStorageException(String message)` — wraps I/O errors
- `AuthorizationException(String message)` — access denied

---

## 4. UTILITY CLASSES (package: com.examsphere.util)

### ThemeManager.java
Singleton pattern. Stores current theme ("LIGHT" or "DARK"). Provides color getters:
```java
public static ThemeManager getInstance()
public void toggleTheme()
public boolean isDarkTheme()
public Color getBackground()        // Light: #E9EEF8, Dark: #050A18
public Color getMainSurface()       // Light: #FFFFFF, Dark: #101A36
public Color getSecondarySurface()  // Light: #F5F7FA, Dark: #0B132A
public Color getPrimaryText()       // Light: #0D1630, Dark: #EEF3FF
public Color getSecondaryText()     // Light: #55617C, Dark: #95A2C2
public Color getPrimaryAccent()     // Light: #2F5CF0, Dark: #3D6BFF
public Color getSuccess()           // Light: #0E9A78, Dark: #4FE0A0
public Color getWarning()           // Light: #C27C0E, Dark: #FBC35E
public Color getDanger()            // Light: #BE2639, Dark: #FF7D8D
public Color getInfo()              // Light: #1563B5, Dark: #5B9EE0
public Color getSidebarBg()         // Light: #FFFFFF, Dark: #0B132A
public Color getBorderColor()       // Light: #E0E5F0, Dark: #1A2744
public Font getFont(int size)       // Returns "Segoe UI" or system sans-serif
public Font getBoldFont(int size)
public void addThemeChangeListener(Runnable listener)
public void notifyThemeChanged()
```

### Constants.java
```java
public static final int WINDOW_WIDTH = 1440;
public static final int WINDOW_HEIGHT = 900;
public static final int MIN_WIDTH = 1100;
public static final int MIN_HEIGHT = 700;
public static final int SIDEBAR_EXPANDED = 256;
public static final int SIDEBAR_COLLAPSED = 84;
public static final int TOPBAR_HEIGHT = 62;
public static final int CARD_RADIUS = 18;
public static final int CONTROL_RADIUS = 12;
public static final int SPACING = 16;
public static final int SPACING_SM = 12;
public static final int SPACING_LG = 20;
public static final String DATA_DIR = "data";
```

### ValidationUtil.java
```java
public static boolean isValidEmail(String email)
public static boolean isValidId(String id) // non-empty alphanumeric
public static boolean isValidMarks(double marks, double max) // 0 <= marks <= max
public static boolean isNotEmpty(String s)
public static boolean isValidDate(String date) // YYYY-MM-DD format
public static boolean isValidTime(String time) // HH:mm format
```

### IdGenerator.java
```java
public static String generateId(String prefix) // e.g., "STU-" + timestamp-based unique
```

### PasswordUtil.java
```java
public static String hashPassword(String password) // Simple hash (SHA-256 + salt for demo)
public static boolean verifyPassword(String password, String hash)
```

### GradeCalculator.java
```java
public static String calculateGrade(double percentage)
  // A+: >=90, A: >=85, A-: >=80, B+: >=75, B: >=70, B-: >=65
  // C+: >=60, C: >=55, D: >=50, F: <50
public static double getGradePoint(String grade)
  // A+: 4.00, A: 3.75, A-: 3.50, B+: 3.25, B: 3.00, B-: 2.75
  // C+: 2.50, C: 2.25, D: 2.00, F: 0.00
public static double calculateGPA(Map<String, Double> courseGradePoints, Map<String, Integer> courseCredits)
```

---

## 5. STORAGE (package: com.examsphere.storage)

### FileStorageService.java
```java
// Singleton
public static FileStorageService getInstance()

// Generic save/load using ObjectOutputStream / ObjectInputStream
public <T> void saveList(List<T> list, String filename) // saves to data/<filename>.dat
public <T> List<T> loadList(String filename) // loads from data/<filename>.dat, returns empty list if file missing/corrupt

// Convenience methods per entity:
public void saveUsers(List<User> users)
public List<User> loadUsers()
public void saveDepartments(List<Department> depts)
public List<Department> loadDepartments()
public void saveCourses(List<Course> courses)
public List<Course> loadCourses()
public void saveExams(List<Exam> exams)
public List<Exam> loadExams()
public void saveRooms(List<ExamRoom> rooms)
public List<ExamRoom> loadRooms()
public void saveAttendance(List<AttendanceRecord> records)
public List<AttendanceRecord> loadAttendance()
public void saveMarks(List<MarkRecord> marks)
public List<MarkRecord> loadMarks()
public void saveResults(List<Result> results)
public List<Result> loadResults()
public void saveApplications(List<ApplicationRequest> apps)
public List<ApplicationRequest> loadApplications()
public void saveSeatAssignments(List<SeatAssignment> seats)
public List<SeatAssignment> loadSeatAssignments()

// Backup
public void backupData(String backupDir)
public void initializeDataDirectory()
```

---

## 6. SERVICES (package: com.examsphere.service)

All services are singletons. They operate on in-memory Lists loaded at startup and save after modifications.

### AuthService.java
```java
public static AuthService getInstance()
public User login(String userId, String password) throws InvalidLoginException
public void logout()
public User getCurrentUser()
public boolean isLoggedIn()
public boolean hasPermission(String action) // checks role-based permissions
public void createUser(User user) throws DuplicateStudentIdException
public void updateUser(User user)
public List<User> getAllUsers()
public User getUserById(String userId)
public void initializeDefaultUsers() // creates demo admin, teacher, student, coordinator
```

**Demo Users (created by initializeDefaultUsers):**
- Admin: userId="ADMIN001", password="admin123", name="Mr. Md. Ashraful Islam Talukder", email="ashrafulislam.cse@diu.edu.bd", designation="Examination Controller"
- Coordinator: userId="COORD001", password="coord123", name="Dewan Mamun Raza", email="dewan.cse@diu.edu.bd", dept="CSE"
- Teacher: userId="TCH001", password="teach123" / "teacher123", name="Pranto Protim Choudhury", email="prantoprotimchoudhury.cse@diu.edu.bd", dept="CSE"
- Student: userId="STU001" (ID: 252-15-869), password="student123", name="Fatin Sharhad", email="252-15-869@diu.edu.bd", dept="CSE", semester=5

### StudentService.java
```java
public static StudentService getInstance()
public void addStudent(Student student) throws DuplicateStudentIdException
public void updateStudent(Student student)
public void deleteStudent(String studentId)
public Student getStudentById(String studentId)
public List<Student> getAllStudents()
public List<Student> searchStudents(String query) // searches by id, name, email
public List<Student> filterByDepartment(String departmentId)
public List<Student> filterBySemester(int semester)
```

### CourseService.java
```java
public static CourseService getInstance()
public void addCourse(Course course)
public void updateCourse(Course course)
public void deleteCourse(String courseCode)
public Course getCourseByCode(String courseCode)
public List<Course> getAllCourses()
public List<Course> getCoursesByDepartment(String departmentId)
public List<Course> getCoursesBySemester(int semester)
public void enrollStudent(String courseCode, String studentId)
public void removeStudentFromCourse(String courseCode, String studentId)
public void assignTeacher(String courseCode, String teacherId)
// Also manages departments:
public void addDepartment(Department dept)
public void updateDepartment(Department dept)
public List<Department> getAllDepartments()
public Department getDepartmentById(String deptId)
```

### ExamService.java
```java
public static ExamService getInstance()
public void createExam(Exam exam) throws ScheduleConflictException
public void updateExam(Exam exam) throws ScheduleConflictException
public void cancelExam(String examId)
public Exam getExamById(String examId)
public List<Exam> getAllExams()
public List<Exam> getExamsByCourse(String courseCode)
public List<Exam> getExamsBySemester(int semester)
public List<Exam> getScheduledExams()
public boolean checkRoomConflict(String roomId, String date, String startTime, String endTime, String excludeExamId)
public boolean checkInvigilatorConflict(String invigilatorId, String date, String startTime, String endTime, String excludeExamId)
```

### SeatAllocationService.java
```java
public static SeatAllocationService getInstance()
public void addRoom(ExamRoom room)
public void updateRoom(ExamRoom room)
public List<ExamRoom> getAllRooms()
public ExamRoom getRoomById(String roomId)
public void allocateSeats(String examId, String roomId, List<String> studentIds)
public List<SeatAssignment> getSeatsByExam(String examId)
public int getAvailableSeats(String roomId, String examId)
public void removeAllocation(String examId)
```

### AttendanceService.java
```java
public static AttendanceService getInstance()
public void markAttendance(AttendanceRecord record)
public void updateAttendance(String attendanceId, String newStatus)
public List<AttendanceRecord> getAttendanceByExam(String examId)
public AttendanceRecord getAttendance(String examId, String studentId)
public Map<String, Integer> getAttendanceStats(String examId) // {PRESENT: n, ABSENT: n, LATE: n}
public List<AttendanceRecord> getAttendanceByStudent(String studentId)
```

### MarksService.java
```java
public static MarksService getInstance()
public void enterMarks(MarkRecord record) throws InvalidMarksException
public void updateMarks(MarkRecord record) throws InvalidMarksException
public MarkRecord getMarks(String courseCode, String studentId)
public List<MarkRecord> getMarksByCourse(String courseCode)
public List<MarkRecord> getMarksByStudent(String studentId)
public void validateMarks(MarkRecord record) throws InvalidMarksException
```

### ResultService.java
```java
public static ResultService getInstance()
public void calculateResults(String courseCode, int semester)
public Result getResult(String studentId, int semester)
public List<Result> getResultsBySemester(int semester)
public void publishResults(int semester, String adminUserId) throws AuthorizationException
public void unpublishResults(int semester, String adminUserId)
public List<Result> getPublishedResults()
public List<Result> getUnpublishedResults()
public Map<String, Object> getResultStats(int semester) // pass%, fail%, avg GPA
```

### ApplicationService.java
```java
public static ApplicationService getInstance()
public void submitApplication(ApplicationRequest app) throws InvalidApplicationException
public void updateApplicationStatus(String appId, String status, String comments) throws AuthorizationException
public ApplicationRequest getApplicationById(String appId)
public List<ApplicationRequest> getAllApplications()
public List<ApplicationRequest> getApplicationsByStudent(String studentId)
public List<ApplicationRequest> getApplicationsByStatus(String status)
public List<ApplicationRequest> getPendingApplications()
```

### ReportService.java
```java
public static ReportService getInstance()
public int getTotalStudents()
public int getTotalExams()
public int getScheduledExams()
public int getPendingResults()
public int getPublishedResults()
public int getPendingApplications()
public double getAttendancePercentage()
public double getPassPercentage()
public double getFailPercentage()
public Map<String, Double> getDepartmentPerformance() // deptId → avg GPA
public Map<String, Double> getCoursePerformance() // courseCode → avg marks
public String generateSummaryReport()
```

---

## 7. UI ARCHITECTURE

### Layout Structure
```
┌──────────────────────────────────────────────────────────────┐
│ TopBarPanel (62px height)                                     │
├──────────┬───────────────────────────────────────────────────┤
│ Sidebar  │ Content Area (CardLayout: "contentCards")          │
│ (256px   │   - "overview"        → DashboardPanel             │
│  or 84px)│   - "students"        → StudentPanel               │
│          │   - "departments"     → CoursePanel                │
│          │   - "examScheduling"  → ExamPanel                  │
│          │   - "rooms"           → RoomPanel                  │
│          │   - "attendance"      → AttendancePanel             │
│          │   - "invigilators"    → InvigilatorPanel            │
│          │   - "marks"           → MarksPanel                 │
│          │   - "results"         → ResultsPanel               │
│          │   - "applications"    → ApplicationsPanel          │
│          │   - "reports"         → ReportsPanel               │
│          │   - "history"         → HistoryPanel               │
│          │   - "users"           → UserManagementPanel        │
│          │   - "settings"        → SettingsPanel              │
└──────────┴───────────────────────────────────────────────────┘
```

### MainFrame.java
```java
public class MainFrame extends JFrame {
    private SidebarPanel sidebar;
    private TopBarPanel topBar;
    private JPanel contentPanel; // uses CardLayout
    private CardLayout cardLayout;
    private User currentUser;
    
    public MainFrame(User currentUser) // sets up the layout
    public void showPanel(String panelName) // switches CardLayout
    public void refreshTheme() // repaints all components with current theme
    public User getCurrentUser()
}
```

The MainFrame constructor receives the logged-in User from LoginFrame.
LoginFrame calls `AuthService.getInstance().login(userId, password)` and on success, disposes itself and creates MainFrame.

### SidebarPanel.java
Uses a JPanel with BoxLayout (Y_AXIS). Sections:
- WORKSPACE: Overview, Student Records, Departments & Courses
- EXAMINATION: Exam Scheduling, Rooms & Seating, Attendance Management, Invigilator Management
- ACADEMIC: Marks & Assessment, Results & Transcripts, Applications & Rechecks
- INSIGHTS: Reports & Analytics, Activity History
- ADMINISTRATION: User & Role Management, System Settings

Each nav item calls `mainFrame.showPanel("cardName")`.
Collapse/expand button toggles between 256px and 84px width.
Role-based visibility: Students see limited nav items, Admin sees all.

### Theme Colors (CRITICAL — both backend ThemeManager and UI must match)
**Light Theme:**
- Background: new Color(0xE9, 0xEE, 0xF8)
- Main Surface: new Color(0xFF, 0xFF, 0xFF)
- Primary Text: new Color(0x0D, 0x16, 0x30)
- Secondary Text: new Color(0x55, 0x61, 0x7C)
- Primary Accent: new Color(0x2F, 0x5C, 0xF0)
- Success: new Color(0x0E, 0x9A, 0x78)
- Warning: new Color(0xC2, 0x7C, 0x0E)
- Danger: new Color(0xBE, 0x26, 0x39)
- Info: new Color(0x15, 0x63, 0xB5)

**Dark Theme:**
- Background: new Color(0x05, 0x0A, 0x18)
- Main Surface: new Color(0x10, 0x1A, 0x36)
- Secondary Surface: new Color(0x0B, 0x13, 0x2A)
- Primary Text: new Color(0xEE, 0xF3, 0xFF)
- Secondary Text: new Color(0x95, 0xA2, 0xC2)
- Primary Accent: new Color(0x3D, 0x6B, 0xFF)
- Success: new Color(0x4F, 0xE0, 0xA0)
- Warning: new Color(0xFB, 0xC3, 0x5E)
- Danger: new Color(0xFF, 0x7D, 0x8D)

### Font: "Segoe UI" fallback to system sans-serif

---

## 8. COMPONENTS (package: com.examsphere.components)

### RoundedPanel.java
Custom JPanel that paints rounded corners with Graphics2D.
```java
public RoundedPanel(int radius, Color bgColor)
public void paintComponent(Graphics g) // uses RoundRectangle2D
```

### StatCard.java
Dashboard KPI card with title, value, subtitle, accent color. Extends RoundedPanel.
```java
public StatCard(String title, String value, String subtitle, Color accentColor)
public void updateValue(String value)
public void updateSubtitle(String subtitle)
```

### DataTablePanel.java
Wraps JTable + JScrollPane with search bar, styled headers, alternating rows.
```java
public DataTablePanel(String[] columns, Object[][] data)
public void setData(Object[][] data)
public void addSearchBar(String placeholder)
public JTable getTable()
public void setTableSelectionListener(ListSelectionListener listener)
public String getSearchText()
```

### CustomButton.java
Styled JButton with rounded corners, hover effects.
```java
public CustomButton(String text, Color bgColor, Color textColor)
public CustomButton(String text) // uses primary accent
// Custom paintComponent for rounded look
```

### FormDialog.java
Base class for modal dialogs with consistent styling.
```java
public FormDialog(JFrame parent, String title, int width, int height)
public JPanel getContentPanel() // area to add form fields
public void addFormField(String label, JComponent field) // adds a labeled row
public JPanel getButtonPanel() // area for OK/Cancel buttons
```

