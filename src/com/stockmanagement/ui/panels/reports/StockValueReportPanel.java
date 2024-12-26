package com.stockmanagement.ui.panels.reports;

import com.stockmanagement.model.Stock;
import com.stockmanagement.service.StockService;
import com.stockmanagement.ui.components.StyledComponents;
import com.stockmanagement.util.DateFormatter;
import com.stockmanagement.util.PriceFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;

public class StockValueReportPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;
    private final JLabel lastGeneratedLabel;

    public StockValueReportPanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Stock Value Report"));

        // Table setup
        String[] columns = {"Product", "Quantity", "Unit", "Unit Price", "Total Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        // Controls
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
        double totalValue = 0;

        for (Stock stock : stockService.getAllStocks()) {
            double stockValue = stock.getQuantity() * stock.getPrice();
            totalValue += stockValue;
            tableModel.addRow(new Object[]{
                    stock.getName(),
                    stock.getQuantity(),
                    stock.getUnit(),
                    PriceFormatter.format(stock.getPrice()),
                    PriceFormatter.format(stockValue)
            });
        }

        tableModel.addRow(new Object[]{"TOTAL", "", "", "",
                PriceFormatter.format(totalValue)});
        lastGeneratedLabel.setText("Last Update: " +
                DateFormatter.format(LocalDateTime.now()));
    }
}