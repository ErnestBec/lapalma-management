package com.lapalma.management.catalog.api;

import java.math.BigDecimal;

public record ProductResponse (
    Long id,
    String name,
    BigDecimal price
) {}
