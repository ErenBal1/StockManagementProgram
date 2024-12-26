package com.stockmanagement.ui.panels;

import com.stockmanagement.model.enums.Unit;
import com.stockmanagement.service.StockService;
import com.stockmanagement.ui.components.StyledComponents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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

        // Initialize components
        searchField = StyledComponents.createStyledTextField();
        nameField = StyledComponents.createStyledTextField();
        quantityField = StyledComponents.createStyledTextField();
        unitComboBox = new JComboBox<>(Unit.values());
        priceField = StyledComponents.createStyledTextField();
        searchResults = new JComboBox<>();
        JButton searchButton = StyledComponents.createStyledButton("Search");
        JButton addButton = StyledComponents.createStyledButton("Add");

        nameField.setEditable(true);

        // Layout setup
        setupLayout(gbc, searchButton, addButton);

        // Add listeners
        setupListeners(searchButton, addButton);
    }

    private void setupLayout(GridBagConstraints gbc, JButton searchButton, JButton addButton) {
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
    }

    private void setupListeners(JButton searchButton, JButton addButton) {
        searchButton.addActionListener(e -> performSearch());
        searchResults.addActionListener(e -> handleSearchSelection());
        addButton.addActionListener(e -> addStock());
    }

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        searchResults.removeAllItems();
        searchResults.addItem("Add New Product");

        stockService.getAllStocks().stream()
                .filter(stock -> stock.getName().toLowerCase().contains(searchTerm))
                .forEach(stock -> searchResults.addItem(stock.getName()));
    }

    private void handleSearchSelection() {
        String selected = (String) searchResults.getSelectedItem();
        if (selected != null) {
            if (selected.equals("Add New Product")) {
                nameField.setText("");
                nameField.setEditable(true);
                unitComboBox.setEnabled(true);
                
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
                JOptionPane.showMessageDialog(this, "Please enter a product name");
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
                    JOptionPane.showMessageDialog(this, "New product added!");
                }
            } else {
                stockService.addStock(name, quantity, price, selectedUnit);
                JOptionPane.showMessageDialog(this, "Stock Successfully Renewed!");
            }

            clearFields();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Quantity or Price!");
        }
    }

    private void handleExistingStock(String name, int quantity, double price, Unit unit) {
        int response = JOptionPane.showConfirmDialog(this,
                "\"" + name + "\" is already availavbe. Would you like to add to this product?",
                "Product Available",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            stockService.addStock(name, quantity, price, unit);
            JOptionPane.showMessageDialog(this, "Stock Successfully Renewed!");
        }
    }

    KeyAdapter enterKeyListener = new KeyAdapter() {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                addStock();
            }
        }
    };

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