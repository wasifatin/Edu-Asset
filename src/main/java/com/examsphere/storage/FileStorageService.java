package com.examsphere.storage;

import com.examsphere.model.*;
import com.examsphere.util.Constants;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton file storage manager providing Java Object Serialization persistence.
 * Reads and writes lists of domain model entities to binary .dat files with robust
 * corrupted file handling, graceful defaults, and automated backups.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class FileStorageService {

    private static FileStorageService instance;
    private final String dataDir;

    private FileStorageService() {
        this.dataDir = Constants.DATA_DIR;
        initializeDataDirectory();
    }

    public static synchronized FileStorageService getInstance() {
        if (instance == null) {
            instance = new FileStorageService();
        }
        return instance;
    }

    /**
     * Ensures the data storage directory exists.
     */
    public void initializeDataDirectory() {
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Serializes a list of objects to a binary file in the data directory.
     *
     * @param list     list of objects to serialize
     * @param filename base filename (with or without .dat)
     * @param <T>      entity type
     */
    public synchronized <T> void saveList(List<T> list, String filename) {
        String cleanName = filename.endsWith(".dat") ? filename : filename + ".dat";
        File file = new File(dataDir, cleanName);
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(new ArrayList<>(list));
        } catch (IOException e) {
            System.err.println("Error saving data to " + file.getAbsolutePath() + ": " + e.getMessage());
        }
    }

    /**
     * Deserializes a list of objects from a binary file in the data directory.
     * Returns an empty list if the file is missing or corrupted.
     *
     * @param filename base filename (with or without .dat)
     * @param <T>      entity type
     * @return deserialized list or an empty list
     */
    @SuppressWarnings("unchecked")
    public synchronized <T> List<T> loadList(String filename) {
        String cleanName = filename.endsWith(".dat") ? filename : filename + ".dat";
        File file = new File(dataDir, cleanName);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(file)))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                return new ArrayList<>((List<T>) obj);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not read " + file.getAbsolutePath() + " (corrupted or incompatible). Returning empty list.");
        }
        return new ArrayList<>();
    }

    // ─── Entity Convenience Methods ─────────────────────────────────────

    public void saveUsers(List<User> users) {
        saveList(users, "users.dat");
    }

    public List<User> loadUsers() {
        return loadList("users.dat");
    }

    public void saveDepartments(List<Department> departments) {
        saveList(departments, "departments.dat");
    }

    public List<Department> loadDepartments() {
        return loadList("departments.dat");
    }

    public void saveCourses(List<Course> courses) {
        saveList(courses, "courses.dat");
    }

    public List<Course> loadCourses() {
        return loadList("courses.dat");
    }

    public void saveExams(List<Exam> exams) {
        saveList(exams, "exams.dat");
    }

    public List<Exam> loadExams() {
        return loadList("exams.dat");
    }

    public void saveRooms(List<ExamRoom> rooms) {
        saveList(rooms, "rooms.dat");
    }

    public List<ExamRoom> loadRooms() {
        return loadList("rooms.dat");
    }

    public void saveAttendance(List<AttendanceRecord> records) {
        saveList(records, "attendance.dat");
    }

    public List<AttendanceRecord> loadAttendance() {
        return loadList("attendance.dat");
    }

    public void saveMarks(List<MarkRecord> marks) {
        saveList(marks, "marks.dat");
    }

    public List<MarkRecord> loadMarks() {
        return loadList("marks.dat");
    }

    public void saveResults(List<Result> results) {
        saveList(results, "results.dat");
    }

    public List<Result> loadResults() {
        return loadList("results.dat");
    }

    public void saveApplications(List<ApplicationRequest> applications) {
        saveList(applications, "applications.dat");
    }

    public List<ApplicationRequest> loadApplications() {
        return loadList("applications.dat");
    }

    public void saveSeatAssignments(List<SeatAssignment> seatAssignments) {
        saveList(seatAssignments, "seat_assignments.dat");
    }

    public List<SeatAssignment> loadSeatAssignments() {
        return loadList("seat_assignments.dat");
    }

    /**
     * Copies all persisted data files into a timestamped backup directory.
     *
     * @param backupDir target backup directory path
     */
    public synchronized boolean backupData(String backupDir) {
        try {
            File srcDir = new File(dataDir);
            if (!srcDir.exists()) return false;

            File destDir = new File(backupDir);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            File[] files = srcDir.listFiles((dir, name) -> name.endsWith(".dat"));
            if (files != null) {
                for (File file : files) {
                    Path destPath = Paths.get(destDir.getAbsolutePath(), file.getName());
                    Files.copy(file.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            return true;
        } catch (IOException e) {
            System.err.println("Backup failed: " + e.getMessage());
            return false;
        }
    }
}

