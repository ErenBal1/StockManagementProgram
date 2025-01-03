package stockManagementProgram.ui.panels.reports;

import stockManagementProgram.config.AppConfig;
import stockManagementProgram.config.DbHelper;
import stockManagementProgram.model.Stock;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.DateFormatter;
import stockManagementProgram.util.PriceFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        String[] columns = {"Product", "Quantity", "Unit", "Price", "Total"};
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
        double totalValue = 0;

        Connection conn=null;
        Statement stmt=null;
        DbHelper helper=new DbHelper();
        try{
            conn=helper.getConnection();
            String query="SELECT * FROM ProductStock";
            stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            while (rs.next()){
                double stockValue = rs.getInt("ProductQuantity") * rs.getInt("ProductPrice");
                totalValue+=stockValue;
                    tableModel.addRow(new Object[]{
                            rs.getString(2),
                            rs.getInt(4),
                            rs.getString(5),
                            PriceFormatter.format(rs.getInt("ProductPrice")),
                            PriceFormatter.format(stockValue)
                    });

            }
        }catch (SQLException e){
            helper.showErrorMessage(e);
        }finally {
            if (conn!=null) {
                conn.close();
            }
            if (stmt!=null){
                stmt.close();
            }
        }


        tableModel.addRow(new Object[]{"TOTAL", "", "", "",
                PriceFormatter.format(totalValue)});
        lastGeneratedLabel.setText("Last Update: " +
                DateFormatter.format(LocalDateTime.now()));
    }
}