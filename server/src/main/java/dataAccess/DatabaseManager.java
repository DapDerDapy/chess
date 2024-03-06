package dataAccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to load db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    public static void clear() throws DataAccessException {
        String[] tablesToDrop = {"YourTable1", "YourTable2", "YourTableN"}; // List your tables here

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement()) {
                for (String table : tablesToDrop) {
                    try {
                        stmt.executeUpdate("DROP TABLE IF EXISTS " + table);
                    } catch (SQLException e) {
                        conn.rollback(); // Rollback transaction if dropping any table fails
                        throw e; // Rethrow the exception to be handled outside
                    }
                }
                conn.commit(); // Commit transaction if all drops are successful
            } catch (SQLException e) {
                throw new DataAccessException("Failed to clear database: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database connection problem: " + e.getMessage());
        }
    }


    /**
     * Sets up the required database tables if they do not already exist.
     */
    public static void setupDatabaseTables() throws DataAccessException {
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                String sqlCreateUsersTable =
                        "CREATE TABLE IF NOT EXISTS users (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY," +
                        "username VARCHAR(255) NOT NULL," +
                        "password VARCHAR(255) NOT NULL," +
                        "email VARCHAR(255) NOT NULL" +
                        ")";
                stmt.executeUpdate(sqlCreateUsersTable);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to setup database tables: " + e.getMessage());
        }
    }
}
