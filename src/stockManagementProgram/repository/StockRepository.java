package stockManagementProgram.repository;

import stockManagementProgram.model.Stock;
import java.util.List;
import java.util.Optional;

public interface StockRepository {
    Optional<Stock> findByName(String name);
    List<Stock> findAll();
    void save(Stock stock);
    void update(Stock stock);

}