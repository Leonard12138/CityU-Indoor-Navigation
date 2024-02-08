package com.CityUIndoorNavigation.server.util.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * The class DatabaseOperation provides methods for database operations, including
 * connecting to the database, disconnecting, creating tables, deleting tables,
 * inserting records, updating tables, etc.
 */
public class DatabaseOperation {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseOperation.class);
    private Connection connection;

    public DatabaseOperation(String driverName, String url, String user, String password) {
        connectDatabase(driverName, url, user, password);
    }

    /**
     * Connect to the database.
     *
     * @param driverName Driver name
     * @param url        Uniform Resource Identifier
     * @param user       Database username
     * @param password   Database password
     */
    private void connectDatabase(String driverName, String url, String user, String password) {
        try {
            Class.forName(driverName);
            connection = DriverManager.getConnection(url, user, password);
            logger.info("Database connection successful...");
        } catch (ClassNotFoundException e) {
            logger.error("Driver creation failed...", e);
        } catch (SQLException e) {
            logger.error("Database connection failed...", e);
        }
    }

    /**
     * Create a table.
     *
     * @param tableName Table name
     * @param member    Table column members
     */
    public void createTable(String tableName, String member) {
        String sql = "CREATE TABLE IF NOT EXISTS  " + tableName + " ( " + member + " ); ";
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sql);
            logger.info("Table {} created successfully...", tableName);
        } catch (SQLException e) {
            logger.error("Table {} creation failed...", tableName, e);
        }
    }

    /**
     * Create a table using SQL statement.
     *
     * @param sqlString SQL statement for table creation
     */
    public void createTable(String sqlString) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(sqlString);
        } catch (SQLException e) {
            logger.error("Table creation failed...", e);
        }
    }

    /**
     * Insert data into a specified table.
     *
     * @param tableName Table name for data insertion
     * @param m         Data to be inserted
     * @param <V>       Generic type, suitable for classes in the data package overriding toString() method
     */
    public <V> void updateTable(String tableName, V m) {
        try {
            Statement statement = connection.createStatement();
            String sql = "INSERT INTO " + tableName + " VALUES " + "(" + m.toString() + ")";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            logger.error("Record insertion failed...{}", e.getMessage());
        }
    }

    /**
     * Update table using SQL statement for record insertion.
     *
     * @param insertStr SQL statement for data insertion
     */
    public void updateTable(String insertStr) {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(insertStr);
        } catch (SQLException e) {
            logger.error("Record insertion failed...{}", e.getMessage());
        }
    }

    /**
     * Delete all records in a table.
     *
     * @param tableName Table name
     * @throws SQLException Thrown in case of SQL exception
     */
    public void deleteAllRecords(String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "DELETE FROM " + tableName;
        statement.executeUpdate(sql);
    }

    /**
     * Delete a specified table.
     *
     * @param tableName Table name
     * @throws SQLException Thrown in case of SQL exception
     */
    public void deleteTable(String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        String sqlString = "DROP TABLE IF EXISTS " + tableName;
        statement.executeUpdate(sqlString);
    }

    /**
     * Disconnect from the database.
     */
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            logger.error("Failed to disconnect...:{}", e.getMessage());
        }
    }

    /**
     * Return the ID of the last record in the table. Returns 0 if the table is empty.
     *
     * @param tableName Table name
     * @return The ID of the last record in the table
     */
    public int checkTableLastID(String tableName) {
        int id = 0;
        String sql = "SELECT count(*) AS maxid FROM " + tableName;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (rs.next()) {
                id = rs.getInt("maxid");
            }
        } catch (SQLException e) {
            logger.error("checkTableLastID error:{}", e.getMessage());
        } finally {
            return id;
        }
    }

    /**
     * Return the connection to the database held by the class.
     *
     * @return The connection to the database
     */
    public Connection getConnection() {
        return connection;
    }
}
