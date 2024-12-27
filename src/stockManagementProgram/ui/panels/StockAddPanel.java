package stockManagementProgram.ui.panels;

import stockManagementProgram.model.enums.Unit;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;

import javax.swing.*;
import java.awt.*;
import java.util.stream.Collectors;

public class StockAddPanel extends JPanel {
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
        searchButton.addActionListener(e -> performSearch());
        searchResults.addActionListener(e -> handleSearchSelection());
        addButton.addActionListener(e -> addStock());
        resetButton.addActionListener(e -> clearFields());
    }

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        searchResults.removeAllItems();

        if (!searchTerm.isEmpty()) {
            var matchingStocks = stockService.getAllStocks().stream()
                    .filter(stock -> stock.getName().toLowerCase().contains(searchTerm))
                    .collect(Collectors.toList());

            matchingStocks.forEach(stock -> searchResults.addItem(stock.getName()));

            searchResults.addItem("Add New Product");

            if (!matchingStocks.isEmpty()) {
                String firstMatch = matchingStocks.get(0).getName();
                searchResults.setSelectedItem(firstMatch);
                nameField.setText(firstMatch);
                nameField.setEditable(false);

                stockService.findStock(firstMatch).ifPresent(stock -> {
                    priceField.setText(String.valueOf(stock.getPrice()));
                    unitComboBox.setSelectedItem(stock.getUnit());
                    unitComboBox.setEnabled(false);
                });
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

private void handleSearchSelection() {
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
                stockService.findStock(selected).ifPresent(stock -> {
                    priceField.setText(String.valueOf(stock.getPrice()));
                    unitComboBox.setSelectedItem(stock.getUnit());
                    unitComboBox.setEnabled(false);
                });
            }
        }
    }

    private void addStock() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a product name!");
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            Unit selectedUnit = (Unit) unitComboBox.getSelectedItem();

            if (nameField.isEditable()) {
                if (stockService.existsByName(name)) {
                    handleExistingStock(name, quantity, price, selectedUnit);
                } else {
                    stockService.addStock(name, quantity, price, selectedUnit);
                    JOptionPane.showMessageDialog(this, "New stock successfully added!");
                }
            } else {
                stockService.addStock(name, quantity, price, selectedUnit);
                JOptionPane.showMessageDialog(this, "Stock successfully updated!");
            }

            clearFields();

        } catch (NumberFormatException ex) {
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