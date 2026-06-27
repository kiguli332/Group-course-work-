package com.firstbank.uganda.util;

import java.sql.*;
import java.time.LocalDate;

/**
 * Generates unique account numbers in format BRANCHCODE-YYYY-xxxxxx.
 * Uses database-backed sequential counters per branch per year.
 */
public class AccountNumberGenerator {

    private static final String FORMAT = "%s-%d-%06d";

    /**
     * Generates the next account number for a given branch.
     * @param branchCode The branch code (e.g., "KLA")
     * @param connection Database connection for counter persistence
     * @return formatted account number
     */
    public static synchronized String generateNext(String branchCode, Connection connection) 
            throws SQLException {
        int year = LocalDate.now().getYear();
        int sequence = getNextSequence(branchCode, year, connection);
        return String.format(FORMAT, branchCode, year, sequence);
    }

    private static int getNextSequence(String branchCode, int year, Connection conn) 
            throws SQLException {
        String selectSql = "SELECT sequence_number FROM account_counters WHERE branch_code = ? AND year = ?";
        String insertSql = "INSERT INTO account_counters (branch_code, year, sequence_number) VALUES (?, ?, 1)";
        String updateSql = "UPDATE account_counters SET sequence_number = sequence_number + 1 WHERE branch_code = ? AND year = ?";

        try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
            selectStmt.setString(1, branchCode);
            selectStmt.setInt(2, year);
            ResultSet rs = selectStmt.executeQuery();

            if (rs.next()) {
                int currentSeq = rs.getInt("sequence_number");
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, branchCode);
                    updateStmt.setInt(2, year);
                    updateStmt.executeUpdate();
                }
                return currentSeq + 1;
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, branchCode);
                    insertStmt.setInt(2, year);
                    insertStmt.executeUpdate();
                }
                return 1;
            }
        }
    }

    /**
     * Creates the counter table if it doesn't exist.
     */
    public static void initializeTable(Connection connection) throws SQLException {
        boolean tableExists = false;
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet res = meta.getTables(null, null, "account_counters", new String[]{"TABLE"})) {
            if (res.next()) {
                tableExists = true;
            }
        }
        // Case-insensitive check for robustness
        if (!tableExists) {
            try (ResultSet res = meta.getTables(null, null, "ACCOUNT_COUNTERS", new String[]{"TABLE"})) {
                if (res.next()) {
                    tableExists = true;
                }
            }
        }

        if (!tableExists) {
            String sql = "CREATE TABLE account_counters (" +
                         "branch_code VARCHAR(10) NOT NULL, " +
                         "year INT NOT NULL, " +
                         "sequence_number INT NOT NULL DEFAULT 0, " +
                         "PRIMARY KEY (branch_code, year))";
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(sql);
            }
        }
    }
}
