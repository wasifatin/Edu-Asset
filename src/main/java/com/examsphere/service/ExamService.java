package com.examsphere.service;

import com.examsphere.exception.ScheduleConflictException;
import com.examsphere.model.Exam;
import com.examsphere.storage.FileStorageService;
import com.examsphere.util.ValidationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing examination scheduling, lifecycle statuses, and automatic conflict detection
 * for examination venues and invigilators.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ExamService {

    private static ExamService instance;

    private final FileStorageService storage;
    private final List<Exam> exams;

    private ExamService() {
        this.storage = FileStorageService.getInstance();
        this.exams = new ArrayList<>(storage.loadExams());
        // Clean initial state: users input their own examination slots
    }

    public static synchronized ExamService getInstance() {
        if (instance == null) {
            instance = new ExamService();
        }
        return instance;
    }

    private void seedInitialExams() {
        Exam e1 = new Exam("EX-2026-001", "CSE221", "MIDTERM", 5, "2026-11-20", "10:00", "12:00");
        e1.setRoomId("KT-320");
        e1.setInvigilatorId("TCH001");
        e1.setStatus("SCHEDULED");

        Exam e2 = new Exam("EX-2026-002", "CSE223", "MIDTERM", 5, "2026-11-22", "14:00", "16:00");
        e2.setRoomId("KT-321");
        e2.setInvigilatorId("TCH001");
        e2.setStatus("SCHEDULED");

        Exam e3 = new Exam("EX-2026-003", "SWE211", "FINAL", 4, "2026-11-25", "10:00", "13:00");
        e3.setRoomId("AB-402");
        e3.setStatus("SCHEDULED");

        exams.add(e1);
        exams.add(e2);
        exams.add(e3);
        storage.saveExams(exams);
    }

    public synchronized void createExam(Exam exam) throws ScheduleConflictException {
        if (exam == null || exam.getExamId() == null) {
            throw new IllegalArgumentException("Exam information cannot be null.");
        }

        if (getExamById(exam.getExamId()) != null) {
            throw new ScheduleConflictException("Duplicate examination ID: '" + exam.getExamId() + "' already exists.");
        }

        if (!ValidationUtil.isValidDate(exam.getExamDate())) {
            throw new ScheduleConflictException("Invalid examination date format. Use YYYY-MM-DD.");
        }

        if (!ValidationUtil.isValidTime(exam.getStartTime()) || !ValidationUtil.isValidTime(exam.getEndTime())) {
            throw new ScheduleConflictException("Invalid time format. Use HH:mm (24-hour format).");
        }

        if (exam.getStartTime().compareTo(exam.getEndTime()) >= 0) {
            throw new ScheduleConflictException("Exam starting time must precede ending time.");
        }

        // Room conflict check
        if (exam.getRoomId() != null && !exam.getRoomId().trim().isEmpty()) {
            if (checkRoomConflict(exam.getRoomId(), exam.getExamDate(), exam.getStartTime(), exam.getEndTime(), null)) {
                throw new ScheduleConflictException("Room Conflict: Room '" + exam.getRoomId() +
                        "' is already occupied on " + exam.getExamDate() + " during " + exam.getStartTime() + " - " + exam.getEndTime() + ".");
            }
        }

        // Invigilator conflict check
        if (exam.getInvigilatorId() != null && !exam.getInvigilatorId().trim().isEmpty()) {
            if (checkInvigilatorConflict(exam.getInvigilatorId(), exam.getExamDate(), exam.getStartTime(), exam.getEndTime(), null)) {
                throw new ScheduleConflictException("Invigilator Conflict: Faculty '" + exam.getInvigilatorId() +
                        "' is already assigned to another examination at that time.");
            }
        }

        exams.add(exam);
        storage.saveExams(exams);
    }

    public synchronized void updateExam(Exam exam) throws ScheduleConflictException {
        if (exam == null || exam.getExamId() == null) return;

        // Room conflict check excluding current exam
        if (exam.getRoomId() != null && !exam.getRoomId().trim().isEmpty()) {
            if (checkRoomConflict(exam.getRoomId(), exam.getExamDate(), exam.getStartTime(), exam.getEndTime(), exam.getExamId())) {
                throw new ScheduleConflictException("Room Conflict: Room '" + exam.getRoomId() +
                        "' is already booked by another exam on that date and time.");
            }
        }

        // Invigilator conflict check excluding current exam
        if (exam.getInvigilatorId() != null && !exam.getInvigilatorId().trim().isEmpty()) {
            if (checkInvigilatorConflict(exam.getInvigilatorId(), exam.getExamDate(), exam.getStartTime(), exam.getEndTime(), exam.getExamId())) {
                throw new ScheduleConflictException("Invigilator Conflict: Faculty '" + exam.getInvigilatorId() +
                        "' is already assigned elsewhere during that period.");
            }
        }

        for (int i = 0; i < exams.size(); i++) {
            if (exams.get(i).getExamId().equalsIgnoreCase(exam.getExamId())) {
                exams.set(i, exam);
                storage.saveExams(exams);
                return;
            }
        }
    }

    public synchronized void cancelExam(String examId) {
        Exam e = getExamById(examId);
        if (e != null) {
            e.setStatus("CANCELLED");
            storage.saveExams(exams);
        }
    }

    public synchronized Exam getExamById(String examId) {
        if (examId == null) return null;
        for (Exam e : exams) {
            if (e.getExamId().equalsIgnoreCase(examId.trim())) {
                return e;
            }
        }
        return null;
    }

    public synchronized List<Exam> getAllExams() {
        return new ArrayList<>(exams);
    }

    public synchronized List<Exam> getScheduledExams() {
        return exams.stream()
                .filter(e -> "SCHEDULED".equalsIgnoreCase(e.getStatus()))
                .collect(Collectors.toList());
    }

    public synchronized List<Exam> getExamsByCourse(String courseCode) {
        if (courseCode == null) return getAllExams();
        return exams.stream()
                .filter(e -> courseCode.equalsIgnoreCase(e.getCourseCode()))
                .collect(Collectors.toList());
    }

    public synchronized List<Exam> getExamsBySemester(int semester) {
        if (semester <= 0) return getAllExams();
        return exams.stream()
                .filter(e -> e.getSemester() == semester)
                .collect(Collectors.toList());
    }

    /**
     * Checks if a room has any overlapping examinations scheduled.
     */
    public synchronized boolean checkRoomConflict(String roomId, String date, String startTime,
                                                  String endTime, String excludeExamId) {
        for (Exam e : exams) {
            if (excludeExamId != null && e.getExamId().equalsIgnoreCase(excludeExamId)) {
                continue;
            }
            if ("CANCELLED".equalsIgnoreCase(e.getStatus())) {
                continue;
            }
            if (roomId.equalsIgnoreCase(e.getRoomId()) && date.equals(e.getExamDate())) {
                if (isTimeOverlapping(startTime, endTime, e.getStartTime(), e.getEndTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if an invigilator is assigned to an overlapping examination.
     */
    public synchronized boolean checkInvigilatorConflict(String invigilatorId, String date, String startTime,
                                                         String endTime, String excludeExamId) {
        for (Exam e : exams) {
            if (excludeExamId != null && e.getExamId().equalsIgnoreCase(excludeExamId)) {
                continue;
            }
            if ("CANCELLED".equalsIgnoreCase(e.getStatus())) {
                continue;
            }
            if (invigilatorId.equalsIgnoreCase(e.getInvigilatorId()) && date.equals(e.getExamDate())) {
                if (isTimeOverlapping(startTime, endTime, e.getStartTime(), e.getEndTime())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTimeOverlapping(String startA, String endA, String startB, String endB) {
        // [startA, endA] overlaps with [startB, endB] iff startA < endB and startB < endA
        return startA.compareTo(endB) < 0 && startB.compareTo(endA) < 0;
    }
}

