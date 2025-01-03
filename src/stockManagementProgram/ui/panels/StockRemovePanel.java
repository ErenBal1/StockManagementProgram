package stockManagementProgram.ui.panels;

import stockManagementProgram.config.DbHelper;
import stockManagementProgram.model.Stock;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;
import stockManagementProgram.util.DateFormatter;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class StockRemovePanel extends JPanel {
    private final StockService stockService;
    private final JTextField searchField;
    private final JTextField nameField;
    private final JTextField quantityField;
    private final JTextField priceField;
    private final JComboBox<String> searchResults;
    private final JLabel currentQuantityLabel;

    public StockRemovePanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Initialize components
        searchField = StyledComponents.createStyledTextField();
        nameField = StyledComponents.createStyledTextField();
        quantityField = StyledComponents.createStyledTextField();
        priceField = StyledComponents.createStyledTextField();
        searchResults = new JComboBox<>();
        currentQuantityLabel = new JLabel("");
        JButton searchButton = StyledComponents.createStyledButton("Search");
        JButton removeButton = StyledComponents.createStyledButton("Remove");
        JButton resetButton = StyledComponents.createStyledButton("Reset");


        nameField.setEditable(false);

        setupLayout(gbc, searchButton, removeButton, resetButton);

        setupListeners(searchButton, removeButton, resetButton);
    }

    private void setupLayout(GridBagConstraints gbc, JButton searchButton, JButton removeButton, JButton resetButton) {
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Search Product:"), gbc);
        gbc.gridx = 1;
        add(searchField, gbc);
        gbc.gridx = 2;
        add(searchButton, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        add(searchResults, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        add(new JLabel("Quantity to Remove:"), gbc);
        gbc.gridx = 1;
        add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Sale Price:"), gbc);
        gbc.gridx = 1;
        add(priceField, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        add(currentQuantityLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 6;
        add(removeButton, gbc);

        gbc.gridx = 2; gbc.gridy = 6;
        add(resetButton, gbc);
    }

    private void setupListeners(JButton searchButton, JButton removeButton, JButton resetButton) {
        searchButton.addActionListener(e -> {
            try {
                performSearch();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        searchResults.addActionListener(e -> {
            try {
                handleSearchSelection();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
        removeButton.addActionListener(e -> removeStock());
        resetButton.addActionListener(e -> clearFields());
    }

    private void performSearch() throws SQLException {
        String searchTerm = searchField.getText().toLowerCase().trim();
        searchResults.removeAllItems();

        stockService.getAllStocks().stream()
                .filter(stock -> stock.getName().toLowerCase().contains(searchTerm))
                .forEach(stock -> searchResults.addItem(stock.getName()));
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        DbHelper helper = new DbHelper();
        ArrayList<String> productsNames = new ArrayList<>();

        try {
            conn = helper.getConnection();
            String query = "SELECT * FROM ProductStock WHERE ProductName LIKE ?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, searchTerm + "%");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String productName = rs.getString(2);
                searchResults.addItem(productName);
                productsNames.add(productName);
            }
        } catch (SQLException e) {
            helper.showErrorMessage(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }

        if (!productsNames.isEmpty()) {
            String firstMatch = productsNames.get(0);
            searchResults.setSelectedItem(firstMatch);
            nameField.setText(firstMatch);
            nameField.setEditable(false);
        } else {
            searchResults.setSelectedItem("Add New Product");
            nameField.setText(searchTerm);
            nameField.setEditable(true);
        }
    }






    private void handleSearchSelection() throws SQLException {
        String selected = (String) searchResults.getSelectedItem();
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        DbHelper helper = new DbHelper();
        if (selected != null) {
            nameField.setText(selected);


            try{
                conn=helper.getConnection();
                String query="Select * FROM ProductStock WHERE ProductName = ?";
                preparedStatement=conn.prepareStatement(query);
                preparedStatement.setString(1,selected);
                ResultSet rs=preparedStatement.executeQuery();

                priceField.setText(rs.getString(3));
                currentQuantityLabel.setText("Current Quantity: " + rs.getInt(4) + " " + rs.getString(5));
                searchResults.setSelectedItem(selected);
                priceField.setText(rs.getString(3));

            }catch (SQLException e){
                helper.showErrorMessage(e);

            }finally {
                if (conn!=null){
                    conn.close();
                }
                if (preparedStatement!=null){
                    preparedStatement.close();
                }
            }


        }
    }

    private void removeStock() {
        DbHelper helper = new DbHelper();
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        ArrayList<String> productsNames = new ArrayList<>();

        try {
            String name = nameField.getText();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a product!");
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());

            conn = helper.getConnection();
            conn.setAutoCommit(false); // Transaction başlat AI


            String query = "SELECT * FROM ProductStock WHERE ProductName LIKE ?";
            preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name + "%");
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                productsNames.add(rs.getString("ProductName"));
            }

            for (String productName : productsNames) {
                if (productName.equals(name)) {
                    query = "SELECT * FROM ProductStock WHERE ProductName = ?";
                    preparedStatement = conn.prepareStatement(query);
                    preparedStatement.setString(1, name);
                    rs = preparedStatement.executeQuery();

                    if (rs.next() && rs.getInt("ProductQuantity") >= quantity) {
                        query = "UPDATE ProductStock SET ProductQuantity = ProductQuantity - ? WHERE ProductName = ?";
                        preparedStatement = conn.prepareStatement(query);
                        preparedStatement.setInt(1, quantity);
                        preparedStatement.setString(2, name);
                        preparedStatement.executeUpdate();

                        query = "INSERT INTO TransactionTable (ProductName, [Transaction], Quantity, Unit, Price, Date) VALUES (?, ?, ?, ?, ?, ?)";
                        preparedStatement = conn.prepareStatement(query);
                        Date now = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String formattedDate = formatter.format(now);

                        preparedStatement.setString(1, name);
                        preparedStatement.setString(2, "REMOVE");
                        preparedStatement.setInt(3, quantity);
                        preparedStatement.setString(4, rs.getString("ProductUnit"));
                        preparedStatement.setDouble(5, price);
                        preparedStatement.setString(6, formattedDate);
                        preparedStatement.executeUpdate();

                        JOptionPane.showMessageDialog(this, "Stock successfully removed!");
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this, "Insufficient stock quantity!");
                    }
                    break;
                }
            }
            conn.commit(); // Transaction onayla
        } catch (SQLException | NumberFormatException e) {
            try {
                if (conn != null) conn.rollback(); // Hata olursa geri al
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        } finally {
            try {
                if (rs != null) rs.close();
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearFields() {
        searchField.setText("");
        nameField.setText("");
        quantityField.setText("");
        priceField.setText("");
        currentQuantityLabel.setText("");
        searchResults.removeAllItems();
    }
}