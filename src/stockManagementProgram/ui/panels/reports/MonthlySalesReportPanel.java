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

public class MonthlySalesReportPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;
    private final JLabel lastGeneratedLabel;

    public MonthlySalesReportPanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Monthly Sales Report"));

        String[] columns = {"Date", "Product", "Quantity", "Unit", "Sales Price", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
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
        double totalSales = 0;
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);

        for (Stock stock : stockService.getAllStocks()) {
            for (StockTransaction trans : stock.getTransactions()) {
                if (trans.getDateTime().isAfter(startOfMonth) &&
                        trans.getType() == TransactionType.REMOVAL) {
                    double amount = trans.getQuantity() * trans.getPrice();
                    totalSales += amount;
                    tableModel.addRow(new Object[]{
                            DateFormatter.format(trans.getDateTime()),
                            stock.getName(),
                            trans.getQuantity(),
                            stock.getUnit(),
                            PriceFormatter.format(trans.getPrice()),
                            PriceFormatter.format(amount)
                    });
                }
            }
        }

        tableModel.addRow(new Object[]{
                "TOTAL", "", "", "", "", PriceFormatter.format(totalSales)
        });

        lastGeneratedLabel.setText("Last Update: " +
                DateFormatter.format(LocalDateTime.now()));
    }
}