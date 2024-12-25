package stockManagementProgram.service;

import stockManagementProgram.model.Stock;
import stockManagementProgram.model.enums.Unit;
import java.util.List;
import java.util.Optional;

public interface StockService {
    void addStock(String name, int quantity, double price, Unit unit);
    void removeStock(String name, int quantity, double price);
    List<Stock> getAllStocks();
    Optional<Stock> findStock(String name);
    boolean existsByName(String name);

}