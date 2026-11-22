# Setup, Installation & Initialization Guide — EduAsset

## University Examination & Academic Assessment Management System

---

## 1. Prerequisites & Environment Requirements

| Requirement | Supported Version | Notes |
|---|---|---|
| **Operating System** | Windows 10 / 11, macOS 11+, Ubuntu 20.04+ | Cross-platform desktop application |
| **Java Development Kit (JDK)** | **JDK 17 or higher** | Verified on Oracle JDK 26 and OpenJDK 21 |
| **Java Environment Variables** | `JAVA_HOME` pointing to JDK, `PATH` containing `%JAVA_HOME%\bin` | Required for command-line compilation |
| **IDE Compatibility** | Apache NetBeans 15+, IntelliJ IDEA 2022+, VS Code | Standard Java Project (no proprietary IDE plugins needed) |
| **Build Tools** | Native `javac` / `jar` (No Gradle or Maven installation required) | Pure standard library implementation |

---

## 2. Command-Line Setup & Compilation

### Step 1: Verify Java Installation
Open your terminal (PowerShell, Command Prompt, or Bash) and execute:
```bash
javac -version
java -version
```
*Ensure the reported version is 17 or above.*

### Step 2: Clone or Open Project Directory
Navigate to the root directory containing `src/`:
```bash
cd "d:\Programming\Antigravity IDE\EduAsset"
```

### Step 3: Compile Source Code
Compile all Java source files into an output directory `out`:
```powershell
# Windows PowerShell
javac -d out -sourcepath src/main/java (Get-ChildItem -Path src/main/java -Recurse -Filter "*.java").FullName

# Linux / macOS Bash
javac -d out -sourcepath src/main/java $(find src/main/java -name "*.java")
```

### Step 4: Run Application
Execute the compiled application with the classpath set to `out`:
```powershell
java -cp out com.examsphere.Main
```

### Step 5: Execute Automated Test Suite (39 Tests)
```powershell
# Compile test suite
javac -d out -sourcepath "src/main/java;src/test/java" src/test/java/com/examsphere/EduAssetTest.java

# Run test suite
java -cp out com.examsphere.EduAssetTest
```

---

## 3. Packaging into a Standalone Executable JAR

To package EduAsset into a single, portable `.jar` file:
```powershell
# Create runnable JAR with main class com.examsphere.Main
jar cfe EduAsset.jar com.examsphere.Main -C out .

# Run the packaged JAR directly
java -jar EduAsset.jar
```

---

## 4. IDE Configuration Guides

### 4.1 Apache NetBeans IDE
1. Launch Apache NetBeans.
2. Select **File** → **New Project...** → **Java with Ant** → **Java Application with Existing Sources**.
3. Set Project Name to `EduAsset` and specify the folder location.
4. In the **Source Package Folders** step, add `src/main/java` and `src/test/java`.
5. Set Main Class to `com.examsphere.Main`.
6. Click **Finish**, then press **F6** (Run Project).

### 4.2 IntelliJ IDEA
1. Launch IntelliJ IDEA.
2. Select **Open** and choose the `EduAsset` root folder.
3. Open **File** → **Project Structure...** (Ctrl+Alt+Shift+S).
4. Under **Project**, verify Project SDK is set to JDK 17+.
5. Under **Modules**, mark `src/main/java` as **Sources** and `src/test/java` as **Tests**.
6. Locate `src/main/java/com/examsphere/Main.java`, right-click, and select **Run 'Main.main()'**.

### 4.3 Visual Studio Code
1. Install the official **Extension Pack for Java** by Microsoft.
2. Open the `EduAsset` workspace folder.
3. Open `src/main/java/com/examsphere/Main.java`.
4. Click the **Run** button located above `public static void main(String[] args)`.

---

## 5. Automatic Data Initialization & Demo Accounts

On the very first launch, `FileStorageService` checks if persistent `.dat` files exist in `./data/`. If the directory is empty, the system automatically initializes default accounts, departments, courses, examination rooms, and schedules:

### Pre-Configured Demonstration Accounts

| Role / Designation | Full Name | Institutional Email | User ID | Password | Department / Role Scope |
|---|---|---|---|---|---|
| **Examination Controller** | **Mr. Md. Ashraful Islam Talukder** | `ashrafulislam.cse@diu.edu.bd` | `ADMIN001` | `admin123` | Office of the Controller • Full Administrative Access |
| **Department Coordinator** | **Dewan Mamun Raza** | `dewan.cse@diu.edu.bd` | `COORD001` | `coord123` | Computer Science & Engineering • Curriculums & Schedules |
| **Faculty / Invigilator** | **Pranto Protim Choudhury** | `prantoprotimchoudhury.cse@diu.edu.bd` | `TCH001` | `teach123` / `teacher123` | Computer Science & Engineering • Marks Entry & Roll Call |
| **Student Examinee** | **Fatin Sharhad** | `252-15-869@diu.edu.bd` | `STU001` / `252-15-869` | `student123` | CSE (Semester 5) • Grades & Grievances |
| **Student (Alternative)** | **Nusrat Jahan** | `nusrat.stu@examsphere.diu.edu.bd` | `STU002` | `student123` | CSE (Semester 5) • Demo Student |

---

## 6. Data Backup & Reset Procedures

### Creating a Snapshot Backup
1. Inside the application, navigate to **Settings** in the sidebar.
2. Click **Trigger Immediate Local Data Backup**.
3. All binary `.dat` files from `./data/` are copied to `./backup/`.

### Resetting to Fresh Factory State
If you wish to clear all modified data and restore default demo records:
1. Close the running EduAsset application.
2. Delete the contents of the `./data/` directory (delete all `.dat` files).
3. Relaunch the application. The system will detect missing records and reinitialize all default records cleanly.

---

## 7. Troubleshooting Common Issues

| Symptom / Error | Root Cause | Solution |
|---|---|---|
| `javac: command not found` | Java JDK is not in system `PATH`. | Ensure JDK 17+ is installed and add `C:\Program Files\Java\jdk-<version>\bin` to your system environment variables. |
| `UnsupportedClassVersionError` | Compiled on newer JDK, running on older JRE. | Ensure both `javac` and `java` point to the same JDK version. |
| Window appears oversized | Desktop display scaling set to 150%+. | Minimum resolution is 1100 × 700 px; adjust display scaling or maximize the window. |
| Corrupted data warning in console | Outdated `.dat` file after model schema update. | Delete `./data/*.dat` to let the system generate fresh serialized files. |

