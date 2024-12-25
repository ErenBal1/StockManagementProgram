package stockManagementProgram.model;

import stockManagementProgram.model.enums.TransactionType;

import java.time.LocalDateTime;

public class StockTransaction {
    private final LocalDateTime dateTime;
    private final int quantity;
    private final double price;
    private final TransactionType type;

    public StockTransaction(int quantity, double price, TransactionType type) {
        this.dateTime = LocalDateTime.now();
        this.quantity = quantity;
        this.price = price;
        this.type = type;
    }

    // Getters
    public LocalDateTime getDateTime() { return dateTime; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public TransactionType getType() { return type; }
}
