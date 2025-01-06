package stockManagementProgram.service.impl;

import stockManagementProgram.model.Stock;
import stockManagementProgram.model.enums.Unit;
import stockManagementProgram.repository.StockRepository;
import stockManagementProgram.service.StockService;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of stock management business logic.
 * Handles stock operations and validation.
 */
public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Adds or updates stock in the system
     * @param name Product name
     * @param quantity Quantity to add
     * @param price Unit price
     * @param unit Unit type
     */
    @Override
    public void addStock(String name, int quantity, double price, Unit unit) {
        Optional<Stock> existingStock = stockRepository.findByName(name);
        if (existingStock.isPresent()) {
            // Update existing stock
            Stock stock = existingStock.get();
            stock.addQuantity(quantity, price);
            stockRepository.update(stock);
        } else {
            // Create new stock entry
            Stock newStock = new Stock(name, quantity, price, unit);
            stockRepository.save(newStock);
        }
    }

    /**
     * Removes stock if sufficient quantity exists
     * @param name Product name
     * @param quantity Quantity to remove
     * @param price Sale price
     * @throws IllegalArgumentException if insufficient stock
     */
    @Override
    public void removeStock(String name, int quantity, double price) {
        Optional<Stock> existingStock = stockRepository.findByName(name);
        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();
            if (stock.getQuantity() >= quantity) {
                stock.removeQuantity(quantity, price);
                stockRepository.update(stock);
            } else {
                throw new IllegalArgumentException("Insufficient stock quantity!");
            }
        }
    }

    @Override
    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    @Override
    public Optional<Stock> findStock(String name) {
        return stockRepository.findByName(name);
    }

    @Override
    public boolean existsByName(String name) {
        return stockRepository.findByName(name).isPresent();
    }
}