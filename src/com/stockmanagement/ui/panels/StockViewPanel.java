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
    private int unitClickCount = 0;
    private final List<String> units = Arrays.asList("Unit", "piece", "kg", "litre", "metre");
    private String currentUnit = "Unit"; // Başlangıç birimi

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
                    case 1: return Integer.class;
                    case 3:
                    case 4: return Double.class;
                    default: return String.class;
                }
            }
        };

        JTable table = new JTable(tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Unit sütunu için sıralamayı devre dışı bırak
        sorter.setSortable(2, false);

        // Diğer sütunlar için sıralama ayarla
        sorter.setComparator(1, Comparator.naturalOrder());
        sorter.setComparator(3, Comparator.naturalOrder());
        sorter.setComparator(4, Comparator.naturalOrder());

        // Birim sütununa tıklama olayını dinle
        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int column = table.columnAtPoint(evt.getPoint());
                if (column == 2) { // Birim sütununa tıklama kontrolü
                    unitClickCount = (unitClickCount + 1) % units.size();
                    currentUnit = units.get(unitClickCount);
                    table.getColumnModel().getColumn(2).setHeaderValue(currentUnit);
                    table.getTableHeader().repaint();
                    refreshTable(); // Tabloyu seçilen birime göre güncelle
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshButton = StyledComponents.createStyledButton("Refresh");
        refreshButton.addActionListener(e -> refreshTable());

        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        refreshTable(); // Başlangıç tablosunu doldur
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (Stock stock : stockService.getAllStocks()) {
            // Unit seçili değilse sadece eşleşen birimleri göster
            if (!currentUnit.equals("Unit")) {
                if (!stock.getUnit().equals(currentUnit)) {
                    continue; // Birim eşleşmiyorsa bu satırı atla
                }
            }

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
