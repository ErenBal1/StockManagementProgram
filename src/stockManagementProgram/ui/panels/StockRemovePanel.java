package stockManagementProgram.ui.panels;

import stockManagementProgram.model.Stock;
import stockManagementProgram.service.StockService;
import stockManagementProgram.ui.components.StyledComponents;

import javax.swing.*;
import java.awt.*;

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
        searchButton.addActionListener(e -> performSearch());
        searchResults.addActionListener(e -> handleSearchSelection());
        removeButton.addActionListener(e -> removeStock());
        resetButton.addActionListener(e -> clearFields());
    }

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase().trim();
        searchResults.removeAllItems();

        stockService.getAllStocks().stream()
                .filter(stock -> stock.getName().toLowerCase().contains(searchTerm))
                .forEach(stock -> searchResults.addItem(stock.getName()));
    }

    private void handleSearchSelection() {
        String selected = (String) searchResults.getSelectedItem();
        if (selected != null) {
            nameField.setText(selected);
            stockService.findStock(selected).ifPresent(stock -> {
                currentQuantityLabel.setText("Current Quantity: " + stock.getQuantity() + " " + stock.getUnit());
                priceField.setText(String.valueOf(stock.getPrice()));
            });
        }
    }

    private void removeStock() {
        try {
            String name = nameField.getText();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a product!");
                return;
            }

            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());

            if (stockService.findStock(name).isPresent()) {
                Stock stock = stockService.findStock(name).get();
                if (stock.getQuantity() >= quantity) {
                    stockService.removeStock(name, quantity, price);
                    JOptionPane.showMessageDialog(this, "Stock successfully removed!");
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Insufficient stock quantity! Quantity available: " + stock.getQuantity() + " " + stock.getUnit());
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity or price!");
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