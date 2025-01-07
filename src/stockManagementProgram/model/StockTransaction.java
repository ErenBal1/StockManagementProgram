package stockManagementProgram.model;

public class StockTransaction {

    private final int quantity;
    private final double price;


    public StockTransaction(int quantity, double price) {
        this.quantity = quantity;
        this.price = price;

    }

    // Getters
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}
