package com.lapalma.modules.inventory.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.lapalma.management.modules.inventory.application.InventoryService;
import com.lapalma.management.modules.inventory.domain.Stock;
import com.lapalma.management.modules.inventory.infrastructure.StockRepository;



@ExtendWith(MockitoExtension.class) 
public class InventoryServiceTest {
    private Long productId;

    @Captor 
    private ArgumentCaptor<Stock> stockCaptor;

    @BeforeEach 
    void setUp(){
        productId = 1L;
    }

    @Mock 
    //Objeto Simulado, controlamos sus respuestas (Crea un repositorio falso de prueba)
    private StockRepository stockRepository;
    
    @InjectMocks
    //Objeto real, ejecutamos su lógica (crea un servicio y le proporciona ese repositorio simulado)
    private InventoryService inventoryService;

   //Parametriza el test
    @ParameterizedTest 
    //El test se ejecutara tantas veces exista una cantidad en el arreglo de parametros
    @ValueSource(ints = {1, 5, 10, 15, 20})  
    void shouldAllowSaleWhenStockIsSufficent(int reqestedQuantity){

        //Arrage
        Long productId = 1L;

        when(stockRepository.findByProductId(productId))
            .thenReturn(Optional.of(new Stock(productId, 20)));

        //act
        boolean result = inventoryService.canSell(productId, reqestedQuantity);

        //Assert
        assertTrue(result);

        verify(stockRepository)
        .findByProductId(productId);
    }

    @Test 
    void shouldRejectSalesWhenStockIsInsufficient(){

        Long productId= 1L;
        
        when(stockRepository.findByProductId(productId))
            .thenReturn(Optional.of(new Stock(productId, 3)));

        boolean result = inventoryService.canSell(productId, 8);

        assertFalse(result);

        verify(stockRepository, times(1))
            .findByProductId(productId);
    }

    @Test
    void shouldRejectInvalidQuantity() {

        assertThrows(
            IllegalArgumentException.class,
            () -> inventoryService.canSell(1L, 0)
        );

        verifyNoInteractions(stockRepository);
    }

    @Test
    void shouldRejectUnknownProduct() {

        when(stockRepository.findByProductId(99L))
            .thenReturn(Optional.empty());

        assertThrows(
            IllegalArgumentException.class,
            () -> inventoryService.canSell(99L, 2)
        );

        verify(stockRepository)
            .findByProductId(99L);
    }

    @Test 
    void shouldAllowSaleWhenStockEqualsRequestedQuantity(){
        
        //Arrage
        Long productId = 1L;
        int availableStock =20;
        int requestesQuantity =20;

        when(stockRepository.findByProductId(productId))
            .thenReturn(Optional.of(new Stock(productId, availableStock)));

        //Act    
        boolean result = inventoryService.canSell(productId, requestesQuantity);

        //Assert
        assertTrue(result);

        verify(stockRepository).findByProductId(productId);
    }

     @Test
    void shouldAddStockCorrectly(){

        //Arrage
        //Inicalizamos un nuevo stock con un productId = 1L y cantidad de 20
        when(stockRepository.findByProductId(productId))
            .thenReturn(Optional.of(new Stock(productId, 20)));

        //Act
        inventoryService.addStock(productId, 10);

        //Asert 
        //Compruaba que se llamó a save() y captura el argumento enviado
        verify(stockRepository).save(stockCaptor.capture());

        //Nos permite inspeccionarlo como cualquier otro objeto Java
        Stock saveStock = stockCaptor.getValue();

        assertEquals(1L, saveStock.productId());

        assertEquals(30, saveStock.availableQuantity());
    }

    @Test 
    void shoulsRejectInvalidStockEntry(){

        //Act & Assert
        assertThrows(IllegalArgumentException.class,
        ()-> inventoryService.addStock(productId, 0));

        //Nos compruaba que nuestro servicio no hizo ninguna llamada al repositorio
        verifyNoInteractions(stockRepository);
    }

    @Test void shouldPropagateErrorWhenRepositoryFails(){

        //Arrage
        when(stockRepository.findByProductId(productId))
            .thenReturn(Optional.of(new Stock(productId, 20)));

        //Se simula que el repositorio fallo cuando intento guardar un objeto Stock. 
        when(stockRepository.save(any(Stock.class)))
            .thenThrow(new IllegalStateException("Error al guardar existencias"));
        
        //Act $ Assert
        IllegalStateException exception = assertThrows(
            IllegalStateException.class, 
            ()-> inventoryService.addStock(productId, 10
        ));

        assertEquals("Error al guardar existencias", exception.getMessage());
        verify(stockRepository).save(stockCaptor.capture());

        assertEquals(30, stockCaptor.getValue().availableQuantity());
            
    }

    @Test 
    void shouldReturnEmptyWhenProductDontExist(){
        //Arrage
        //Preparamos todo para la prueba
        Long productId = 12L;
        int quantity = 20;  
        
        //Simulamos que falla la prueba (No existe el producto, es decir que devuelve empty)
        when(stockRepository.findByProductId(productId))
            .thenReturn(Optional.empty());        
        
        //Act + Assert
        //Ejecutamos la operación que detonará lo que ya previamente configuramos
        assertThrows(
            IllegalArgumentException.class, // Representa el tipo de esa clase en tiempo de ejecución 
            () -> inventoryService.addStock(productId, quantity));
        
        //Assert
        //Comprobamos que se cumplio con la prueba
        verify(stockRepository, never())
            .save(any(Stock.class));

    }

}
