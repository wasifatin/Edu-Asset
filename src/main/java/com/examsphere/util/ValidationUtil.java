package com.examsphere.util;

import java.util.regex.Pattern;

/**
 * Utility class providing validation methods for credentials, marks, IDs, and dates.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern DATE_PATTERN  = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN  = Pattern.compile("^([01]\\d|2[0-3]):[0-5]\\d$");

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public static boolean isValidId(String id) {
        return id != null && !id.trim().isEmpty() && id.trim().matches("^[A-Za-z0-9_-]+$");
    }

    public static boolean isValidMarks(double marks, double max) {
        return marks >= 0.0 && marks <= max;
    }

    public static boolean isNotEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }

    public static boolean isValidDate(String date) {
        return date != null && DATE_PATTERN.matcher(date.trim()).matches();
    }

    public static boolean isValidTime(String time) {
        return time != null && TIME_PATTERN.matcher(time.trim()).matches();
    }
}

