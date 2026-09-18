package com.resqnepal.util;

public class DistanceUtil {
    
    /**
     * Calculates the Euclidean distance between two points.
     * Note: These coordinates are for simulation purposes and do NOT represent real GPS coordinates.
     */
    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
}
