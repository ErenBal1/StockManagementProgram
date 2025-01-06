package stockManagementProgram;

import stockManagementProgram.config.DbHelper;
import stockManagementProgram.config.AppConfig;
import stockManagementProgram.repository.StockRepository;
import stockManagementProgram.repository.impl.InMemoryStockRepository;
import stockManagementProgram.service.StockService;
import stockManagementProgram.service.impl.StockServiceImpl;
import stockManagementProgram.ui.MainFrame;

import javax.swing.*;
import java.sql.*;

public class Main {


    public static void main(String[] args) throws SQLException {
        // Initialize database and load credentials
        DbHelper helper=new DbHelper();
        Connection conn=null;
        try{
            conn= helper.getConnection();
            System.out.println("Veritabanına bağlanıldı.");
            // Load admin credentials
            String query="SELECT * FROM admin";
            Statement stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            AppConfig.DEFAULT_USERNAME=rs.getString(1);
            AppConfig.DEFAULT_PASSWORD=rs.getString(2);

        }catch(SQLException e){
            helper.showErrorMessage(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

        // Set up UI look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Dependency Injection
        // Initialize services and repository
        StockRepository repository = new InMemoryStockRepository();
        StockService stockService = new StockServiceImpl(repository);

        // GUI initialisation
        // Launch application UI
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = null;
            try {
                mainFrame = new MainFrame(stockService);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            mainFrame.setVisible(true);
        });
    }
}