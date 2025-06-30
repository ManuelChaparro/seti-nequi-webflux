package com.seti.webflux_test.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Branch {

    private Long id;
    private String name;
    private Long franchiseId;

    public Branch applyUpdates(Branch updates) {
        return Branch.builder()
                .id(this.id)
                .name(updates.getName() != null ? updates.getName() : this.name)
                .franchiseId(updates.getFranchiseId() != null ? updates.getFranchiseId() : this.franchiseId)
                .build();
    }
}