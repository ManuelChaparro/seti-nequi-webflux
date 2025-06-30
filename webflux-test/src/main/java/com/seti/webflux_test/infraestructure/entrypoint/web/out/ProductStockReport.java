package com.seti.webflux_test.infraestructure.entrypoint.web.out;

import com.seti.webflux_test.domain.model.Product;

import lombok.Builder;

@Builder
public record ProductStockReport (
    String branchName,
    Product product
){}