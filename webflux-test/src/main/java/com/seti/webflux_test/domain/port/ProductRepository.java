package com.seti.webflux_test.domain.port;

import com.seti.webflux_test.domain.model.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {

    Mono<Product> save(Product product);

    Mono<Void> delete(Long id);

    Mono<Product> findById(Long id);

    Mono<Product> findMostStockedProductByBranchId(Long id);

    Flux<Product> findAll();
}
