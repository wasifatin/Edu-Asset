package com.examsphere.model;

import com.examsphere.interfaces.Searchable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract base class for all users in the EduAsset system.
 * <p>
 * Provides common identity fields, authentication data, and role information.
 * Subclasses must implement {@link #getDashboardDescription()} and
 * {@link #getDisplayRole()} to supply role-specific display text.
 * </p>
 * <p>
 * Implements {@link Serializable} for persistence and {@link Searchable}
 * for keyword-based search support.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 * @see Student
 * @see Teacher
 * @see Admin
 * @see DepartmentCoordinator
 */
public abstract class User implements Serializable, Searchable {

    private static final long serialVersionUID = 1L;

    /** Unique identifier for this user. */
    private String userId;

    /** Full display name of the user. */
    private String fullName;

    /** Email address of the user. */
    private String email;

    /** Hashed password (SHA-256 with salt). */
    private String passwordHash;

    /** Role string: "ADMIN", "COORDINATOR", "TEACHER", or "STUDENT". */
    private String role;

    /** Whether this account is active and can log in. */
    private boolean active;

    /** Account creation date in ISO format "YYYY-MM-DD". */
    private String createdDate;

    /**
     * Default no-argument constructor.
     */
    public User() {
        this.active = true;
        this.createdDate = LocalDate.now().toString();
    }

    /**
     * Parameterized constructor that initialises all core user fields.
     *
     * @param userId       unique identifier for this user
     * @param fullName     full display name
     * @param email        email address
     * @param passwordHash hashed password
     * @param role         role string ("ADMIN", "COORDINATOR", "TEACHER", "STUDENT")
     */
    public User(String userId, String fullName, String email, String passwordHash, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = true;
        this.createdDate = LocalDate.now().toString();
    }

    // ─── Abstract Methods ───────────────────────────────────────────────

    /**
     * Returns a short description for the user's dashboard header.
     *
     * @return dashboard description string
     */
    public abstract String getDashboardDescription();

    /**
     * Returns the human-readable display role for this user type.
     *
     * @return display role string
     */
    public abstract String getDisplayRole();

    // ─── Getters and Setters ────────────────────────────────────────────

    /**
     * Returns the unique user identifier.
     *
     * @return user ID
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets the unique user identifier.
     *
     * @param userId user ID to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Returns the full display name of the user.
     *
     * @return full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Sets the full display name of the user.
     *
     * @param fullName full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Returns the email address of the user.
     *
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of the user.
     *
     * @param email email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the hashed password.
     *
     * @return password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Sets the hashed password.
     *
     * @param passwordHash password hash to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Returns the role of this user.
     *
     * @return role string
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the role of this user.
     *
     * @param role role string to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Returns whether this user account is active.
     *
     * @return {@code true} if active, {@code false} otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active status of this user account.
     *
     * @param active {@code true} to activate, {@code false} to deactivate
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the account creation date in ISO format.
     *
     * @return creation date string ("YYYY-MM-DD")
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * Sets the account creation date.
     *
     * @param createdDate creation date in "YYYY-MM-DD" format
     */
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    // ─── Searchable ─────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * <p>
     * Base implementation checks userId, fullName, email, and role
     * for a case-insensitive match. Subclasses should override to
     * include additional fields.
     * </p>
     */
    @Override
    public boolean matchesSearch(String query) {
        if (query == null || query.isEmpty()) {
            return true;
        }
        String lowerQuery = query.toLowerCase();
        return (userId != null && userId.toLowerCase().contains(lowerQuery))
                || (fullName != null && fullName.toLowerCase().contains(lowerQuery))
                || (email != null && email.toLowerCase().contains(lowerQuery))
                || (role != null && role.toLowerCase().contains(lowerQuery));
    }

    // ─── Object overrides ───────────────────────────────────────────────

    /**
     * Returns a string representation of this user.
     *
     * @return formatted string with userId, fullName, and role
     */
    @Override
    public String toString() {
        return String.format("User{userId='%s', fullName='%s', role='%s'}", userId, fullName, role);
    }

    /**
     * Two users are equal if they share the same userId.
     *
     * @param o the object to compare with
     * @return {@code true} if userIds are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    /**
     * Hash code based on userId.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}
