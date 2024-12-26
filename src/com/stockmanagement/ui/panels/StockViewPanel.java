package com.stockmanagement.ui.panels;

import com.stockmanagement.model.Stock;
import com.stockmanagement.service.StockService;
import com.stockmanagement.ui.components.StyledComponents;
import com.stockmanagement.util.PriceFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;

public class StockViewPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;

    public StockViewPanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new BorderLayout(10, 10));

        String[] columns = {"Product Name", "Quantity", "Unit", "Unit Price", "Total Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 1: // Quantity
                        return Integer.class;
                    case 3: // Unit Price
                    case 4: // Total Amount
                        return Double.class;
                    default:
                        return String.class;
                }
            }
        };

        JTable table = new JTable(tableModel);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Unit sütunu (index 2) için sıralamayı devre dışı bırak
        sorter.setSortable(2, false);

        // Diğer sütunlar için sıralama ayarla
        sorter.setComparator(1, Comparator.naturalOrder()); // Quantity için
        sorter.setComparator(3, Comparator.naturalOrder()); // Unit Price için
        sorter.setComparator(4, Comparator.naturalOrder()); // Total Amount için

        JScrollPane scrollPane = new JScrollPane(table);

        JButton refreshButton = StyledComponents.createStyledButton("Refresh");
        refreshButton.addActionListener(e -> refreshTable());

        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Stock stock : stockService.getAllStocks()) {
            double totalValue = stock.getQuantity() * stock.getPrice();
            tableModel.addRow(new Object[]{
                    stock.getName(),
                    stock.getQuantity(),
                    stock.getUnit(),
                    stock.getPrice(),
                    totalValue
            });
        }
    }
}