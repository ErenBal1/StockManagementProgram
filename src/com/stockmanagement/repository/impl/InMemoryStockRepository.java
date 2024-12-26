package com.stockmanagement.repository.impl;

import com.stockmanagement.model.Stock;
import com.stockmanagement.repository.StockRepository;
import java.util.*;

public class InMemoryStockRepository implements StockRepository {
    private final Map<String, Stock> stocks = new HashMap<>();

    @Override
    public Optional<Stock> findById(String id) {
        return Optional.ofNullable(stocks.get(id));
    }

    @Override
    public Optional<Stock> findByName(String name) {
        return stocks.values().stream()
                .filter(stock -> stock.getName().equals(name))
                .findFirst();
    }

    @Override
    public List<Stock> findAll() {
        return new ArrayList<>(stocks.values());
    }

    @Override
    public void save(Stock stock) {
        stocks.put(stock.getId(), stock);
    }

    @Override
    public void update(Stock stock) {
        stocks.put(stock.getId(), stock);
    }

    @Override
    public void delete(String id) {
        stocks.remove(id);
    }
}