package com.seti.webflux_test.infraestructure.gateway.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import com.seti.webflux_test.domain.model.Branch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("branch")

public class BranchR2DBCEntity {
    
    @Id
    @Column("_id")
    private Long id;

    private String name;

    @Column("franchise_id")
    private Long franchiseId;

    public static BranchR2DBCEntity fromDomain(Branch domain) {
        return BranchR2DBCEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .franchiseId(domain.getFranchiseId())
                .build();
    }

    public Branch toDomain() {
        return Branch.builder()
                .id(this.id)
                .name(this.name)
                .franchiseId(this.franchiseId)
                .build();
    }
}