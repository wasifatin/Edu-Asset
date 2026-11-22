package com.examsphere.service;

import com.examsphere.model.AttendanceRecord;
import com.examsphere.storage.FileStorageService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service managing examination attendance tracking, late arrival records,
 * and attendance statistical reporting.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class AttendanceService {

    private static AttendanceService instance;

    private final FileStorageService storage;
    private final List<AttendanceRecord> records;

    private AttendanceService() {
        this.storage = FileStorageService.getInstance();
        this.records = new ArrayList<>(storage.loadAttendance());
        // Clean initial state: users input their own attendance records
    }

    public static synchronized AttendanceService getInstance() {
        if (instance == null) {
            instance = new AttendanceService();
        }
        return instance;
    }

    private void seedInitialAttendance() {
        AttendanceRecord ar1 = new AttendanceRecord("ATT-001", "EX-2026-001", "STU001", "PRESENT");
        AttendanceRecord ar2 = new AttendanceRecord("ATT-002", "EX-2026-001", "STU002", "PRESENT");
        records.add(ar1);
        records.add(ar2);
        storage.saveAttendance(records);
    }

    public synchronized void markAttendance(AttendanceRecord record) {
        if (record == null || record.getExamId() == null || record.getStudentId() == null) return;

        // Prevent duplicate records for the same student and examination
        for (int i = 0; i < records.size(); i++) {
            AttendanceRecord existing = records.get(i);
            if (existing.getExamId().equalsIgnoreCase(record.getExamId()) &&
                existing.getStudentId().equalsIgnoreCase(record.getStudentId())) {
                existing.setStatus(record.getStatus());
                existing.setRemarks(record.getRemarks());
                existing.setTimestamp(LocalDateTime.now().toString());
                storage.saveAttendance(records);
                return;
            }
        }

        records.add(record);
        storage.saveAttendance(records);
    }

    public synchronized void updateAttendance(String attendanceId, String newStatus) {
        if (attendanceId == null || newStatus == null) return;
        for (AttendanceRecord r : records) {
            if (r.getAttendanceId().equalsIgnoreCase(attendanceId)) {
                r.setStatus(newStatus.toUpperCase());
                r.setTimestamp(LocalDateTime.now().toString());
                storage.saveAttendance(records);
                return;
            }
        }
    }

    public synchronized List<AttendanceRecord> getAttendanceByExam(String examId) {
        if (examId == null) return new ArrayList<>();
        return records.stream()
                .filter(r -> examId.equalsIgnoreCase(r.getExamId()))
                .collect(Collectors.toList());
    }

    public synchronized AttendanceRecord getAttendance(String examId, String studentId) {
        if (examId == null || studentId == null) return null;
        for (AttendanceRecord r : records) {
            if (r.getExamId().equalsIgnoreCase(examId) && r.getStudentId().equalsIgnoreCase(studentId)) {
                return r;
            }
        }
        return null;
    }

    public synchronized List<AttendanceRecord> getAttendanceByStudent(String studentId) {
        if (studentId == null) return new ArrayList<>();
        return records.stream()
                .filter(r -> studentId.equalsIgnoreCase(r.getStudentId()))
                .collect(Collectors.toList());
    }

    public synchronized Map<String, Integer> getAttendanceStats(String examId) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("PRESENT", 0);
        stats.put("ABSENT", 0);
        stats.put("LATE", 0);

        List<AttendanceRecord> list = getAttendanceByExam(examId);
        for (AttendanceRecord r : list) {
            String status = r.getStatus() != null ? r.getStatus().toUpperCase() : "ABSENT";
            stats.put(status, stats.getOrDefault(status, 0) + 1);
        }
        return stats;
    }

    public synchronized List<AttendanceRecord> getAllRecords() {
        return new ArrayList<>(records);
    }
}

