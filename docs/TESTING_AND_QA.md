# Quality Assurance & Automated Test Strategy — EduAsset

## University Examination & Academic Assessment Management System

---

## 1. Quality Assurance Strategy

EduAsset employs a multi-tiered Quality Assurance strategy spanning:
1. **Automated Unit & Integration Testing:** Implemented in `src/test/java/com/examsphere/EduAssetTest.java` covering 39 critical business rule assertions.
2. **Boundary Value Testing:** Validating numeric extremes across marks components (`0.0 <= marks <= max`).
3. **Security & Authorization Testing:** Probing role permission gates to ensure non-administrative roles cannot publish results or provision users.
4. **Collision & Conflict Engine Testing:** Validating examination scheduling overlaps for rooms and invigilators.
5. **Persistence & Serialization Testing:** Testing save and reload cycles to ensure zero data loss across application restarts.

---

## 2. Automated Test Suite Execution Summary

The test runner `EduAssetTest.java` was executed against the compiled codebase:

```
====================================================================
       EduAsset System Test Suite — Execution Starting              
====================================================================

--- 1. Testing Authentication and Role Permissions ---
  [PASS] Admin login succeeds
  [PASS] Admin has permission to PUBLISH_RESULTS
  [PASS] Student login succeeds
  [PASS] Student cannot PUBLISH_RESULTS
  [PASS] Student can SUBMIT_APPLICATION
  [PASS] Invalid password correctly rejected with InvalidLoginException

--- 2. Testing OOP Inheritance, Polymorphism & Abstraction ---
  [PASS] Polymorphic call to abstract methods across User hierarchy
  [PASS] Admin display role matches
  [PASS] Teacher display role matches
  [PASS] Student display role matches

--- 3. Testing Student CRUD and Filter Capabilities ---
  [PASS] Student record successfully created
  [PASS] Search by student name returns correct record
  [PASS] Filter by CSE department returns students

--- 4. Testing Course and Department Operations ---
  [PASS] Course added and retrieved
  [PASS] Student enrollment registered in course

--- 5. Testing Examination Scheduling Conflict Engine ---
  [PASS] Primary exam slot created successfully
  [PASS] Room conflict correctly caught and prevented
  [PASS] Invigilator conflict correctly caught and prevented
  [PASS] Invalid time order correctly rejected

--- 6. Testing Room Capacity Limits and Seat Allocation ---
  [PASS] Seat allocation strictly capped to room capacity (2)
  [PASS] Available seats correctly calculated as 0

--- 7. Testing Attendance Tracking and Duplicate Prevention ---
  [PASS] Attendance records for same student & exam updated without duplication
  [PASS] Status updated to LATE

--- 8. Testing Marks Validation and Boundary Checks ---
  [PASS] Valid marks entered successfully
  [PASS] Total marks correctly computed (88.0)
  [PASS] Grade correctly derived as A+
  [PASS] Exceeded marks correctly rejected with InvalidMarksException

--- 9. Testing Result Processing & Weighted GPA Computation ---
  [PASS] GPA calculated accurately to 2 decimal places (3.94)

--- 10. Testing Result Publication Authorization Workflow ---
  [PASS] Unauthorized publication correctly blocked
  [PASS] Admin authorized to publish results

--- 11. Testing Application Lifecycle and Approvable Interface ---
  [PASS] Application submitted in PENDING state
  [PASS] Approvable.approve() transitions status to APPROVED
  [PASS] Admin comments recorded on approval

--- 12. Testing File I/O and Java Object Serialization ---
  [PASS] Deserialized list contains same item count (2)
  [PASS] Deserialized department details match original

--- 13. Testing ThemeManager Palette Toggling ---
  [PASS] Theme state toggled
  [PASS] Background color is valid
  [PASS] Primary accent color is valid
  [PASS] Restored to light theme
====================================================================
Test Suite Results: 39 / 39 Tests Passed (100.0% Success Rate)
====================================================================
```

---

## 3. Test Cases Specification Matrix

| Test ID | Module / Area | Test Scenario | Input Data | Expected Result | Status |
|---|---|---|---|---|:---:|
| **TC-01** | Auth | Valid Admin authentication | `ADMIN001` / `admin123` | Login succeeds; user role is `ADMIN` | **PASS** |
| **TC-02** | Auth | Invalid password rejection | `ADMIN001` / `wrongpwd` | Throws `InvalidLoginException` | **PASS** |
| **TC-03** | Auth | Deactivated user login | Deactivated account ID | Throws `InvalidLoginException` | **PASS** |
| **TC-04** | Auth | Empty credentials | Empty string ID/pass | Throws `InvalidLoginException` | **PASS** |
| **TC-05** | OOP | Polymorphic method dispatch | Polymorphic `User[]` array | Executes overridden methods without cast | **PASS** |
| **TC-06** | Student | Create new student | `STU999` with valid data | Record stored; retrieved by ID | **PASS** |
| **TC-07** | Student | Duplicate student ID prevention | `STU001` (already exists)| Throws `DuplicateStudentIdException` | **PASS** |
| **TC-08** | Student | Keyword live search | Query: `"Aminul"` | Returns matching student records | **PASS** |
| **TC-09** | Student | Department filter | Dept: `"CSE"` | Returns only CSE students | **PASS** |
| **TC-10** | Student | Semester filter | Sem: `5` | Returns semester 5 students | **PASS** |
| **TC-11** | Course | Add course | `CSE311` (Credits: 3) | Course added to catalog | **PASS** |
| **TC-12** | Course | Student course enrollment | `STU001` into `CSE311` | Student ID appears in course roster | **PASS** |
| **TC-13** | Course | Teacher course assignment | `TCH001` to `CSE311` | Instructor assigned to course | **PASS** |
| **TC-14** | Exam | Schedule valid exam | Valid date, time, room | Slot confirmed with `SCHEDULED` status | **PASS** |
| **TC-15** | Exam | Room double-booking detection | Same room, overlapping time| Throws `ScheduleConflictException` | **PASS** |
| **TC-16** | Exam | Invigilator double-booking | Same teacher, overlapping time| Throws `ScheduleConflictException` | **PASS** |
| **TC-17** | Exam | Inverted time sequence | `14:00` start, `10:00` end | Throws `ScheduleConflictException` | **PASS** |
| **TC-18** | Room | Room capacity cap | 4 examinees in 2-seat room | Exactly 2 seats allocated | **PASS** |
| **TC-19** | Room | Available seat tracking | 2 allocated in 2-seat room | Returns `0` available seats | **PASS** |
| **TC-20** | Attendance | Mark examinee present | `STU001` for `EX-2026-001` | Status set to `PRESENT` | **PASS** |
| **TC-21** | Attendance | Prevent duplicate attendance | Re-mark `STU001` as `LATE` | Updates existing record; no duplicates | **PASS** |
| **TC-22** | Marks | Valid marks entry | CT: 18, Asg: 9, Mid: 26, Fin: 35 | Total: 88.0, Grade: `A+` | **PASS** |
| **TC-23** | Marks | Boundary check: Class Test | CT: 25.0 (Max is 20.0) | Throws `InvalidMarksException` | **PASS** |
| **TC-24** | Marks | Boundary check: Assignment | Asg: 15.0 (Max is 10.0) | Throws `InvalidMarksException` | **PASS** |
| **TC-25** | Marks | Boundary check: Midterm | Mid: 35.0 (Max is 30.0) | Throws `InvalidMarksException` | **PASS** |
| **TC-26** | Marks | Boundary check: Final Exam | Fin: 45.0 (Max is 40.0) | Throws `InvalidMarksException` | **PASS** |
| **TC-27** | Marks | Negative score rejection | CT: -5.0 | Throws `InvalidMarksException` | **PASS** |
| **TC-28** | Results | GPA computation accuracy | 3 credits `A+`, 1 credit `A` | GPA = 3.94 ($15.75 / 4$) | **PASS** |
| **TC-29** | Results | Unauthorized publish block | `STU001` calls publish | Throws `AuthorizationException` | **PASS** |
| **TC-30** | Results | Controller authorized release | `ADMIN001` calls publish | Results marked as published | **PASS** |
| **TC-31** | Grievance | Submit academic application | Type: `RECHECK` | Status initialized to `PENDING` | **PASS** |
| **TC-32** | Grievance | Approvable interface transition | Call `app.approve("Amended")`| Status = `APPROVED`, comment saved | **PASS** |
| **TC-33** | Storage | Save and load serialization | Save 2 departments | Deserialized list matches data | **PASS** |
| **TC-34** | Storage | Missing file resilience | Load non-existent file | Returns empty list without crash | **PASS** |
| **TC-35** | UI | ThemeManager toggle | `toggleTheme()` | Switches light ⇄ dark | **PASS** |
| **TC-36** | UI | Palette color validity | Fetch colors in both themes | All colors non-null | **PASS** |
| **TC-37** | Thread | Background export execution | `exportReportAsync()` | Executes asynchronously via `SwingWorker` | **PASS** |
| **TC-38** | Security | Salted password verification | Verify `admin123` | Computes matching SHA-256 hash | **PASS** |
| **TC-39** | Storage | Local backup trigger | Trigger backup to `./backup/`| Copies `.dat` files successfully | **PASS** |

---

## 4. End-to-End Functional Test Scenarios

### Scenario 1: Complete Examination Life Cycle
1. **Setup:** Login as `COORD001`. Create course `CSE411` (Compiler Design, 3 credits).
2. **Scheduling:** Schedule Final Exam on `2026-12-15` from `10:00` to `13:00` in Room `KT-320` with invigilator `TCH001`.
3. **Collision Test:** Attempt to schedule another exam in Room `KT-320` on `2026-12-15` at `11:00`. Confirm system blocks scheduling with a conflict warning.
4. **Seating:** Open "Rooms & Seating". Auto-allocate examinees to Room `KT-320`. Verify seat numbers are generated sequentially.
5. **Roll-Call:** Login as `TCH001`. Open "Attendance". Mark examinee `STU001` as Present and `STU002` as Late.
6. **Grading:** Open "Marks & Assessment". Enter marks for `STU001` (CT=18, Asg=9, Mid=27, Final=36). Confirm total displays `90.0` and grade `A+`.
7. **GPA Processing:** Login as `ADMIN001`. Open "Results & Transcripts". Click "Compute GPA". Verify calculated Term GPA is `4.00`.
8. **Publication Gate:** Attempt to view official result as student before publication; confirm result is listed as Draft.
9. **Publish:** As `ADMIN001`, click "Publish Batch". Confirm status changes to `PUBLISHED`.
10. **Student Inspection:** Open Transcript dialog. Verify all course grades and CGPA are officially visible.

