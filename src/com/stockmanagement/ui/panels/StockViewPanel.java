package com.stockmanagement.ui.panels;

import com.stockmanagement.model.Stock;
import com.stockmanagement.service.StockService;
import com.stockmanagement.ui.components.StyledComponents;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;

public class StockViewPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;
    // Unit değerlerini küçük harfle saklayalım
    private final List<String> units = Arrays.asList("All Units", "piece", "kg", "litre", "metre");
    private String currentUnit = "All Units";
    private final JLabel statusLabel;
    private final JComboBox<String> unitFilterCombo;

    public StockViewPanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new BorderLayout(10, 10));

        String[] columns = {"Product Name", "Quantity", "Unit", "Unit Price", "Total Amount"};
        tableModel = createTableModel(columns);

        JTable table = new JTable(tableModel);
        TableRowSorter<DefaultTableModel> sorter = createTableSorter(table);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        unitFilterCombo = new JComboBox<>(units.toArray(new String[0]));
        unitFilterCombo.addActionListener(e -> {
            currentUnit = (String) unitFilterCombo.getSelectedItem();
            refreshTable();
        });

        topPanel.add(new JLabel("Filter by Unit: "));
        topPanel.add(unitFilterCombo);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        statusLabel = new JLabel("");
        JButton refreshButton = StyledComponents.createStyledButton("Refresh");
        refreshButton.addActionListener(e -> refreshTable());

        bottomPanel.add(statusLabel, BorderLayout.WEST);
        bottomPanel.add(refreshButton, BorderLayout.EAST);

        JScrollPane scrollPane = new JScrollPane(table);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshTable();
    }

    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 1: return Integer.class;
                    case 3:
                    case 4: return Double.class;
                    default: return String.class;
                }
            }
        };
    }

    private TableRowSorter<DefaultTableModel> createTableSorter(JTable table) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        sorter.setComparator(1, Comparator.naturalOrder());
        sorter.setComparator(3, Comparator.naturalOrder());
        sorter.setComparator(4, Comparator.naturalOrder());

        return sorter;
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int filteredCount = 0;
        int totalCount = 0;

        for (Stock stock : stockService.getAllStocks()) {
            totalCount++;

            if (!currentUnit.equals("All Units")) {
                // Null kontrolü ve güvenli karşılaştırma
                String stockUnit = String.valueOf(stock.getUnit());
                if (stockUnit == null) {
                    continue; // Eğer stock'un unit'i null ise bu satırı atla
                }

                String normalizedStockUnit = stockUnit.toLowerCase().trim();
                String normalizedCurrentUnit = currentUnit.toLowerCase().trim();

                if (!normalizedStockUnit.equals(normalizedCurrentUnit)) {
                    continue;
                }
            }

            filteredCount++;
            double totalValue = stock.getQuantity() * stock.getPrice();

            // Null kontrolü ile güvenli değer atama
            Object[] rowData = {
                    stock.getName(),
                    stock.getQuantity(),
                    stock.getUnit() != null ? stock.getUnit() : "",  // Unit null ise boş string göster
                    stock.getPrice(),
                    totalValue
            };

            tableModel.addRow(rowData);
        }

        updateStatusLabel(filteredCount, totalCount);
    }

    private void updateStatusLabel(int filteredCount, int totalCount) {
        if (currentUnit.equals("All Units")) {
            statusLabel.setText(String.format("Showing all %d products", totalCount));
        } else {
            statusLabel.setText(String.format("Showing %d of %d products (Filtered by %s)",
                    filteredCount, totalCount, currentUnit));
        }
    }
}
