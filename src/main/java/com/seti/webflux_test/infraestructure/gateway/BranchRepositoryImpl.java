package com.seti.webflux_test.infraestructure.gateway;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.seti.webflux_test.domain.model.Branch;
import com.seti.webflux_test.domain.port.BranchRepository;
import com.seti.webflux_test.infraestructure.gateway.data.BranchR2DBCEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface SpringDataBranchR2dbcRepository extends R2dbcRepository<BranchR2DBCEntity, Long> {
}

@Repository
@RequiredArgsConstructor
public class BranchRepositoryImpl implements BranchRepository{
    
    private final SpringDataBranchR2dbcRepository springDataRepo;

    @Override
    public Mono<Branch> save(Branch branch) {
        return springDataRepo.save(BranchR2DBCEntity.fromDomain(branch))
                .map(BranchR2DBCEntity::toDomain);
    }

    @Override
    public Mono<Branch> findById(Long id) {
        return springDataRepo.findById(id).map(BranchR2DBCEntity::toDomain);
    }

    @Override
    public Flux<Branch> findAll() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    }
}
