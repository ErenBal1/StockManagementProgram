package stockManagementProgram.ui.panels;

import stockManagementProgram.config.DbHelper;
import stockManagementProgram.model.Stock;
import stockManagementProgram.model.StockTransaction;
import stockManagementProgram.model.enums.TransactionType;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.DateFormatter;
import stockManagementProgram.util.PriceFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RecentTransactionsPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;

    public RecentTransactionsPanel(StockService stockService) throws SQLException {
        this.stockService = stockService;
        setLayout(new BorderLayout(10, 10));

        String[] columns = {"Date", "Product", "Transaction", "Quantity", "Unit", "Price", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                String transactionType = (String) table.getModel().getValueAt(row, 2);
                if ("ADD".equals(transactionType)) {
                    c.setForeground(new Color(0, 150, 0));
                } else if ("REMOVE".equals(transactionType)) {
                    c.setForeground(new Color(200, 0, 0));
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshButton = StyledComponents.createStyledButton("Refresh");

        refreshButton.addActionListener(e -> {
            try {
                refreshTransactions();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);


        refreshTransactions();
    }

    private void refreshTransactions() throws SQLException {
        Connection conn=null;
        Statement stmt=null;
        DbHelper helper=new DbHelper();
        tableModel.setRowCount(0);
//        List<Object[]> transactions = new ArrayList<>();
        try{
            conn=helper.getConnection();
            System.out.println("Başarılı şekilde bağlandı");
            String query="SELECT * FROM TransactionTable";
            stmt=conn.createStatement();
            ResultSet rs=stmt.executeQuery(query);
            while (rs.next()){
                tableModel.addRow(new Object[]{
                        rs.getString(7),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getString(5),
                        rs.getInt(6),
                        PriceFormatter.format(rs.getDouble(6)*rs.getInt(4))
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

//        for (Stock stock : stockService.getAllStocks()) {
//            for (StockTransaction trans : stock.getTransactions()) {
//                double total = trans.getQuantity() * trans.getPrice();
//                transactions.add(new Object[]{
//                        DateFormatter.format(trans.getDateTime()),
//                        stock.getName(),
//                        trans.getType() == TransactionType.ADDITION ? "ADD" : "REMOVE",
//                        trans.getQuantity(),
//                        stock.getUnit(),
//                        PriceFormatter.format(trans.getPrice()),
//                        PriceFormatter.format(total)
//                });
//            }
//        }
//
//        transactions.sort((a, b) -> String.valueOf(b[0]).compareTo(String.valueOf(a[0])));
//        transactions.forEach(tableModel::addRow);
    }
}