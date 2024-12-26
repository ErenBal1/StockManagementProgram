package com.stockmanagement.repository;

import com.stockmanagement.model.Stock;
import java.util.List;
import java.util.Optional;

public interface StockRepository {
    Optional<Stock> findById(String id);
    Optional<Stock> findByName(String name);
    List<Stock> findAll();
    void save(Stock stock);
    void update(Stock stock);
    void delete(String id);
}