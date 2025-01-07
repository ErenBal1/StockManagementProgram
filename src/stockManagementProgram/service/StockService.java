package stockManagementProgram.service;

import stockManagementProgram.model.Stock;
import stockManagementProgram.model.enums.Unit;
import java.util.List;

public interface StockService {
    void addStock(String name, int quantity, double price, Unit unit);
    List<Stock> getAllStocks();
    boolean existsByName(String name);

}