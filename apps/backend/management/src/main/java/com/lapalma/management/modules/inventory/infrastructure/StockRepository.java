package com.lapalma.management.modules.inventory.infrastructure;

import java.util.Optional;

import com.lapalma.management.modules.inventory.domain.Stock;

public interface StockRepository {
    Optional<Stock> findByProductId(Long productId);
    Stock save(Stock stock);
} 
