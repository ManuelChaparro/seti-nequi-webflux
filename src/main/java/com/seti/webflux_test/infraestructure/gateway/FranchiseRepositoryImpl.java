package com.seti.webflux_test.infraestructure.gateway;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.seti.webflux_test.domain.model.Franchise;
import com.seti.webflux_test.domain.port.FranchiseRepository;
import com.seti.webflux_test.infraestructure.gateway.data.FranchiseR2DBCEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface SpringDataFranchiseR2dbcRepository extends R2dbcRepository<FranchiseR2DBCEntity, Long> {

    Mono<Boolean> existsByName(String name);
}

@Repository
@RequiredArgsConstructor
public class FranchiseRepositoryImpl implements FranchiseRepository {

    private final SpringDataFranchiseR2dbcRepository springDataRepo;

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return springDataRepo.save(FranchiseR2DBCEntity.fromDomain(franchise))
                .map(FranchiseR2DBCEntity::toDomain);
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return springDataRepo.findById(id).map(FranchiseR2DBCEntity::toDomain);
    }

    @Override
    public Flux<Franchise> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return springDataRepo.existsByName(name);
    }
}
