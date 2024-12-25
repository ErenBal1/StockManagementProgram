package stockManagementProgram.ui.panels;

import stockManagementProgram.model.Stock;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.PriceFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class StockViewPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;

    public StockViewPanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new BorderLayout(10, 10));

        String[] columns = {"Product Name", "Quantity", "Unit", "Unit Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
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
            tableModel.addRow(new Object[]{
                    stock.getName(),
                    stock.getQuantity(),
                    stock.getUnit(),
                    PriceFormatter.format(stock.getPrice())
            });
        }
    }
}