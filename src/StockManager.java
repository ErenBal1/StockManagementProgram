import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class StockManager extends JFrame {
    private List<Item> inventory;
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, quantityField, priceField;

    public StockManager() {
        inventory = new ArrayList<>();
        setTitle("Stock Management System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        createTable();
        JScrollPane scrollPane = new JScrollPane(itemTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        nameField = new JTextField();
        quantityField = new JTextField();
        priceField = new JTextField();

        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        return panel;
    }

    private void createTable() {
        String[] columns = {"Name", "Quantity", "Price", "Added Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        itemTable = new JTable(tableModel);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        JButton addButton = new JButton("Add Item");
        addButton.addActionListener(e -> addItem());

        JButton removeButton = new JButton("Remove Item");
        removeButton.addActionListener(e -> removeItem());

        JButton sellButton = new JButton("Sell Item");
        sellButton.addActionListener(e -> sellItem());

        panel.add(addButton);
        panel.add(removeButton);
        panel.add(sellButton);

        return panel;
    }

    private void addItem() {
        try {
            String name = nameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());

            if (name.isEmpty() || quantity < 0 || price < 0) {
                JOptionPane.showMessageDialog(this, "Please enter valid values");
                return;
            }

            Item item = new Item(name, quantity, price);
            inventory.add(item);
            updateTable();
            clearFields();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for quantity and price");
        }
    }

    private void removeItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow >= 0) {
            inventory.remove(selectedRow);
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove");
        }
    }

    private void sellItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow >= 0) {
            Item item = inventory.get(selectedRow);
            String quantityStr = JOptionPane.showInputDialog("Enter quantity to sell:");
            try {
                int sellQuantity = Integer.parseInt(quantityStr);
                if (sellQuantity <= item.getQuantity() && sellQuantity > 0) {
                    item.setQuantity(item.getQuantity() - sellQuantity);
                    if (item.getQuantity() == 0) {
                        item.setSoldDate(LocalDateTime.now());
                    }
                    updateTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid quantity");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to sell");
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Item item : inventory) {
            String status = item.getQuantity() > 0 ? "In Stock" : "Sold Out";
            String addedDate = item.getAddedDate().format(formatter);

            tableModel.addRow(new Object[]{
                    item.getName(),
                    item.getQuantity(),
                    String.format("$%.2f", item.getPrice()),
                    addedDate,
                    status
            });
        }
    }

    private void clearFields() {
        nameField.setText("");
        quantityField.setText("");
        priceField.setText("");
    }}