package com.stockmanagement.ui.panels.reports;

import com.stockmanagement.config.AppConfig;
import com.stockmanagement.model.Stock;
import com.stockmanagement.service.StockService;
import com.stockmanagement.ui.components.StyledComponents;
import com.stockmanagement.util.DateFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;

public class LowStockReportPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;
    private final JLabel lastGeneratedLabel;

    public LowStockReportPanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Low Stock Report"));

        String[] columns = {"Product", "Quantity Available", "Unit", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, (table1, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            if (isSelected) {
                label.setBackground(table1.getSelectionBackground());
                label.setForeground(table1.getSelectionForeground());
            } else {
                label.setBackground(table1.getBackground());
                label.setForeground(Color.RED); // Kritik seviye için kırmızı renk
            }
            return label;
        });

        JScrollPane scrollPane = new JScrollPane(table);

        JButton generateButton = StyledComponents.createStyledButton("Create Report");
        lastGeneratedLabel = new JLabel("");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(generateButton, BorderLayout.WEST);
        bottomPanel.add(lastGeneratedLabel, BorderLayout.EAST);

        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        generateButton.addActionListener(e -> generateReport());
    }

    private void generateReport() {
        tableModel.setRowCount(0);
        for (Stock stock : stockService.getAllStocks()) {
            if (stock.getQuantity() < AppConfig.CRITICAL_STOCK_LEVEL) {
                tableModel.addRow(new Object[]{
                        stock.getName(),
                        stock.getQuantity(),
                        stock.getUnit(),
                        "CRITICAL LEVEL"
                });
            }
        }
        lastGeneratedLabel.setText("Last Update: " +
                DateFormatter.format(LocalDateTime.now()));
    }
}