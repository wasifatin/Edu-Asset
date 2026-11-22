# Inter-Module Integration & Data Flow Specification — EduAsset

## University Examination & Academic Assessment Management System

---

## 1. Integration Architecture Overview

EduAsset integrates presentation, business logic, and persistent storage using a clean **Model-Service-UI** pattern with **Singleton** services and **Observer** event notification.

```
┌────────────────────────────────────────────────────────┐
│                   UI Presentation Layer                │
│    DashboardPanel  StudentPanel  ExamPanel  MarksPanel  │
└───────────────────────────┬────────────────────────────┘
                            │ Synchronous Calls on EDT
                            ▼
┌────────────────────────────────────────────────────────┐
│                 Service Integration Layer              │
│    AuthService ──► StudentService ──► CourseService    │
│         │               │                   │          │
│         ▼               ▼                   ▼          │
│    ExamService ──► MarksService ───► ResultService     │
│         │                                   │          │
│         ▼                                   ▼          │
│    SeatService                         ReportService   │
└───────────────────────────┬────────────────────────────┘
                            │ Read / Write Serialized Objects
                            ▼
┌────────────────────────────────────────────────────────┐
│                   File Storage Layer                   │
│                   FileStorageService                   │
│          ./data/*.dat Binary Object Files              │
└────────────────────────────────────────────────────────┘
```

---

## 2. Core Module Integration Workflows

### 2.1 Examination Scheduling & Conflict Engine Integration

When an administrator schedules a new examination session in `ExamPanel`, the request passes through the `ExamService` conflict engine:

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Coordinator / Admin
    participant UI as ExamPanel
    participant ExamSvc as ExamService
    participant RoomSvc as SeatAllocationService
    participant Storage as FileStorageService

    Admin->>UI: Submit Exam Schedule Form
    UI->>ExamSvc: createExam(Exam)
    ExamSvc->>ExamSvc: Validate Date & Time Format
    ExamSvc->>ExamSvc: checkRoomConflict(roomId, date, start, end)
    alt Room is Booked
        ExamSvc-->>UI: throw ScheduleConflictException("Room occupied")
        UI-->>Admin: Display Warning Alert
    else Room Available
        ExamSvc->>ExamSvc: checkInvigilatorConflict(teacherId, date, start, end)
        alt Invigilator Overlap
            ExamSvc-->>UI: throw ScheduleConflictException("Faculty occupied")
            UI-->>Admin: Display Warning Alert
        else No Conflicts
            ExamSvc->>Storage: saveExams(examsList)
            ExamSvc-->>UI: Success Confirmation
            UI->>RoomSvc: Notify Seat Allocation Ready
            UI-->>Admin: Timetable Updated Successfully
        end
    end
```

---

### 2.2 Continuous Assessment to Official Result Pipeline

The workflow from mark recording to official publication spans three coordinated services:

```mermaid
flowchart TD
    A["1. Teacher inputs component marks in MarksPanel"] --> B["2. MarksService.enterMarks()"]
    B --> C{"3. validateMarks():\nAre scores <= max?"}
    C -- No --> D["Throw InvalidMarksException & Alert UI"]
    C -- Yes --> E["MarkRecord.calculateTotal() & calculateGrade()"]
    E --> F["FileStorageService saves marks.dat"]
    F --> G["4. Controller triggers ResultService.calculateResults()"]
    G --> H["Match enrolled students with Course & Marks records"]
    H --> I["Compute credit-weighted GPA & CGPA"]
    I --> J["Result created with published = FALSE (Draft)"]
    J --> K["5. Controller approves & clicks Publish Batch"]
    K --> L["ResultService.publishResults(semester, 'ADMIN001')"]
    L --> M["Set published = TRUE & timestamp"]
    M --> N["Official Transcripts viewable in ResultsPanel"]
```

---

### 2.3 Student Grievance & Application Lifecycle

```mermaid
stateDiagram-v2
    [*] --> PENDING : Student submits Recheck / Retake in ApplicationsPanel
    PENDING --> UNDER_REVIEW : Coordinator reviews statement & marks record
    UNDER_REVIEW --> APPROVED : Admin grants mark correction / retake slot
    UNDER_REVIEW --> REJECTED : Admin rejects grievance with comments
    APPROVED --> COMPLETED : Record amended in MarksService & verified
    REJECTED --> COMPLETED : Decision officially conveyed to student
    COMPLETED --> [*]
```

1. **Submission:** `ApplicationPanel` calls `ApplicationService.submitApplication(ApplicationRequest)`. Status initialized to `PENDING`.
2. **Review:** Administrative reviewer updates status to `UNDER_REVIEW` and records initial evaluation notes.
3. **Decision:** Reviewer invokes `updateApplicationStatus(appId, "APPROVED", comments)`.
4. **Resolution:** If approved, course instructor updates marks in `MarksService`, which triggers automated GPA recalculation in `ResultService`.

---

## 3. UI-to-Service Integration Patterns

All Swing user interface panels integrate with the business layer using standard patterns:

1. **Singleton Access:** UI components obtain service references via static factory methods:
   ```java
   private final ExamService examService = ExamService.getInstance();
   private final CourseService courseService = CourseService.getInstance();
   ```
2. **Observer Pattern for Theming:** Every panel registers a callback with `ThemeManager`:
   ```java
   theme.addThemeChangeListener(this::refreshTheme);
   ```
   When the user toggles Light/Dark mode in `TopBarPanel` or `SettingsPanel`, `ThemeManager` notifies all active panels to repaint with the new palette.

---

## 4. Multi-Threading & Concurrency Rules

1. **Event Dispatch Thread (EDT) Integrity:** All Swing component construction, button event handlers, and table updates run on the EDT (`javax.swing.SwingUtilities.invokeLater`).
2. **Background File I/O (`SwingWorker`):** Lengthy file generation and statistical aggregations run on background worker threads:
   ```java
   SwingWorker<Void, Void> worker = new SwingWorker<>() {
       @Override
       protected Void doInBackground() throws Exception {
           String text = reportService.generateSummaryReport();
           try (FileWriter writer = new FileWriter(targetFile)) {
               writer.write(text);
           }
           return null;
       }
       @Override
       protected void done() {
           JOptionPane.showMessageDialog(mainFrame, "Export complete!");
       }
   };
   worker.execute();
   ```
3. **Synchronized Critical Sections:** Service mutation methods (`enterMarks`, `createExam`, `publishResults`) are marked `synchronized` to ensure thread safety across concurrent operations.

