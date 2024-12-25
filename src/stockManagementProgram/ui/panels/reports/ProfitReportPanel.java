package stockManagementProgram.ui.panels.reports;

import stockManagementProgram.model.Stock;
import stockManagementProgram.model.StockTransaction;
import stockManagementProgram.model.enums.TransactionType;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.DateFormatter;
import stockManagementProgram.util.PriceFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;

public class ProfitReportPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;
    private final JLabel lastGeneratedLabel;

    public ProfitReportPanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Profit/Loss Report"));

        String[] columns = {"Product", "Sales Quantity", "Unit", "Revenue", "Cost", "Profit"};
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
            if (column == 5 && row < table1.getRowCount() - 1) { // For profit column
                double profit = extractProfitValue(value.toString());
                if (profit < 0) {
                    label.setForeground(Color.RED);
                } else if (profit > 0) {
                    label.setForeground(new Color(0, 150, 0));
                }
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
        double totalProfit = 0;

        for (Stock stock : stockService.getAllStocks()) {
            int totalSoldQuantity = 0;
            double totalRevenue = 0;
            double totalCost = 0;

            for (StockTransaction trans : stock.getTransactions()) {
                if (trans.getType() == TransactionType.REMOVAL) {
                    totalSoldQuantity += trans.getQuantity();
                    totalRevenue += trans.getQuantity() * trans.getPrice();
                    totalCost += trans.getQuantity() * stock.getPrice();
                }
            }

            if (totalSoldQuantity > 0) {
                double profit = totalRevenue - totalCost;
                totalProfit += profit;
                tableModel.addRow(new Object[]{
                        stock.getName(),
                        totalSoldQuantity,
                        stock.getUnit(),
                        PriceFormatter.format(totalRevenue),
                        PriceFormatter.format(totalCost),
                        PriceFormatter.format(profit)
                });
            }
        }

        tableModel.addRow(new Object[]{
                "TOTAL", "", "", "", "", PriceFormatter.format(totalProfit)
        });

        lastGeneratedLabel.setText("Last Update: " +
                DateFormatter.format(LocalDateTime.now()));
    }

    private double extractProfitValue(String formattedProfit) {
        try {
            return Double.parseDouble(formattedProfit.replace("TL", "")
                    .replace(",", "")
                    .trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}