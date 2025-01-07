package stockManagementProgram.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection helper class.
 * Manages SQLite database connections and error handling.
 */
public class DbHelper {

    /**
     * Establishes and returns a database connection
     */
    public Connection getConnection() throws SQLException {
        String path = "jdbc:sqlite:/Users/barkin/Downloads/db_klasor/javASqlLite.db";
        return DriverManager.getConnection(path);
    }

    /**
     * Displays formatted database error messages
     */
    public void showErrorMessage(SQLException ex){
        System.out.println("Error: "+ex.getMessage());
        System.out.println("Error code: "+ex.getErrorCode());
    }
}
