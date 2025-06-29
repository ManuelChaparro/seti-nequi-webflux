package com.seti.webflux_test.domain.usecase;

import org.springframework.stereotype.Service;

import com.seti.webflux_test.domain.model.Franchise;
import com.seti.webflux_test.domain.port.FranchiseRepository;
import com.seti.webflux_test.infraestructure.entrypoint.web.exception.CustomException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.save(franchise);
    }

    public Flux<Franchise> listFranchises() {
        return franchiseRepository.findAll();
    }

    public Mono<Franchise> getFranchise(Long id) {
        return franchiseRepository.findById(id);
    }

    public Mono<Franchise> updateFranchise(Franchise franchise) {
        return franchiseRepository.findById(franchise.getId())
            .switchIfEmpty(Mono.error(new CustomException("La franquicia que desea actualizar no existe.")))
            .flatMap(existingFranchise -> {
                Franchise updatedFranchise = existingFranchise.applyUpdates(franchise);
                return franchiseRepository.save(updatedFranchise);
            });
    }
}