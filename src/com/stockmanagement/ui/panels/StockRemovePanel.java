package com.stockmanagement.ui.panels;

import com.stockmanagement.model.Stock;
import com.stockmanagement.service.StockService;
import com.stockmanagement.ui.components.StyledComponents;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.border.EmptyBorder;

public class StockRemovePanel extends JPanel {
    private final StockService stockService;
    private final JTextField searchField;
    private final JTable productTable;
    private final JTextField quantityField;
    private final JTextField priceField;
    private final JTextField currentQuantityField;
    private Timer searchTimer;
    private Timer refreshTimer;

    public StockRemovePanel(StockService stockService) {
        this.stockService = stockService;
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Initialize components
        searchField = createSearchField();
        productTable = createProductTable();
        JScrollPane scrollPane = new JScrollPane(productTable);
        scrollPane.setPreferredSize(new Dimension(300, 120));

        quantityField = StyledComponents.createStyledTextField();
        priceField = StyledComponents.createStyledTextField();
        currentQuantityField = StyledComponents.createStyledTextField();
        currentQuantityField.setEditable(false);

        JButton removeButton = createStyledButton();

        setupTimers();
        setupLayout(gbc, scrollPane, removeButton);
        setupListeners(removeButton);
        updateProductTable("");
    }

    private JTextField createSearchField() {
        JTextField field = StyledComponents.createStyledTextField();
        field.putClientProperty("JTextField.placeholderText", "Search products...");
        return field;
    }

    private JTable createProductTable() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Product Name"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(25);
        table.getTableHeader().setPreferredSize(new Dimension(100, 30));

        table.setSelectionBackground(new Color(173, 216, 230)); // Açık mavi arka plan
        table.setSelectionForeground(new Color(40, 40, 40));    // Koyu gri yazı rengi

        return table;
    }

    private JButton createStyledButton() {
        JButton button = StyledComponents.createStyledButton("Remove");
        button.setPreferredSize(new Dimension(100, 35));
        return button;
    }

    private void setupTimers() {
        searchTimer = new Timer(300, e -> updateProductTable(searchField.getText()));
        searchTimer.setRepeats(false);

        refreshTimer = new Timer(2000, e -> updateProductTable(searchField.getText()));
        refreshTimer.start();
    }

    private void setupLayout(GridBagConstraints gbc, JScrollPane scrollPane, JButton removeButton) {
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.anchor = GridBagConstraints.WEST;
        formGbc.fill = GridBagConstraints.HORIZONTAL;

        // Add components to form panel
        addToForm(formPanel, formGbc, "Current Quantity:", currentQuantityField, 0);
        addToForm(formPanel, formGbc, "Amount to Remove:", quantityField, 1);
        addToForm(formPanel, formGbc, "Sale Price:", priceField, 2);

        // Main layout
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(searchPanel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 5, 10, 5);
        add(scrollPane, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        add(formPanel, gbc);

        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        add(removeButton, gbc);
    }

    private void addToForm(JPanel panel, GridBagConstraints gbc, String label, JComponent component, int row) {
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(component, gbc);
        gbc.weightx = 0.0;
    }

    private void setupListeners(JButton removeButton) {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchTimer.restart(); }
            public void removeUpdate(DocumentEvent e) { searchTimer.restart(); }
            public void changedUpdate(DocumentEvent e) { searchTimer.restart(); }
        });

        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = productTable.getSelectedRow();
                if (selectedRow != -1) {
                    String selected = (String) productTable.getValueAt(selectedRow, 0);
                    stockService.findStock(selected).ifPresent(stock -> {
                        currentQuantityField.setText(stock.getQuantity() + " " + stock.getUnit());
                        priceField.setText(String.valueOf(stock.getPrice()));
                        quantityField.setText("");
                    });
                }
            }
        });

        removeButton.addActionListener(e -> removeStock());
    }

    private void updateProductTable(String searchTerm) {
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        model.setRowCount(0);

        stockService.getAllStocks().stream()
                .filter(stock -> stock.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .limit(10)
                .forEach(stock -> model.addRow(new Object[]{stock.getName()}));
    }

    private void removeStock() {
        try {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a product!");
                return;
            }

            String name = (String) productTable.getValueAt(selectedRow, 0);
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());

            stockService.findStock(name).ifPresent(stock -> {
                if (stock.getQuantity() >= quantity) {
                    stockService.removeStock(name, quantity, price);
                    JOptionPane.showMessageDialog(this, "Stock successfully removed!");
                    clearFields();
                    updateProductTable(searchField.getText());
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Insufficient stock! Available: " + stock.getQuantity() + " " + stock.getUnit());
                }
            });
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity and price!");
        }
    }

    private void clearFields() {
        searchField.setText("");
        quantityField.setText("");
        priceField.setText("");
        currentQuantityField.setText("");
        updateProductTable("");
    }

    public void cleanup() {
        if (searchTimer != null) searchTimer.stop();
        if (refreshTimer != null) refreshTimer.stop();
    }
}
