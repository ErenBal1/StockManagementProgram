package stockManagementProgram.model;

import stockManagementProgram.model.enums.Unit;
import java.util.UUID;

/**
 * Represents a stock item in the inventory system.
 * Manages stock properties and transaction history.
 */
public class Stock {
    private final String id;
    private final String name;
    private int quantity;
    private double price;
    private final Unit unit;



    /**
     * Creates a new stock item with initial quantity
     */
    public Stock(String name, int quantity, double price, Unit unit) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.unit = unit;
    }


    /**
     * Adds quantity to existing stock
     */
    public void addQuantity(int quantity, double price) {
        this.quantity += quantity;
        this.price = price;
    }


    /**
     * Removes quantity from stock if available
     */
    public void removeQuantity(int quantity) {
        if (this.quantity >= quantity) {
            this.quantity -= quantity;

        }
    }



    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public Unit getUnit() { return unit; }
}