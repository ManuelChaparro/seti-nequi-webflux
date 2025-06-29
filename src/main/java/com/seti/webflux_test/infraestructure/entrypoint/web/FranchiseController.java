package com.seti.webflux_test.infraestructure.entrypoint.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.seti.webflux_test.domain.model.Franchise;
import com.seti.webflux_test.domain.usecase.FranchiseUseCase;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.FranchiseCreateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.FranchiseUpdateRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/franchise")
@RequiredArgsConstructor
public class FranchiseController {

    private final FranchiseUseCase franchiseUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Franchise> create(@Valid @RequestBody FranchiseCreateRequestDTO franchiseRequestDTO){
        return franchiseUseCase.createFranchise(franchiseRequestDTO.toDomain());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<Franchise> update(@Valid @RequestBody FranchiseUpdateRequestDTO franchiseRequestDTO){
        return franchiseUseCase.updateFranchise(franchiseRequestDTO.toDomain());
    }
}