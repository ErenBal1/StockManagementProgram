package stockManagementProgram.ui.panels;

import stockManagementProgram.config.DbHelper;
import stockManagementProgram.model.Stock;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.PriceFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StockViewPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;

    public StockViewPanel(StockService stockService) throws SQLException {
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
        refreshButton.addActionListener(e -> {
            try {
                refreshTable();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        refreshTable();
    }

    private void refreshTable() throws SQLException {

        Connection conn=null;
        Statement stmt=null;
        DbHelper helper=new DbHelper();
        tableModel.setRowCount(0);
        try{
            conn=helper.getConnection();
            String query="SELECT * FROM ProductStock";
            stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            while (rs.next()){
                tableModel.addRow(new Object[]{
                        rs.getString(2),
                        rs.getInt(4),
                        rs.getString(5),
                        PriceFormatter.format(rs.getDouble(3))
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

    }
}