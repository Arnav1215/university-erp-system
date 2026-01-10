package edu.univ.erp.util;

import java.util.Locale;

public final class GradeScale {
    private GradeScale() {}

    public static String toLetter(Double percentage) {
        if (percentage == null) {
            return null;
        }
        double value = percentage;
        if (value >= 90) return "A+";
        if (value >= 80) return "A";
        if (value >= 70) return "B";
        if (value >= 60) return "C";
        if (value >= 50) return "D";
        if (value >= 40) return "E";
        return "F";
    }

    public static double toGradePoint(String letter) {
        if (letter == null) {
            return Double.NaN;
        }
        switch (letter.toUpperCase(Locale.ROOT)) {
            case "A+":
            case "A":
                return 10.0;
            case "B":
                return 9.0;
            case "C":
                return 8.0;
            case "D":
                return 7.0;
            case "E":
                return 6.0;
            case "F":
                return 0.0;
            default:
                return Double.NaN;
        }
    }

    public static double toGradePoint(Double percentage) {
        String letter = toLetter(percentage);
        return letter == null ? Double.NaN : toGradePoint(letter);
    }
}


