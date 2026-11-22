package com.examsphere.service;

import com.examsphere.model.ExamRoom;
import com.examsphere.model.SeatAssignment;
import com.examsphere.storage.FileStorageService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing examination room capacities, student seating allocations,
 * and overflow prevention.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class SeatAllocationService {

    private static SeatAllocationService instance;

    private final FileStorageService storage;
    private final List<ExamRoom> rooms;
    private final List<SeatAssignment> seatAssignments;

    private SeatAllocationService() {
        this.storage = FileStorageService.getInstance();
        this.rooms = new ArrayList<>(storage.loadRooms());
        this.seatAssignments = new ArrayList<>(storage.loadSeatAssignments());
        // Clean initial state: users input their own rooms and seat plans
    }

    public static synchronized SeatAllocationService getInstance() {
        if (instance == null) {
            instance = new SeatAllocationService();
        }
        return instance;
    }

    private void seedInitialRooms() {
        ExamRoom r1 = new ExamRoom("KT-320", "Kobi Sufia Kamal Bhaban", 40);
        r1.addFacility("Projector");
        r1.addFacility("Air Conditioned");
        r1.addFacility("CCTV Surveillance");

        ExamRoom r2 = new ExamRoom("KT-321", "Kobi Sufia Kamal Bhaban", 35);
        r2.addFacility("Air Conditioned");
        r2.addFacility("CCTV Surveillance");

        ExamRoom r3 = new ExamRoom("AB-402", "Academic Building 4", 50);
        r3.addFacility("Projector");
        r3.addFacility("CCTV Surveillance");

        rooms.add(r1);
        rooms.add(r2);
        rooms.add(r3);
        storage.saveRooms(rooms);

        // Seed some initial seat assignments for EX-2026-001
        SeatAssignment sa1 = new SeatAssignment("SA-001", "EX-2026-001", "KT-320", "STU001", 1);
        SeatAssignment sa2 = new SeatAssignment("SA-002", "EX-2026-001", "KT-320", "STU002", 2);
        seatAssignments.add(sa1);
        seatAssignments.add(sa2);
        storage.saveSeatAssignments(seatAssignments);
    }

    public synchronized void addRoom(ExamRoom room) {
        if (room == null || room.getRoomId() == null) return;
        if (getRoomById(room.getRoomId()) == null) {
            rooms.add(room);
            storage.saveRooms(rooms);
        }
    }

    public synchronized void updateRoom(ExamRoom room) {
        if (room == null) return;
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomId().equalsIgnoreCase(room.getRoomId())) {
                rooms.set(i, room);
                storage.saveRooms(rooms);
                return;
            }
        }
    }

    public synchronized List<ExamRoom> getAllRooms() {
        return new ArrayList<>(rooms);
    }

    public synchronized ExamRoom getRoomById(String roomId) {
        if (roomId == null) return null;
        for (ExamRoom r : rooms) {
            if (r.getRoomId().equalsIgnoreCase(roomId.trim())) {
                return r;
            }
        }
        return null;
    }

    /**
     * Allocates seats to a list of students for an exam, checking room capacity.
     */
    public synchronized void allocateSeats(String examId, String roomId, List<String> studentIds) {
        ExamRoom room = getRoomById(roomId);
        if (room == null || studentIds == null) return;

        removeAllocation(examId);

        int max = Math.min(room.getCapacity(), studentIds.size());
        for (int i = 0; i < max; i++) {
            String studentId = studentIds.get(i);
            String seatId = "SA-" + examId + "-" + (i + 1);
            SeatAssignment sa = new SeatAssignment(seatId, examId, roomId, studentId, i + 1);
            seatAssignments.add(sa);
        }
        storage.saveSeatAssignments(seatAssignments);
    }

    public synchronized List<SeatAssignment> getSeatsByExam(String examId) {
        if (examId == null) return new ArrayList<>();
        return seatAssignments.stream()
                .filter(s -> examId.equalsIgnoreCase(s.getExamId()))
                .collect(Collectors.toList());
    }

    public synchronized int getAvailableSeats(String roomId, String examId) {
        ExamRoom room = getRoomById(roomId);
        if (room == null) return 0;
        int occupied = (int) seatAssignments.stream()
                .filter(s -> examId.equalsIgnoreCase(s.getExamId()) && roomId.equalsIgnoreCase(s.getRoomId()))
                .count();
        return Math.max(0, room.getCapacity() - occupied);
    }

    public synchronized void removeAllocation(String examId) {
        if (examId == null) return;
        seatAssignments.removeIf(s -> examId.equalsIgnoreCase(s.getExamId()));
        storage.saveSeatAssignments(seatAssignments);
    }
}

