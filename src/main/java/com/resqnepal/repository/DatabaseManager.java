package com.resqnepal.repository;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DIRECTORY = "data";
    private static String URL = "jdbc:sqlite:" + DIRECTORY + "/resqnepal.db";

    public static void setTestMode(boolean testMode) {
        if (testMode) {
            URL = "jdbc:sqlite:" + DIRECTORY + "/resqnepal_test.db";
        } else {
            URL = "jdbc:sqlite:" + DIRECTORY + "/resqnepal.db";
        }
    }

    public static Connection getConnection() throws SQLException {
        // Ensure the directory exists
        File dir = new File(DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        Connection conn = DriverManager.getConnection(URL);
        // Enable foreign keys
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. victims
            stmt.execute("CREATE TABLE IF NOT EXISTS victims (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "age INTEGER, " +
                    "phone TEXT, " +
                    "location_x REAL, " +
                    "location_y REAL)");

            // 2. rescue_requests
            stmt.execute("CREATE TABLE IF NOT EXISTS rescue_requests (" +
                    "request_id TEXT PRIMARY KEY, " +
                    "victim_id INTEGER, " +
                    "type TEXT, " +
                    "severity TEXT, " +
                    "people_affected INTEGER, " +
                    "required_specialization TEXT, " +
                    "status TEXT, " +
                    "description TEXT, " +
                    "FOREIGN KEY (victim_id) REFERENCES victims(id))");

            // 3. rescue_teams
            stmt.execute("CREATE TABLE IF NOT EXISTS rescue_teams (" +
                    "team_id TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "specialization TEXT, " +
                    "member_count INTEGER, " +
                    "loc_x REAL, " +
                    "loc_y REAL, " +
                    "status TEXT)");

            // 4. resources
            stmt.execute("CREATE TABLE IF NOT EXISTS resources (" +
                    "resource_id TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "category TEXT, " +
                    "quantity INTEGER, " +
                    "loc_x REAL, " +
                    "loc_y REAL, " +
                    "status TEXT)");

            // 5. shelters
            stmt.execute("CREATE TABLE IF NOT EXISTS shelters (" +
                    "shelter_id TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "loc_x REAL, " +
                    "loc_y REAL, " +
                    "max_capacity INTEGER, " +
                    "current_occupancy INTEGER, " +
                    "medical_support INTEGER, " +
                    "food_available INTEGER, " +
                    "water_available INTEGER)");

            // 6. missing_persons
            stmt.execute("CREATE TABLE IF NOT EXISTS missing_persons (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "last_known_location TEXT, " +
                    "status TEXT)");

            // 7. rescue_operations
            stmt.execute("CREATE TABLE IF NOT EXISTS rescue_operations (" +
                    "op_id TEXT PRIMARY KEY, " +
                    "request_id TEXT, " +
                    "team_id TEXT, " +
                    "start_time TEXT, " +
                    "end_time TEXT, " +
                    "status TEXT, " +
                    "FOREIGN KEY (request_id) REFERENCES rescue_requests(request_id), " +
                    "FOREIGN KEY (team_id) REFERENCES rescue_teams(team_id))");

            // 8. disasters
            stmt.execute("CREATE TABLE IF NOT EXISTS disasters (" +
                    "disaster_id TEXT PRIMARY KEY, " +
                    "name TEXT NOT NULL, " +
                    "location TEXT, " +
                    "date_occurred TEXT, " +
                    "description TEXT)");

            System.out.println("Database tables verified/initialized successfully at data/resqnepal.db");
        } catch (SQLException e) {
            System.err.println("Database initialization failed: " + e.getMessage());
        }
    }
}
