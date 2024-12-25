package stockManagementProgram;

import stockManagementProgram.repository.StockRepository;
import stockManagementProgram.repository.impl.InMemoryStockRepository;
import stockManagementProgram.service.StockService;
import stockManagementProgram.service.impl.StockServiceImpl;
import stockManagementProgram.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
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