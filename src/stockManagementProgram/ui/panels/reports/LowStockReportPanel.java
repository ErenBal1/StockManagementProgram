package stockManagementProgram.ui.panels.reports;

import stockManagementProgram.config.AppConfig;
import stockManagementProgram.config.DbHelper;


import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.DateFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

/**
 * Panel for displaying and generating low stock reports.
 * Identifies products with quantities below the critical threshold.
 */
public class LowStockReportPanel extends JPanel {
    private final DefaultTableModel tableModel;
    private final JLabel lastGeneratedLabel;

    public LowStockReportPanel() {

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

        Connection conn=null;
        Statement stmt=null;
        DbHelper helper=new DbHelper();
        try{
            conn=helper.getConnection();
            String query="SELECT * FROM ProductStock";
            stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            while (rs.next()){
                if (rs.getInt("ProductQuantity")< AppConfig.CRITICAL_STOCK_LEVEL) {
                    tableModel.addRow(new Object[]{
                            rs.getString("ProductName"),
                            rs.getInt("ProductQuantity"),
                            rs.getString("ProductUnit"),
                            "CRITICAL LEVEL"
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

        lastGeneratedLabel.setText("Last update: " +
                DateFormatter.format(LocalDateTime.now()));
    }
}