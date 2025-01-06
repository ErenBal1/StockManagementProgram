package stockManagementProgram.ui.panels;
import stockManagementProgram.config.DbHelper;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.PriceFormatter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.*;
import java.util.Arrays;

public class StockViewPanel extends JPanel {
    private final StockService stockService;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> unitFilterCombo;
    private String currentUnit = "All Units";

    public StockViewPanel(StockService stockService) throws SQLException {
        this.stockService = stockService;
        setLayout(new BorderLayout(10, 10));

        // Temel tablo yapısı
        String[] columns = {"Product Name", "Quantity", "Unit", "Unit Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Tablo ve sorter oluşturma
        JTable table = new JTable(tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        // Filter panel oluşturma
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] units = {"All Units", "Piece", "Kilogram", "Liter", "Meter"};
        unitFilterCombo = new JComboBox<>(units);
        unitFilterCombo.addActionListener(e -> {
            currentUnit = (String) unitFilterCombo.getSelectedItem();
            try {
                refreshTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        filterPanel.add(new JLabel("Filter by Unit: "));
        filterPanel.add(unitFilterCombo);

        // Scroll ve refresh button ekleme
        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshButton = StyledComponents.createStyledButton("Refresh");
        refreshButton.addActionListener(e -> {
            try {
                refreshTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // Panel düzeni
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        // İlk veri yükleme
        refreshTable();
    }

    private void refreshTable() throws SQLException {
        tableModel.setRowCount(0);
        Connection conn = null;
        Statement stmt = null;
        DbHelper helper = new DbHelper();

        try {
            conn = helper.getConnection();
            String query = "SELECT * FROM ProductStock";
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String unit = rs.getString(5);

                // Eğer filtre "All Units" değilse ve birim eşleşmiyorsa, atla
                if (!"All Units".equals(currentUnit) &&
                        !currentUnit.equalsIgnoreCase(unit)) {
                    continue;
                }

                tableModel.addRow(new Object[]{
                        rs.getString(2),  // Product Name
                        rs.getInt(4),     // Quantity
                        unit,             // Unit
                        PriceFormatter.format(rs.getDouble(3))  // Price
                });
            }
        } catch (SQLException e) {
            helper.showErrorMessage(e);
        } finally {
            if (conn != null) conn.close();
            if (stmt != null) stmt.close();
        }
    }
}