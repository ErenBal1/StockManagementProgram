package stockManagementProgram.ui.panels;

import stockManagementProgram.config.DbHelper;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.PriceFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class RecentTransactionsPanel extends JPanel {
    private final DefaultTableModel tableModel;

    public RecentTransactionsPanel() throws SQLException {
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

    }
}