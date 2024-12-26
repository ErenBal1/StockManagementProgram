package com.stockmanagement.ui.panels;

import com.stockmanagement.model.Stock;
import com.stockmanagement.service.StockService;
import com.stockmanagement.ui.components.StyledComponents;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;

public class StockRemovePanel extends JPanel {
    private final StockService stockService;
    private final JTextField quantityField;
    private final JTextField priceField;
    private final JComboBox<String> searchComboBox;
    private final JLabel currentQuantityLabel;
    private Timer searchTimer;

    public StockRemovePanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Initialize components
        quantityField = StyledComponents.createStyledTextField();
        priceField = StyledComponents.createStyledTextField();
        searchComboBox = new JComboBox<>();
        searchComboBox.setEditable(true);
        currentQuantityLabel = new JLabel("");
        JButton removeButton = StyledComponents.createStyledButton("Remove");

        // Timer setup for delayed search
        searchTimer = new Timer(300, e -> performSearch());
        searchTimer.setRepeats(false);

        // ComboBox editor setup
        JTextField editor = (JTextField) searchComboBox.getEditor().getEditorComponent();
        editor.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { delayedSearch(); }
            public void removeUpdate(DocumentEvent e) { delayedSearch(); }
            public void changedUpdate(DocumentEvent e) { delayedSearch(); }
        });

        // Layout setup
        setupLayout(gbc, removeButton);

        // Add listeners
        setupListeners(removeButton);
    }

    private void delayedSearch() {
        searchTimer.restart();
    }

    private void setupLayout(GridBagConstraints gbc, JButton removeButton) {
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(searchComboBox, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Amount to be Removed:"), gbc);
        gbc.gridx = 1;
        add(quantityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Sale price:"), gbc);
        gbc.gridx = 1;
        add(priceField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        add(currentQuantityLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        add(removeButton, gbc);
    }

    private void setupListeners(JButton removeButton) {
        searchComboBox.addActionListener(e -> {
            if (e.getActionCommand().equals("comboBoxChanged")) {
                handleSearchSelection();
            }
        });
        removeButton.addActionListener(e -> removeStock());
    }

    private void performSearch() {
        String searchTerm = ((JTextField) searchComboBox.getEditor().getEditorComponent()).getText().toLowerCase().trim();

        if (searchTerm.isEmpty()) {
            searchComboBox.setPopupVisible(false);
            return;
        }

        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        stockService.getAllStocks().stream()
                .filter(stock -> stock.getName().toLowerCase().contains(searchTerm))
                .limit(10)
                .forEach(stock -> model.addElement(stock.getName()));

        searchComboBox.setModel(model);

        if (model.getSize() > 0) {
            searchComboBox.setPopupVisible(true);
        }

        JTextField editor = (JTextField) searchComboBox.getEditor().getEditorComponent();
        editor.setText(searchTerm);
        editor.setCaretPosition(searchTerm.length());


    }

    private void handleSearchSelection() {
        String selected = (String) searchComboBox.getSelectedItem();
        if (selected != null) {
            stockService.findStock(selected).ifPresent(stock -> {
                currentQuantityLabel.setText("Current Quantity: " + stock.getQuantity() + " " + stock.getUnit());
                priceField.setText(String.valueOf(stock.getPrice()));
                quantityField.setText(String.valueOf(stock.getQuantity()));
            });
        }

    }

    private void removeStock() {
        try {
            String name = (String) searchComboBox.getSelectedItem();
            if (name == null || name.isEmpty()) {
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
                            "Insufficient stock! Available quantity: " + stock.getQuantity() + " " + stock.getUnit());
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity or price!");
        }
    }

    private void clearFields() {
        searchComboBox.setSelectedItem("");
        quantityField.setText("");
        priceField.setText("");
        currentQuantityLabel.setText("");
     

    }
}