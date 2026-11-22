package com.examsphere.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Secure salted password hashing and verification utility.
 * Uses SHA-256 with an application-level salt.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public final class PasswordUtil {

    private static final String APP_SALT = "EduAsset@DIU-CSE221-Spring2026#SecureSalt";

    private PasswordUtil() {
    }

    /**
     * Hashes a plain-text password using SHA-256 with application salt.
     *
     * @param password raw password string
     * @return hex-encoded hash string
     */
    public static String hashPassword(String password) {
        if (password == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String salted = APP_SALT + password;
            byte[] hash = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        }
    }

    /**
     * Verifies whether a candidate plain-text password matches the stored hash.
     * Also supports direct equality if the stored hash is in plain-text demo format.
     *
     * @param candidatePassword entered plain password
     * @param storedHash        stored hash value
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String candidatePassword, String storedHash) {
        if (candidatePassword == null || storedHash == null) {
            return false;
        }
        String calculated = hashPassword(candidatePassword);
        return calculated.equals(storedHash) || candidatePassword.equals(storedHash);
    }
}

