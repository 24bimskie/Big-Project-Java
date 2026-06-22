package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class InitializeDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "sam";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("⏳ Starting database initialization...");
        
        // 1. Create database if it doesn't exist
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("✅ Database '" + DB_NAME + "' verified/created.");
        } catch (Exception e) {
            System.err.println("❌ Failed to create database: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // 2. Read and parse sam.sql
        String sqlFilePath = "database/sam.sql";
        try (Connection conn = DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             BufferedReader reader = new BufferedReader(new FileReader(sqlFilePath))) {
            
            StringBuilder sb = new StringBuilder();
            String line;
            boolean inMultiLineComment = false;

            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                
                // Skip single-line comments
                if (trimmed.startsWith("--") || trimmed.startsWith("#") || trimmed.isEmpty()) {
                    continue;
                }

                // Handle multi-line comments /* ... */
                if (trimmed.startsWith("/*")) {
                    inMultiLineComment = true;
                }
                if (inMultiLineComment) {
                    if (trimmed.endsWith("*/") || trimmed.contains("*/")) {
                        inMultiLineComment = false;
                    }
                    continue;
                }

                sb.append(line).append("\n");

                // If statement ends with semicolon, execute it
                if (trimmed.endsWith(";")) {
                    String sql = sb.toString().trim();
                    if (!sql.isEmpty()) {
                        try {
                            stmt.execute(sql);
                        } catch (Exception ex) {
                            // Print warning but continue (some drop commands might fail if tables don't exist yet)
                            System.out.println("⚠️ Warning executing statement: " + ex.getMessage());
                        }
                    }
                    sb.setLength(0); // Clear buffer
                }
            }
            System.out.println("✅ Database tables and relationships loaded successfully from sam.sql!");
        } catch (Exception e) {
            System.err.println("❌ Failed to parse/execute sam.sql: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
