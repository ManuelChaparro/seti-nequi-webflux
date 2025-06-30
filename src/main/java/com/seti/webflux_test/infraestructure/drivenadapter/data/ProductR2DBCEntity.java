package com.seti.webflux_test.infraestructure.drivenadapter.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.seti.webflux_test.domain.model.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("product")
public class ProductR2DBCEntity {
    
    @Id
    @Column("_id")
    private Long id;

    private String name;

    @Column("branch_id")
    private Long branchId;

    @Column("stock")
    private Long stock;

    public static ProductR2DBCEntity fromDomain(Product product) {
        return ProductR2DBCEntity.builder()
                .id(product.getId())
                .name(product.getName())
                .branchId(product.getBranchId())
                .stock(product.getStock())
                .build();
    }

    public Product toDomain() {
        return Product.builder()
                .id(this.id)
                .name(this.name)
                .branchId(this.branchId)
                .stock(this.stock)
                .build();
    }
}
