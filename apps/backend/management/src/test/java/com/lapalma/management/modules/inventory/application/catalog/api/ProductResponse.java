package com.lapalma.management.modules.inventory.application.catalog.api;

import java.math.BigDecimal;

public record ProductResponse (
    Long id,
    String name,
    BigDecimal price
) {}
