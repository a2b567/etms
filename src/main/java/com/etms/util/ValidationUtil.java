package com.etms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Centralised validation utility – Encapsulates all input‑checking logic.
 */
public class ValidationUtil {

    // ---------- basic checks ----------
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    // ---------- length constraints ----------
    public static boolean isWithinLength(String value, int min, int max) {
        if (value == null) return false;
        int len = value.trim().length();
        return len >= min && len <= max;
    }

    // ---------- numeric checks ----------
    public static boolean isPositiveInteger(String value) {
        try {
            int num = Integer.parseInt(value.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isNonNegativeInteger(String value) {
        try {
            int num = Integer.parseInt(value.trim());
            return num >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveDouble(String value) {
        try {
            double num = Double.parseDouble(value.trim());
            return num > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ---------- date format ----------
    public static boolean isValidDateFormat(String date, String format) {
        if (date == null || date.trim().isEmpty()) return false;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(date.trim());
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    /** Common YYYY-MM-DD check */
    public static boolean isDateYYYYMMDD(String date) {
        return isValidDateFormat(date, "yyyy-MM-dd");
    }

    /** YYYY-MM-DD HH:MM check (used for match scheduling) */
    public static boolean isDateTimeYYYYMMDDHHMM(String dateTime) {
        return isValidDateFormat(dateTime, "yyyy-MM-dd HH:mm");
    }

    // ---------- sanitisation (basic) ----------
    public static String sanitize(String input) {
        if (input == null) return null;
        return input.replaceAll("[;'\"\\-\\-]", "");  // remove dangerous chars
    }
}