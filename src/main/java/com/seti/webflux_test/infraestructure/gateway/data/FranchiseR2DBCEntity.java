package com.seti.webflux_test.infraestructure.gateway.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.seti.webflux_test.domain.model.Franchise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("franchise")
public class FranchiseR2DBCEntity {
    
    @Id
    @Column("_id")
    private Long id;

    private String name;

    public static FranchiseR2DBCEntity fromDomain(Franchise domain) {
        return FranchiseR2DBCEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .build();
    }

    public Franchise toDomain() {
        return Franchise.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }
}