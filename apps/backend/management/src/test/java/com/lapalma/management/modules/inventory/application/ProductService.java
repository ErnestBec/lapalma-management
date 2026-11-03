package com.lapalma.management.modules.inventory.application;

import com.lapalma.management.modules.inventory.application.catalog.api.CreateProductRequest;
import com.lapalma.management.modules.inventory.application.catalog.api.ProductResponse;

public interface  ProductService {
    ProductResponse create(CreateProductRequest request);
}
