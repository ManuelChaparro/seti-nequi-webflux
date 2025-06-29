package com.seti.webflux_test.infraestructure.entrypoint.web.out;

import com.seti.webflux_test.domain.model.Product;

public record ProductStockReport (
    String branchName,
    Product product
){}