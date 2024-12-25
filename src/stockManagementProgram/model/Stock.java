package stockManagementProgram.model;

import stockManagementProgram.model.enums.TransactionType;
import stockManagementProgram.model.enums.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Stock {
    private final String id;
    private String name;
    private int quantity;
    private double price;
    private Unit unit;
    private List<StockTransaction> transactions;

    public Stock(String name, int quantity, double price, Unit unit) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.unit = unit;
        this.transactions = new ArrayList<>();
        addTransaction(quantity, price, TransactionType.ADDITION);
    }

    public void addQuantity(int quantity, double price) {
        this.quantity += quantity;
        this.price = price;
        addTransaction(quantity, price, TransactionType.ADDITION);
    }

    public void removeQuantity(int quantity, double price) {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;
            addTransaction(quantity, price, TransactionType.REMOVAL);
        }
    }

    private void addTransaction(int quantity, double price, TransactionType type) {
        transactions.add(new StockTransaction(quantity, price, type));
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public Unit getUnit() { return unit; }
    public List<StockTransaction> getTransactions() { return transactions; }
}