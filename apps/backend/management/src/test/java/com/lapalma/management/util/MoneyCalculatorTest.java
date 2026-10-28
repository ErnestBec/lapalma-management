package com.lapalma.management.util;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.lapalma.management.shared.util.MoneyCalculator;


public class MoneyCalculatorTest {

    private final MoneyCalculator calculator = new MoneyCalculator();

    @Test    
    void shouldCalculateSubtotalCorrectly(){

        //Arrage
        BigDecimal price = new BigDecimal("25.50");
        int quantity = 4;

        //Act
        BigDecimal result = calculator.calculateSubtotal(price, quantity);

        //Assert
        assertEquals(
            0,
            new BigDecimal("102.00").compareTo(result)
        );
    }

    @Test 
    void shouldRejectNegativePrice(){
        //Arrage
        BigDecimal price = new BigDecimal("-10");

        //Act
        assertThrows(
            IllegalArgumentException.class, 
            //Assert
            () -> calculator.calculateSubtotal(price, 2)
        );

    }

    @Test 
    void shouldRejectZeroQuantity(){
        assertThrows(
            IllegalArgumentException.class, 
            ()-> calculator.calculateSubtotal(new BigDecimal("10"), 0)
        );
    }
}
