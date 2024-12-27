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


    public static void main(String[] args) {
        DbHelper helper=new DbHelper();
        Connection conn=null;
        try{
            conn= helper.getConnection();
            System.out.println("Veritabanına bağlanıldı.");
            String query="SELECT * FROM admin";
            Statement stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            AppConfig.DEFAULT_USERNAME=rs.getString(1);
            AppConfig.DEFAULT_PASSWORD=rs.getString(2);

        }catch(SQLException e){
            helper.showErrorMessage(e);
        }
        try {
            // Look and Feel setting
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Dependency Injection
        StockRepository repository = new InMemoryStockRepository();
        StockService stockService = new StockServiceImpl(repository);

        // GUI initialisation
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(stockService);
            mainFrame.setVisible(true);
        });
    }
}