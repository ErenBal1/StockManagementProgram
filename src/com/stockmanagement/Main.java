package com.stockmanagement;

import com.stockmanagement.repository.StockRepository;
import com.stockmanagement.repository.impl.InMemoryStockRepository;
import com.stockmanagement.service.StockService;
import com.stockmanagement.service.impl.StockServiceImpl;
import com.stockmanagement.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Look and Feel ayarı
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Dependency Injection
        StockRepository repository = new InMemoryStockRepository();
        StockService stockService = new StockServiceImpl(repository);

        // GUI başlatma
        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame(stockService);
            mainFrame.setVisible(true);
        });
    }
}