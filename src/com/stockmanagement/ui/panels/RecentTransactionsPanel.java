package com.stockmanagement.ui.panels;

import com.stockmanagement.model.Stock;
import com.stockmanagement.model.StockTransaction;
import com.stockmanagement.model.enums.TransactionType;
import com.stockmanagement.service.StockService;
import com.stockmanagement.ui.components.StyledComponents;
import com.stockmanagement.util.DateFormatter;
import com.stockmanagement.util.PriceFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RecentTransactionsPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;

    public RecentTransactionsPanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new BorderLayout(10, 10));

        String[] columns = {"Date", "Product", "Process", "Quantity", "Unit", "Price", "Total Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                String transactionType = (String) table.getModel().getValueAt(row, 2);
                if ("ADDING".equals(transactionType)) {
                    c.setForeground(new Color(0, 150, 0));
                } else if ("REMOVING".equals(transactionType)) {
                    c.setForeground(new Color(200, 0, 0));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshButton = StyledComponents.createStyledButton("Refresh");

        refreshButton.addActionListener(e -> refreshTransactions());

        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        // İlk yükleme
        refreshTransactions();
    }

    private void refreshTransactions() {
        tableModel.setRowCount(0);
        List<Object[]> transactions = new ArrayList<>();

        for (Stock stock : stockService.getAllStocks()) {
            for (StockTransaction trans : stock.getTransactions()) {
                double total = trans.getQuantity() * trans.getPrice();
                transactions.add(new Object[]{
                        DateFormatter.format(trans.getDateTime()),
                        stock.getName(),
                        trans.getType() == TransactionType.ADDITION ? "ADDING" : "REMOVING",
                        trans.getQuantity(),
                        stock.getUnit(),
                        PriceFormatter.format(trans.getPrice()),
                        PriceFormatter.format(total)
                });
            }
        }

        transactions.sort((a, b) -> String.valueOf(b[0]).compareTo(String.valueOf(a[0])));
        transactions.forEach(tableModel::addRow);
    }
}