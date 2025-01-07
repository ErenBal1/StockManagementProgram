package stockManagementProgram.ui.panels;

import stockManagementProgram.config.DbHelper;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.PriceFormatter;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Comparator;

public class StockViewPanel extends JPanel {
        private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,##0.00");
        private final DefaultTableModel tableModel;
        private final JComboBox<String> unitFilterCombo;
        private String currentUnit = "All Units";
        public static String format(double price) {
        return "₺" + PRICE_FORMAT.format(price);
    }
    public StockViewPanel() throws SQLException {
        setLayout(new BorderLayout(10, 10));

        // Basic table structure
        String[] columns = {"Product Name", "Quantity", "Unit", "Unit Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Creating tables and sorters
        JTable table = new JTable(tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);



        // Special sorter for numeric columns
        sorter.setComparator(1, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                Integer num1 = Integer.parseInt(o1.toString());
                Integer num2 = Integer.parseInt(o2.toString());
                return num1.compareTo(num2);
            }
        });



// Updated price comparator
        sorter.setComparator(3, new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                try {
                    // Debug print to see incoming values
                    System.out.println("Comparing: " + o1 + " with " + o2);

                    // Remove all non-numeric characters except decimal point
                    String price1 = o1.toString().replaceAll("[^\\d.]", "");
                    String price2 = o2.toString().replaceAll("[^\\d.]", "");

                    // Debug print after cleaning
                    System.out.println("After cleaning: " + price1 + " vs " + price2);

                    // Parse as BigDecimal for precise decimal comparison
                    BigDecimal num1 = new BigDecimal(price1);
                    BigDecimal num2 = new BigDecimal(price2);

                    // Debug print final values
                    System.out.println("Comparing BigDecimals: " + num1 + " vs " + num2);

                    return num1.compareTo(num2);
                } catch (Exception e) {
                    System.err.println("Error comparing prices: " + e.getMessage());
                    System.err.println("Values were: " + o1 + " and " + o2);
                    // Fall back to string comparison
                    return o1.toString().compareTo(o2.toString());
                }
            }
        });

// Also update your PriceFormatter.format method to ensure consistent formatting:


        table.setRowSorter(sorter);

        // Create a filter panel
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

        // Add scroll and refresh button
        JScrollPane scrollPane = new JScrollPane(table);
        JButton refreshButton = StyledComponents.createStyledButton("Refresh");
        refreshButton.addActionListener(e -> {
            try {
                refreshTable();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // Panel layout
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(refreshButton, BorderLayout.SOUTH);

        // Initial data upload
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

                // If the filter is not “All Units” and the unit does not match, skip
                if (!"All Units".equals(currentUnit) &&
                        !currentUnit.equalsIgnoreCase(unit)) {
                    continue;
                }

                tableModel.addRow(new Object[]{
                        rs.getString("ProductName"),
                        rs.getInt("ProductQuantity"),
                        unit,
                        PriceFormatter.format(rs.getDouble("ProductPrice"))
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