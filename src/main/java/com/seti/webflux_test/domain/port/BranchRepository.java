package com.seti.webflux_test.domain.port;

import com.seti.webflux_test.domain.model.Branch;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {

    Mono<Branch> save(Branch branch);

    Mono<Branch> findById(Long id);

    Flux<Branch> findAll();

    Flux<Branch> findByFranchiseId(Long id);
}
