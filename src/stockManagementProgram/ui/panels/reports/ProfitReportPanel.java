package stockManagementProgram.ui.panels.reports;

import stockManagementProgram.config.DbHelper;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.DateFormatter;
import stockManagementProgram.util.PriceFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ProfitReportPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JLabel lastGeneratedLabel;

    public ProfitReportPanel() {
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

        generateButton.addActionListener(e -> {
            try {
                generateReport();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void generateReport() throws SQLException {
        tableModel.setRowCount(0);
        double totalProfit = 0;
        DbHelper helper = new DbHelper();

        String transactionQuery = "SELECT * FROM TransactionTable";
        String stockQuery = "SELECT ProductName, ProductPrice FROM ProductStock";

        try (Connection conn = helper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet transactionRs = stmt.executeQuery(transactionQuery)) {

            Map<String, Double> productPrices = new HashMap<>();
            try (Statement stockStmt = conn.createStatement();
                 ResultSet stockRs = stockStmt.executeQuery(stockQuery)) {
                while (stockRs.next()) {
                    productPrices.put(stockRs.getString("ProductName"), stockRs.getDouble("ProductPrice"));
                }
            }

            while (transactionRs.next()) {
                int totalSoldQuantity = 0;
                double totalRevenue = 0;
                double totalCost = 0;

                if ("REMOVE".equals(transactionRs.getString("Transaction"))) {
                    int quantity = transactionRs.getInt("Quantity");
                    String productName = transactionRs.getString("ProductName");
                    double price = transactionRs.getDouble("Price");

                    totalSoldQuantity += quantity;
                    totalRevenue += quantity * price;

                    if (productPrices.containsKey(productName)) {
                        totalCost += quantity * productPrices.get(productName);
                    } else {
                        System.err.println("Ürün fiyatı bulunamadı: " + productName);
                    }
                }

                if (totalSoldQuantity > 0) {
                    double profit = totalRevenue - totalCost;
                    totalProfit += profit;

                    tableModel.addRow(new Object[]{
                            transactionRs.getString("ProductName"),
                            totalSoldQuantity,
                            transactionRs.getString("Unit"),
                            PriceFormatter.format(totalRevenue),
                            PriceFormatter.format(totalCost),
                            PriceFormatter.format(profit)
                    });
                }
            }
        } catch (SQLException e) {
            helper.showErrorMessage(e);
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