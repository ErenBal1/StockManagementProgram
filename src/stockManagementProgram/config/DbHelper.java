package stockManagementProgram.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection helper class.
 * Manages SQLite database connections and error handling.
 */
public class DbHelper {
    private String path = "jdbc:sqlite:/Users/barkin/Downloads/db_klasor/javASqlLite.db";

    /**
     * Establishes and returns a database connection
     * @return Connection object for database operations
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(path);
    }

    /**
     * Displays formatted database error messages
     * @param ex SQLException to be formatted and displayed
     */
    public void showErrorMessage(SQLException ex){
        System.out.println("Error: "+ex.getMessage());
        System.out.println("Error code: "+ex.getErrorCode());
    }
}
