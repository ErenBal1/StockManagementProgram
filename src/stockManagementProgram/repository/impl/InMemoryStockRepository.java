package stockManagementProgram.repository.impl;

import stockManagementProgram.model.Stock;
import stockManagementProgram.repository.StockRepository;
import java.util.*;

/**
 * In-memory implementation of StockRepository.
 * Stores stock data in a HashMap for quick access and modification.
 * Suitable for testing and prototyping purposes.
 */
public class InMemoryStockRepository implements StockRepository {
    /**
     * In-memory storage for stock items
     */
    private final Map<String, Stock> stocks = new HashMap<>();


    /**
     * Performs case-sensitive name comparison
     * Returns first matching stock if multiple stocks have the same name
     */
    @Override
    public Optional<Stock> findByName(String name) {
        return stocks.values().stream()
                .filter(stock -> stock.getName().equals(name))
                .findFirst();
    }

    /**
     * Creates a new ArrayList to prevent external modification of internal collection
     */
    @Override
    public List<Stock> findAll() {
        return new ArrayList<>(stocks.values());
    }

    @Override
    public void save(Stock stock) {
        stocks.put(stock.getId(), stock);
    }

     //Updates existing stock with the same operation as save func

    @Override
    public void update(Stock stock) {
        stocks.put(stock.getId(), stock);
    }

}