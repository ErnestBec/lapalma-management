package com.lapalma.management.modules.inventory.application;

import com.lapalma.management.catalog.api.CreateProductRequest;
import com.lapalma.management.catalog.api.ProductResponse;

public interface  ProductService {
    ProductResponse create(CreateProductRequest request);
}
