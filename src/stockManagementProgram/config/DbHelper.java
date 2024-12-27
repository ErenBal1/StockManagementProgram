package stockManagementProgram.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbHelper {
    private String path = "jdbc:sqlite:/Users/barkin/Downloads/db_klasor/javASqlLite.db";
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(path);
    }
    public void showErrorMessage(SQLException ex){
        System.out.println("Error: "+ex.getMessage());
        System.out.println("Error code: "+ex.getErrorCode());
    }
}
