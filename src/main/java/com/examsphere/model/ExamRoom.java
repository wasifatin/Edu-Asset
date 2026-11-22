package com.examsphere.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a physical examination room.
 * <p>
 * Tracks the room's location, seating capacity, availability, and facilities.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ExamRoom implements Serializable {

    private static final long serialVersionUID = 9L;

    /** Unique room identifier (e.g., "KT-320"). */
    private String roomId;

    /** Building where the room is located. */
    private String building;

    /** Maximum seating capacity. */
    private int capacity;

    /** Whether the room is currently available for scheduling. */
    private boolean available;

    /** Facilities available in the room (e.g., "Projector", "AC"). */
    private List<String> facilities;

    /**
     * Default no-argument constructor.
     */
    public ExamRoom() {
        this.available = true;
        this.facilities = new ArrayList<>();
    }

    /**
     * Constructs an ExamRoom with the given details.
     *
     * @param roomId   unique room identifier
     * @param building building name
     * @param capacity seating capacity
     */
    public ExamRoom(String roomId, String building, int capacity) {
        this.roomId = roomId;
        this.building = building;
        this.capacity = capacity;
        this.available = true;
        this.facilities = new ArrayList<>();
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    /**
     * Returns the room ID.
     *
     * @return room ID
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Sets the room ID.
     *
     * @param roomId room ID to set
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    /**
     * Returns the building name.
     *
     * @return building
     */
    public String getBuilding() {
        return building;
    }

    /**
     * Sets the building name.
     *
     * @param building building to set
     */
    public void setBuilding(String building) {
        this.building = building;
    }

    /**
     * Returns the seating capacity.
     *
     * @return capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Sets the seating capacity.
     *
     * @param capacity capacity to set
     */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Returns whether the room is available.
     *
     * @return {@code true} if available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Sets the availability status.
     *
     * @param available availability to set
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Returns the list of facilities.
     *
     * @return facilities list
     */
    public List<String> getFacilities() {
        return facilities;
    }

    /**
     * Sets the list of facilities.
     *
     * @param facilities facilities list to set
     */
    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    /**
     * Adds a facility to this room if not already present.
     *
     * @param facility the facility name to add
     */
    public void addFacility(String facility) {
        if (!facilities.contains(facility)) {
            facilities.add(facility);
        }
    }

    /**
     * Removes a facility from this room.
     *
     * @param facility the facility name to remove
     */
    public void removeFacility(String facility) {
        facilities.remove(facility);
    }

    /**
     * Returns a string representation of this room.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return String.format("ExamRoom{roomId='%s', building='%s', capacity=%d, available=%b}",
                roomId, building, capacity, available);
    }

    /**
     * Two rooms are equal if they share the same roomId.
     *
     * @param o the object to compare with
     * @return {@code true} if roomIds are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExamRoom examRoom = (ExamRoom) o;
        return Objects.equals(roomId, examRoom.roomId);
    }

    /**
     * Hash code based on roomId.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(roomId);
    }
}
