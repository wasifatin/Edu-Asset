package com.examsphere.util;

import java.util.Map;

/**
 * Implements academic grading policies, letter grade mapping, grade point calculations,
 * and Grade Point Average (GPA) computation according to Daffodil International University standards.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public final class GradeCalculator {

    private GradeCalculator() {
    }

    /**
     * Maps percentage score to official university letter grade.
     *
     * @param percentage marks percentage (0 - 100)
     * @return letter grade ("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "D", "F")
     */
    public static String calculateGrade(double percentage) {
        if (percentage >= 80.0) return "A+";
        if (percentage >= 75.0) return "A";
        if (percentage >= 70.0) return "A-";
        if (percentage >= 65.0) return "B+";
        if (percentage >= 60.0) return "B";
        if (percentage >= 55.0) return "B-";
        if (percentage >= 50.0) return "C+";
        if (percentage >= 45.0) return "C";
        if (percentage >= 40.0) return "D";
        return "F";
    }

    /**
     * Converts a letter grade to its corresponding 4.00 scale grade point.
     *
     * @param grade letter grade
     * @return numerical grade point
     */
    public static double getGradePoint(String grade) {
        if (grade == null) return 0.0;
        switch (grade.trim().toUpperCase()) {
            case "A+": return 4.00;
            case "A":  return 3.75;
            case "A-": return 3.50;
            case "B+": return 3.25;
            case "B":  return 3.00;
            case "B-": return 2.75;
            case "C+": return 2.50;
            case "C":  return 2.25;
            case "D":  return 2.00;
            case "F":
            default:   return 0.00;
        }
    }

    /**
     * Converts a letter grade to its official qualitative performance remark.
     *
     * @param grade letter grade
     * @return performance remark ("Outstanding", "Excellent", "Very Good", etc.)
     */
    public static String getRemark(String grade) {
        if (grade == null) return "Fail";
        switch (grade.trim().toUpperCase()) {
            case "A+": return "Outstanding";
            case "A":  return "Excellent";
            case "A-": return "Very Good";
            case "B+": return "Good";
            case "B":  return "Satisfactory";
            case "B-": return "Above Average";
            case "C+": return "Average";
            case "C":  return "Below Average";
            case "D":  return "Pass";
            case "F":
            default:   return "Fail";
        }
    }

    /**
     * Calculates weighted GPA based on grade points and credit hours (supports fractional credits, e.g. 1.5).
     *
     * @param courseGradePoints map of course code to grade point
     * @param courseCredits     map of course code to credit hours (Integer or Double)
     * @return GPA rounded to two decimal places
     */
    public static double calculateGPA(Map<String, Double> courseGradePoints, Map<String, ? extends Number> courseCredits) {
        if (courseGradePoints == null || courseCredits == null || courseGradePoints.isEmpty()) {
            return 0.0;
        }

        double totalPoints = 0.0;
        double totalCredits = 0.0;

        for (Map.Entry<String, Double> entry : courseGradePoints.entrySet()) {
            String course = entry.getKey();
            double gp = entry.getValue();
            Number num = courseCredits.get(course);
            double credits = num != null ? num.doubleValue() : 3.0;
            totalPoints += (gp * credits);
            totalCredits += credits;
        }

        if (totalCredits == 0.0) return 0.0;
        return Math.round((totalPoints / totalCredits) * 100.0) / 100.0;
    }
}

