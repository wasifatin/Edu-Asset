package com.examsphere.interfaces;

/**
 * Interface for entities that support an approval/rejection workflow.
 * <p>
 * Classes implementing this interface can be approved or rejected,
 * with comments attached to each decision. They also expose their
 * current approval status.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public interface Approvable {

    /**
     * Approves this entity with the given comments.
     *
     * @param comments the approval comments or reason
     */
    void approve(String comments);

    /**
     * Rejects this entity with the given comments.
     *
     * @param comments the rejection comments or reason
     */
    void reject(String comments);

    /**
     * Returns the current approval status of this entity.
     *
     * @return the approval status string (e.g., "PENDING", "APPROVED", "REJECTED")
     */
    String getApprovalStatus();
}
