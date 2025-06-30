package com.seti.webflux_test.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Franchise {

    private Long id;
    private String name;

    public Franchise applyUpdates(Franchise updates) {
        return Franchise.builder()
                .id(this.id)
                .name(updates.getName() != null ? updates.getName() : this.name)
                .build();
    }

}
