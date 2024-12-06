import java.time.LocalDateTime;

public class Item {
    private String name;
    private int quantity;
    private double price;
    private LocalDateTime addedDate;
    private LocalDateTime soldDate;

    public Item(String name, int quantity, double price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.addedDate = LocalDateTime.now();
        this.soldDate = null;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public LocalDateTime getAddedDate() { return addedDate; }
    public LocalDateTime getSoldDate() { return soldDate; }
    public void setSoldDate(LocalDateTime soldDate) { this.soldDate = soldDate; }
}