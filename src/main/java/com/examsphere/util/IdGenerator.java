package com.examsphere.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Utility for generating unique, readable identifiers with business domain prefixes.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public final class IdGenerator {

    private static final AtomicLong COUNTER = new AtomicLong(System.currentTimeMillis() % 100000);

    private IdGenerator() {
    }

    public static String generateId(String prefix) {
        String cleanPrefix = (prefix == null || prefix.trim().isEmpty()) ? "ID" : prefix.trim().toUpperCase();
        long seq = COUNTER.incrementAndGet();
        return String.format("%s-%05d", cleanPrefix, seq);
    }
}

