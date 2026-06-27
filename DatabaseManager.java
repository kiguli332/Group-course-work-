package com.firstbank.uganda.dao;

import com.firstbank.uganda.model.Account;
import com.firstbank.uganda.util.AccountNumberGenerator;

import java.sql.*;
import java.time.LocalDate;

/**
 * Manages database connections and account persistence.
 * Uses H2 embedded database for portability.
 * 
 * NOTE: To use MS Access instead, replace the JDBC URL with:
 * jdbc:ucanaccess://path/to/FirstBank.accdb
 * and add ucanaccess dependency to pom.xml
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:ucanaccess://./data/FirstBankUganda.accdb;newDatabaseVersion=V2010";

    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        try {
            // Ensure parent directory exists
            java.io.File dataDir = new java.io.File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
            connection = DriverManager.getConnection(DB_URL);
            initializeTables();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MS Access database", e);
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Creates required tables if they don't exist.
     */
    private void initializeTables() throws SQLException {
        boolean tableExists = false;
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet res = meta.getTables(null, null, "accounts", new String[]{"TABLE"})) {
            if (res.next()) {
                tableExists = true;
            }
        }
        // Case-insensitive check for robustness
        if (!tableExists) {
            try (ResultSet res = meta.getTables(null, null, "ACCOUNTS", new String[]{"TABLE"})) {
                if (res.next()) {
                    tableExists = true;
                }
            }
        }

        if (!tableExists) {
            String createAccountsTable = "CREATE TABLE accounts (" +
                "account_number VARCHAR(20) PRIMARY KEY, " +
                "first_name VARCHAR(30) NOT NULL, " +
                "last_name VARCHAR(30) NOT NULL, " +
                "nin VARCHAR(14) NOT NULL, " +
                "email VARCHAR(100) NOT NULL, " +
                "phone_number VARCHAR(15) NOT NULL, " +
                "pin VARCHAR(10) NOT NULL, " +
                "date_of_birth DATE NOT NULL, " +
                "branch_code VARCHAR(10) NOT NULL, " +
                "branch_name VARCHAR(50) NOT NULL, " +
                "account_type VARCHAR(20) NOT NULL, " +
                "opening_deposit DECIMAL(15,2) NOT NULL, " +
                "date_opened DATE NOT NULL, " +
                "joint_nin VARCHAR(14), " +
                "special_rules VARCHAR(100))";

            try (Statement stmt = connection.createStatement()) {
                stmt.execute(createAccountsTable);
            }
        }

        AccountNumberGenerator.initializeTable(connection);
    }

    /**
     * Saves a new account to the database.
     */
    public boolean saveAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (account_number, first_name, last_name, nin, " +
            "email, phone_number, pin, date_of_birth, branch_code, branch_name, " +
            "account_type, opening_deposit, date_opened, joint_nin, special_rules) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getFirstName());
            pstmt.setString(3, account.getLastName());
            pstmt.setString(4, account.getNin());
            pstmt.setString(5, account.getEmail());
            pstmt.setString(6, account.getPhoneNumber());
            pstmt.setString(7, account.getPin());
            pstmt.setDate(8, Date.valueOf(account.getDateOfBirth()));
            pstmt.setString(9, account.getBranchCode());
            pstmt.setString(10, account.getBranchName());
            pstmt.setString(11, account.getAccountType());
            pstmt.setBigDecimal(12, account.getOpeningDeposit());
            pstmt.setDate(13, Date.valueOf(account.getDateOpened()));
            pstmt.setString(14, account.getJointNin());
            pstmt.setString(15, account.getSpecialRules());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Generates and assigns a unique account number.
     */
    public String generateAccountNumber(String branchCode) throws SQLException {
        return AccountNumberGenerator.generateNext(branchCode, connection);
    }

    /**
     * Retrieves all accounts from database.
     */
    public ResultSet getAllAccounts() throws SQLException {
        Statement stmt = connection.createStatement();
        return stmt.executeQuery("SELECT * FROM accounts ORDER BY date_opened DESC");
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes database connection.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
