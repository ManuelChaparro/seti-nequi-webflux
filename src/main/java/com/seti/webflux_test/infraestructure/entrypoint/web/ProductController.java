package com.seti.webflux_test.infraestructure.entrypoint.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.seti.webflux_test.domain.model.Product;
import com.seti.webflux_test.domain.usecase.ProductUseCase;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.ProductCreateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.ProductUpdateRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    
    private final ProductUseCase productUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> create(@Valid @RequestBody ProductCreateRequestDTO req){
        return productUseCase.createProduct(req.toDomain());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<Product> update(@Valid @RequestBody ProductUpdateRequestDTO req){
        return productUseCase.updateProduct(req.toDomain());
    }

    @PatchMapping("/{id}/stock/{quantity}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Product> updateStock(@PathVariable String id, @PathVariable String quantity){
        return productUseCase.updateStock(id, quantity);
    }
}
