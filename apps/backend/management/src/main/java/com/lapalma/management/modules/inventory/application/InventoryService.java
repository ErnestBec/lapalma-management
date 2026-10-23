package com.lapalma.management.modules.inventory.application;

import com.lapalma.management.modules.inventory.domain.Stock;
import com.lapalma.management.modules.inventory.infrastructure.StockRepository;

public class InventoryService {
    
    private final StockRepository stockRepository;

    public InventoryService(StockRepository stockRepository){
        this.stockRepository = stockRepository;
    }

    public boolean canSell(Long productId, int quantity){
        
        if(productId == null || productId <=0){
            throw new IllegalArgumentException("El identificador del producto no es válido");
        }

        if(quantity<= 0){
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Stock stock = stockRepository
            .findByProductId(productId)
            .orElseThrow(()-> 
                new IllegalArgumentException("El producto no existe en inventario")    
            );

        return stock.availableQuantity() >= quantity;
    }

    public void addStock(Long productId, int quantity){

        if(productId == null || productId <= 0){
            throw new IllegalArgumentException("El identificador dep producto no es válida");    
        }

        if(quantity <= 0){
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        Stock currentStock = stockRepository
            .findByProductId(productId)
            .orElseThrow(()->
                new IllegalArgumentException("El producto no existe en inventario")
            );
        
        int updateQuantity = Math.addExact(currentStock.availableQuantity(), quantity);

        Stock updateStock = new Stock(productId, updateQuantity);
        
        stockRepository.save(updateStock);
        

        
    }

}
