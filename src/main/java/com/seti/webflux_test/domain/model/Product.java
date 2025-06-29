package com.seti.webflux_test.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Product {

    private Long id;
    private String name;
    private Long stock;
    private Long branchId;

    public Product applyUpdates(Product updates) {
        return Product.builder()
                .id(this.id)
                .name(updates.getName() != null ? updates.getName() : this.name)
                .stock(updates.getStock() != null ? updates.getStock() : this.stock)
                .branchId(updates.getBranchId() != null ? updates.getBranchId() : this.branchId)
                .build();
    }

    public Product applyStockChanges(Long quantity) {
        return Product.builder()
                .id(this.id)
                .name(this.name)
                .stock(stock + quantity)
                .branchId(this.branchId)
                .build();
    }
}