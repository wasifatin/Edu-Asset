package com.examsphere.interfaces;

/**
 * Interface for entities that support keyword-based searching.
 * <p>
 * Classes implementing this interface provide a mechanism to determine
 * whether the entity matches a given search query string.
 * </p>
 *
 * @author EduAsset Team
 * @version 1.0
 */
public interface Searchable {

    /**
     * Determines whether this entity matches the given search query.
     * <p>
     * Implementations should perform case-insensitive matching against
     * relevant fields of the entity (e.g., name, ID, email).
     * </p>
     *
     * @param query the search query string to match against
     * @return {@code true} if this entity matches the query, {@code false} otherwise
     */
    boolean matchesSearch(String query);
}
