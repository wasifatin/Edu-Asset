# System Deployment & Distribution Specification — EduAsset

## University Examination & Academic Assessment Management System

---

## 1. Deployment Architecture

EduAsset is designed as a self-contained, zero-dependency Java desktop application distributed either as source code or as a standalone executable JAR archive (`EduAsset.jar`).

```
Deployment Distribution Package:
EduAsset-v1.0.0-Release/
├── EduAsset.jar             # Executable JAR archive containing all compiled classes
├── run.bat                  # One-click Windows launch script
├── run.sh                   # One-click Linux / macOS launch script
├── README.md                # System overview and quick-start instructions
├── docs/                    # Complete architectural & operational documentation
└── data/                    # Local directory for serialized object storage (*.dat)
```

---

## 2. Compilation & Packaging Instructions

### 2.1 Compiling Source Code
From the project root directory:
```powershell
# Windows PowerShell
javac -d out -sourcepath src/main/java (Get-ChildItem -Path src/main/java -Recurse -Filter "*.java").FullName

# Linux / macOS Bash
javac -d out -sourcepath src/main/java $(find src/main/java -name "*.java")
```

### 2.2 Packaging Executable JAR
Package the compiled classes in `out/` into `EduAsset.jar` with entry point `com.examsphere.Main`:
```bash
jar cfe EduAsset.jar com.examsphere.Main -C out .
```

Verify JAR contents:
```bash
jar tf EduAsset.jar
```
*Confirm that `META-INF/MANIFEST.MF` specifies `Main-Class: com.examsphere.Main`.*

---

## 3. One-Click Launch Scripts

### 3.1 Windows Batch Script (`run.bat`)
Create a file named `run.bat` in the project root:
```batch
@echo off
title EduAsset - University Examination Management System
echo Launching EduAsset Desktop Application...
java -jar EduAsset.jar
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Application failed to launch. Ensure Java 17+ is installed and on your PATH.
    pause
)
```

### 3.2 Linux / macOS Shell Script (`run.sh`)
Create a file named `run.sh` in the project root:
```bash
#!/bin/bash
echo "Launching EduAsset Desktop Application..."
java -jar EduAsset.jar
if [ $? -ne 0 ]; then
    echo "[ERROR] Launch failed. Please verify JDK 17+ is installed."
fi
```
Make executable:
```bash
chmod +x run.sh
```

---

## 4. Cross-Platform Desktop Deployment

### Windows 10 & 11
- Ensure Oracle JDK or OpenJDK 17+ is installed.
- Double-click `run.bat` or `EduAsset.jar`.
- If high-DPI scaling causes sizing issues, right-click `EduAsset.jar` → Properties → Compatibility → Change high DPI settings → Override high DPI scaling.

### macOS (Apple Silicon & Intel)
- Install OpenJDK (e.g. via Homebrew: `brew install openjdk@17`).
- Launch via terminal:
  ```bash
  java -jar EduAsset.jar
  ```
- macOS menu bar integrates with Swing menus automatically.

### Linux (Ubuntu / Fedora / Debian)
- Install default JDK:
  ```bash
  sudo apt-get install openjdk-17-jdk
  ```
- Run:
  ```bash
  java -jar EduAsset.jar
  ```

---

## 5. Local Data Storage & Backup Management

- Serialized application records are stored under `./data/` relative to the current working directory.
- For multi-user workstations, ensure the executing user account possesses read/write filesystem permissions for the application folder.
- **Automated Backup Procedure:**
  1. Triggering backup via the UI copies `./data/*.dat` to `./backup/`.
  2. For automated OS-level backups, schedule a daily script:
     ```powershell
     # Windows Task Scheduler PowerShell task
     Copy-Item -Path ".\data\*.dat" -Destination ".\backup\daily-$(Get-Date -Format 'yyyyMMdd')\" -Recurse -Force
     ```

---

## 6. Hardware & Environment Sizing Guidelines

| Parameter | Recommended Specification | Minimum Specification |
|---|---|---|
| **CPU** | Dual-Core 2.0 GHz or higher | Single-Core 1.5 GHz |
| **RAM** | 1 GB available system RAM | 512 MB |
| **Storage** | 100 MB free disk space | 30 MB |
| **Display Resolution**| 1440 × 900 px | 1100 × 700 px |
| **JDK Version** | Java SE 17, 21, or 26 | Java SE 17 LTS |

