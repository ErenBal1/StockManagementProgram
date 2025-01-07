package stockManagementProgram.ui.panels;

import stockManagementProgram.config.DbHelper;
import stockManagementProgram.model.enums.Unit;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Panel for adding new stock or updating existing stock quantities.
 * Provides search functionality and stock addition form.
 */
public class StockAddPanel extends JPanel {
    // UI Components
    private final StockService stockService;
    private final JTextField searchField;
    private final JTextField nameField;
    private final JTextField quantityField;
    private final JComboBox<Unit> unitComboBox;
    private final JTextField priceField;
    private final JComboBox<String> searchResults;

    public StockAddPanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        searchField = StyledComponents.createStyledTextField();
        nameField = StyledComponents.createStyledTextField();
        quantityField = StyledComponents.createStyledTextField();
        unitComboBox = new JComboBox<>(Unit.values());
        priceField = StyledComponents.createStyledTextField();
        searchResults = new JComboBox<>();
        JButton searchButton = StyledComponents.createStyledButton("Search");
        JButton addButton = StyledComponents.createStyledButton("Add");
        JButton resetButton = StyledComponents.createStyledButton("Reset");


        nameField.setEditable(false);

        setupLayout(gbc, searchButton, addButton, resetButton);
        setupListeners(searchButton, addButton, resetButton);
    }

    private void setupLayout(GridBagConstraints gbc, JButton searchButton, JButton addButton, JButton resetButton) {
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
        add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        add(quantityField, gbc);
        gbc.gridx = 2;
        add(unitComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        add(priceField, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        add(addButton, gbc);

        gbc.gridx = 2; gbc.gridy = 5;
        add(resetButton, gbc);
    }

    private void setupListeners(JButton searchButton, JButton addButton, JButton resetButton) {
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
        addButton.addActionListener(e -> addStock());
        resetButton.addActionListener(e -> clearFields());
    }

    private void performSearch() throws SQLException {
        String searchTerm = searchField.getText().toLowerCase().trim();
        searchResults.removeAllItems();

        if (!searchTerm.isEmpty()) {
            var matchingStocks = stockService.getAllStocks().stream()
                    .filter(stock -> stock.getName().toLowerCase().contains(searchTerm))
                    .collect(Collectors.toList());

            Connection conn= null;
            PreparedStatement preparedStatement=null;
            DbHelper helper=new DbHelper();
            ArrayList<String> productsNames=new ArrayList<>();
            try{
                conn=helper.getConnection();
                String query="Select * FROM ProductStock WHERE ProductName like ?";
                preparedStatement=conn.prepareStatement(query);
                preparedStatement.setString(1,searchTerm+"%");
                ResultSet rs=preparedStatement.executeQuery();
                int sizeOfSearching=0;

                while (rs.next()){
                    searchResults.addItem(rs.getString(2));
                    System.out.println("Product name: "+rs.getString(2));
                    productsNames.add(sizeOfSearching,rs.getString(2));
                    sizeOfSearching++;
                }

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


            searchResults.addItem("Add New Product");

            if (!matchingStocks.isEmpty()) {
                String firstMatch=productsNames.get(0);
                searchResults.setSelectedItem(firstMatch);
                nameField.setText(firstMatch);
                nameField.setEditable(false);

                try{
                    conn=helper.getConnection();
                    String query="Select * FROM ProductStock WHERE ProductName = ?";
                    preparedStatement=conn.prepareStatement(query);
                    preparedStatement.setString(1,firstMatch);
                    ResultSet rs=preparedStatement.executeQuery();

                    searchResults.setSelectedItem(firstMatch);
                    nameField.setText(firstMatch);
                    nameField.setEditable(false);
                    priceField.setText(rs.getString(3));
                    unitComboBox.setSelectedItem(rs.getString(5));
                    unitComboBox.setEnabled(false);

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

            } else {
                searchResults.setSelectedItem("Add New Product");
                nameField.setText(searchTerm);
                nameField.setEditable(true);
                unitComboBox.setEnabled(true);
                priceField.setText("");
            }
        } else {
            searchResults.addItem("Add New Product");
            nameField.setText("");
            nameField.setEditable(true);
            unitComboBox.setEnabled(true);
            priceField.setText("");
        }
    }

private void handleSearchSelection() throws SQLException {
    Connection conn= null;
    PreparedStatement preparedStatement=null;
    DbHelper helper=new DbHelper();
        String selected = (String) searchResults.getSelectedItem();
        if (selected != null) {
            if (selected.equals("Add New Product")) {
                String searchTerm = searchField.getText().trim();
                if (!searchTerm.isEmpty()) {
                    nameField.setText(searchTerm);
                } else {
                    nameField.setText("");
                }
                nameField.setEditable(true);
                unitComboBox.setEnabled(true);
                priceField.setText("");
            } else {
                nameField.setText(selected);
                nameField.setEditable(false);

                try{
                    conn=helper.getConnection();
                    String query="Select * FROM ProductStock WHERE ProductName = ?";
                    preparedStatement=conn.prepareStatement(query);
                    preparedStatement.setString(1,selected);
                    ResultSet rs=preparedStatement.executeQuery();

                    priceField.setText(rs.getString(3));
                    unitComboBox.setSelectedItem(rs.getString(5));
                    searchResults.setSelectedItem(selected);
                    unitComboBox.setEnabled(false);

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
    }

    /**
     * Handles stock addition process
     * Validates input and updates database
     */
    private void addStock() {
        DbHelper helper=new DbHelper();
        Connection conn=null;
        PreparedStatement preparedstmt=null;
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a product name!");
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            Unit selectedUnit = (Unit) unitComboBox.getSelectedItem();
            if (quantity>=0 && price>=0) {
                try {
                    conn = helper.getConnection();
                    System.out.println("Transaction dbye bağlandı");
                    String query = "INSERT INTO TransactionTable (ProductName,[Transaction],Quantity,Unit,Price,Date) Values(?,?,?,?,?,?)";
                    Date now = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String formattedDate = formatter.format(now);
                    preparedstmt = conn.prepareStatement(query);
                    preparedstmt.setString(1, name);
                    preparedstmt.setString(2, "ADD");
                    preparedstmt.setInt(3, quantity);
                    preparedstmt.setString(4, String.valueOf(selectedUnit));
                    preparedstmt.setDouble(5, price);
                    preparedstmt.setString(6, formattedDate);
                    preparedstmt.executeUpdate();


                } catch (SQLException e) {
                    helper.showErrorMessage(e);
                } finally {
                    if (preparedstmt != null) {
                        preparedstmt.close();
                    }
                    if (conn != null) {
                        conn.close();
                    }
                }

                if (nameField.isEditable()) {
                    if (stockService.existsByName(name)) {
                        handleExistingStock(name, quantity, price, selectedUnit);
                    } else {
                        try{
                            conn=helper.getConnection();
                            System.out.println("Başarılı şekilde bağlandı");
                            String query="INSERT INTO ProductStock (ProductName,ProductPrice,ProductQuantity,ProductUnit,ProductInsertDate) Values(?,?,?,?,?)";
                            preparedstmt=conn.prepareStatement(query);

                            preparedstmt.setString(1,name);
                            preparedstmt.setDouble(2,price);
                            preparedstmt.setInt(3,quantity);
                            preparedstmt.setString(4, String.valueOf(selectedUnit));
                            Date now = new Date();
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                            String formattedDate = formatter.format(now);
                            System.out.println("Formatted Date: " + formattedDate);

                            preparedstmt.setString(5, formattedDate);
                            preparedstmt.executeUpdate();
                            System.out.println("Başarılı şekilde eklendi");

                        }catch (SQLException e){
                            helper.showErrorMessage(e);
                        }finally {
                            if (preparedstmt != null) {
                                preparedstmt.close();
                            }
                            if (conn != null) {
                                conn.close();
                            }
                        }
                        stockService.addStock(name, quantity, price, selectedUnit);
                        JOptionPane.showMessageDialog(this, "New stock successfully added!");
                    }
                } else {
                    try{
                        conn=helper.getConnection();
                        System.out.println("Başarılı şekilde bağlandı");
                        String query="Update ProductStock set ProductQuantity = ProductQuantity + ? ,ProductPrice = ?  Where ProductName = ?";
                        preparedstmt=conn.prepareStatement(query);
                        preparedstmt.setInt(1,quantity);
                        preparedstmt.setDouble(2,price);
                        preparedstmt.setString(3,name);
                        preparedstmt.executeUpdate();
                        System.out.println("Başarılı şekilde güncellendi");

                    }catch (SQLException e){
                        helper.showErrorMessage(e);
                    }finally {
                        try {
                            if (preparedstmt!=null){
                                preparedstmt.close();
                            }
                            if (conn!=null){
                                conn.close();
                            }
                        }catch (SQLException e ){
                            System.out.println(e.getMessage());
                        }
                    }
                    stockService.addStock(name, quantity, price, selectedUnit);
                    JOptionPane.showMessageDialog(this, "Stock successfully updated!");
                }

                clearFields();


            }else{
                JOptionPane.showMessageDialog(this, "Quantity or Price invalid value!!");
            }


        } catch (NumberFormatException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity or price!");
        }
    }
    private void handleExistingStock(String name, int quantity, double price, Unit unit) {
        int response = JOptionPane.showConfirmDialog(this,
                "Product \"" + name + "\" already exists. Would you like to add to existing stock?",
                "Product Exists",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            stockService.addStock(name, quantity, price, unit);
            JOptionPane.showMessageDialog(this, "Stock successfully updated!");
        }
    }

    private void clearFields() {
        searchField.setText("");
        nameField.setText("");
        quantityField.setText("");
        priceField.setText("");
        searchResults.removeAllItems();
        nameField.setEditable(false);
        unitComboBox.setEnabled(true);
    }
}