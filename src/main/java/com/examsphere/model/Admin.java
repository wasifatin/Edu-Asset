package com.examsphere.model;

import java.io.Serializable;

/**
 * Represents an administrator (Examination Controller) in the EduAsset system.
 * <p>
 * Extends {@link User} with admin-specific fields such as designation.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class Admin extends User implements Serializable {

    private static final long serialVersionUID = 4L;

    /** Admin ID (mirrors userId via super). */
    private String adminId;

    /** Designation (e.g., "Examination Controller"). */
    private String designation;

    /**
     * Default no-argument constructor.
     */
    public Admin() {
        super();
    }

    /**
     * Constructs a fully-initialised Admin.
     *
     * @param adminId      unique admin identifier (also used as userId)
     * @param fullName     full display name
     * @param email        email address
     * @param passwordHash hashed password
     * @param designation  designation title
     */
    public Admin(String adminId, String fullName, String email, String passwordHash, String designation) {
        super(adminId, fullName, email, passwordHash, "ADMIN");
        this.adminId = adminId;
        this.designation = designation;
    }

    // ─── Abstract Method Implementations ────────────────────────────────

    /**
     * {@inheritDoc}
     *
     * @return "Administration Dashboard"
     */
    @Override
    public String getDashboardDescription() {
        return "Administration Dashboard";
    }

    /**
     * {@inheritDoc}
     *
     * @return "Examination Controller"
     */
    @Override
    public String getDisplayRole() {
        return "Examination Controller";
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
        return (adminId != null && adminId.toLowerCase().contains(lowerQuery))
                || (getFullName() != null && getFullName().toLowerCase().contains(lowerQuery))
                || (getEmail() != null && getEmail().toLowerCase().contains(lowerQuery))
                || (designation != null && designation.toLowerCase().contains(lowerQuery));
    }

    // ─── Getters and Setters ────────────────────────────────────────────

    /**
     * Returns the admin ID.
     *
     * @return admin ID
     */
    public String getAdminId() {
        return adminId;
    }

    /**
     * Sets the admin ID.
     *
     * @param adminId admin ID to set
     */
    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    /**
     * Returns the designation title.
     *
     * @return designation
     */
    public String getDesignation() {
        return designation;
    }

    /**
     * Sets the designation title.
     *
     * @param designation designation to set
     */
    public void setDesignation(String designation) {
        this.designation = designation;
    }

    /**
     * Returns a string representation of this admin.
     *
     * @return formatted string
     */
    @Override
    public String toString() {
        return String.format("Admin{adminId='%s', fullName='%s', designation='%s'}",
                adminId, getFullName(), designation);
    }
}
