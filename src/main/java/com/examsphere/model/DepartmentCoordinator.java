package com.examsphere.model;

import java.io.Serializable;

/**
 * Represents a department coordinator in the EduAsset system.
 * <p>
 * Extends {@link User} with a department assignment. Coordinators
 * oversee departmental exam and academic operations.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class DepartmentCoordinator extends User implements Serializable {

    private static final long serialVersionUID = 5L;

    /** Coordinator ID (mirrors userId via super). */
    private String coordinatorId;

    /** Department this coordinator manages. */
    private String department;

    /**
     * Default no-argument constructor.
     */
    public DepartmentCoordinator() {
        super();
    }

    /**
     * Constructs a fully-initialised DepartmentCoordinator.
     *
     * @param coordinatorId unique coordinator identifier (also used as userId)
     * @param fullName      full display name
     * @param email         email address
     * @param passwordHash  hashed password
     * @param department    department code this coordinator manages
     */
    public DepartmentCoordinator(String coordinatorId, String fullName, String email,
                                  String passwordHash, String department) {
        super(coordinatorId, fullName, email, passwordHash, "COORDINATOR");
        this.coordinatorId = coordinatorId;
        this.department = department;
    }

    // ─── Abstract Method Implementations ────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * @return dashboard description containing the department
     */
    @Override
    public String getDashboardDescription() {
        return "Department Coordinator — " + department;
    }

    /**
     * {@inheritDoc}
     *
     * @return "Department Coordinator"
     */
    @Override
    public String getDisplayRole() {
        return "Department Coordinator";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean matchesSearch(String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }
        String lowerQuery = query.toLowerCase();
        return (coordinatorId != null && coordinatorId.toLowerCase().contains(lowerQuery))
                || (getFullName() != null && getFullName().toLowerCase().contains(lowerQuery))
                || (getEmail() != null && getEmail().toLowerCase().contains(lowerQuery))
                || (department != null && department.toLowerCase().contains(lowerQuery));
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    /**
     * Returns the coordinator ID.
     *
     * @return coordinator ID
     */
    public String getCoordinatorId() {
        return coordinatorId;
    }

    /**
     * Sets the coordinator ID.
     *
     * @param coordinatorId coordinator ID to set
     */
    public void setCoordinatorId(String coordinatorId) {
        this.coordinatorId = coordinatorId;
    }

    /**
     * Returns the department code.
     *
     * @return department
     */
    public String getDepartment() {
        return department;
    }

    /**
     * Sets the department code.
     *
     * @param department department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }

    /**
     * Returns a string representation of this coordinator.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return String.format("DepartmentCoordinator{coordinatorId='%s', fullName='%s', department='%s'}",
                coordinatorId, getFullName(), department);
    }
}
