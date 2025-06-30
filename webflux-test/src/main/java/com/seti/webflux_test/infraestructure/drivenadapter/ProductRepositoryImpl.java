package com.seti.webflux_test.infraestructure.drivenadapter;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

import com.seti.webflux_test.domain.model.Product;
import com.seti.webflux_test.domain.port.ProductRepository;
import com.seti.webflux_test.infraestructure.entrypoint.web.exception.CustomException;
import com.seti.webflux_test.infraestructure.drivenadapter.data.ProductR2DBCEntity;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface SpringDataProductR2dbcRepository extends R2dbcRepository<ProductR2DBCEntity, Long> {

    @Query("SELECT * FROM product WHERE branch_id = :branchId ORDER BY stock DESC LIMIT 1")
    Mono<ProductR2DBCEntity> findMostStockedProductByBranchIdCustomQuery(Long id);
}

@Repository
@RequiredArgsConstructor

public class ProductRepositoryImpl implements ProductRepository {

    private final SpringDataProductR2dbcRepository springDataRepo;

    @Override
    public Mono<Product> save(Product product) {
        return springDataRepo.save(ProductR2DBCEntity.fromDomain(product))
                .map(ProductR2DBCEntity::toDomain);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return springDataRepo.existsById(id)
                .flatMap(exists -> {
                    if (Boolean.FALSE.equals(exists))
                        return Mono.error(new CustomException("El producto que intenta eliminar no existe."));

                    return springDataRepo.deleteById(id)
                            .onErrorResume(DataIntegrityViolationException.class, e -> Mono.error(new CustomException(
                                    "No se puede eliminar el producto debido a que se usa en otras partes del sistema.")));
                });
    }

    @Override
    public Mono<Product> findById(Long id) {
        return springDataRepo.findById(id).map(ProductR2DBCEntity::toDomain);
    }

    @Override
    public Mono<Product> findMostStockedProductByBranchId(Long id) {
        return springDataRepo.findMostStockedProductByBranchIdCustomQuery(id).map(ProductR2DBCEntity::toDomain);
    }

    @Override
    public Flux<Product> findAll() {
        return springDataRepo.findAll().map(ProductR2DBCEntity::toDomain);
    }
}
