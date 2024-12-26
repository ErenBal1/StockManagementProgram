package com.stockmanagement.service.impl;

import com.stockmanagement.model.Stock;
import com.stockmanagement.model.enums.Unit;
import com.stockmanagement.repository.StockRepository;
import com.stockmanagement.service.StockService;
import java.util.List;
import java.util.Optional;

public class StockServiceImpl implements StockService {
    private final StockRepository stockRepository;

    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void addStock(String name, int quantity, double price, Unit unit) {
        Optional<Stock> existingStock = stockRepository.findByName(name);
        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();
            stock.addQuantity(quantity, price);
            stockRepository.update(stock);
        } else {
            Stock newStock = new Stock(name, quantity, price, unit);
            stockRepository.save(newStock);
        }
    }

    @Override
    public void removeStock(String name, int quantity, double price) {
        Optional<Stock> existingStock = stockRepository.findByName(name);
        if (existingStock.isPresent()) {
            Stock stock = existingStock.get();
            if (stock.getQuantity() >= quantity) {
                stock.removeQuantity(quantity, price);
                stockRepository.update(stock);
            } else {
                throw new IllegalArgumentException("Insufficient stock!");
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