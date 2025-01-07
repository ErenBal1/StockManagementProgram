package stockManagementProgram.ui.panels.reports;


import stockManagementProgram.config.DbHelper;
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
import java.util.Objects;

public class MonthlySalesReportPanel extends JPanel {

    private final DefaultTableModel tableModel;
    private final JLabel lastGeneratedLabel;

    public MonthlySalesReportPanel() {

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
        double totalSales = 0;
        Connection conn=null;
        Statement stmt=null;
        DbHelper helper=new DbHelper();
        try{
            conn=helper.getConnection();
            String query="SELECT * FROM TransactionTable";
            stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            while (rs.next()){
               if (Objects.equals(rs.getString(3), "REMOVE")){
                   double amount = rs.getInt("Quantity") * rs.getDouble("Price");
                   totalSales += amount;
                    tableModel.addRow(new Object[]{
                            rs.getString("Date"),
                            rs.getString("ProductName"),
                            rs.getInt("Quantity"),
                            rs.getString("Unit"),
                            PriceFormatter.format(rs.getDouble("Price")),
                            PriceFormatter.format(amount)
                    });
                }
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

        tableModel.addRow(new Object[]{
                "TOTAL", "", "", "", "", PriceFormatter.format(totalSales)
        });

        lastGeneratedLabel.setText("Last Update: " +
                DateFormatter.format(LocalDateTime.now()));
    }
}