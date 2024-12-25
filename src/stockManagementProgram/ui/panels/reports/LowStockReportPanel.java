package stockManagementProgram.ui.panels.reports;

import stockManagementProgram.config.AppConfig;
import stockManagementProgram.model.Stock;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.DateFormatter;

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

        String[] columns = {"Product", "Quantity", "Unit", "Status"};
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
                label.setForeground(Color.RED); // Red colour for critical level
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
        lastGeneratedLabel.setText("Last update: " +
                DateFormatter.format(LocalDateTime.now()));
    }
}