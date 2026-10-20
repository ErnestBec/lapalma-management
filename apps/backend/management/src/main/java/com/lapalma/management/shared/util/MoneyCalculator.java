package com.lapalma.management.shared.util;

import java.math.BigDecimal;
import java.util.Objects;

public class MoneyCalculator {
    
    public BigDecimal calculateSubtotal(BigDecimal unitPrice, int quantity){
     Objects.requireNonNull(unitPrice, "El precio es obligatorio");
     
     if(unitPrice.signum() < 0){
        throw new IllegalArgumentException("El precio no puede ser negativo");
     }

     if(quantity <= 0){
        throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
     }

     return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
