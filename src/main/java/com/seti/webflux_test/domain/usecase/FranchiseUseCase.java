package com.seti.webflux_test.domain.usecase;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.seti.webflux_test.domain.model.Franchise;
import com.seti.webflux_test.domain.port.BranchRepository;
import com.seti.webflux_test.domain.port.FranchiseRepository;
import com.seti.webflux_test.domain.port.ProductRepository;
import com.seti.webflux_test.infraestructure.entrypoint.web.exception.CustomException;
import com.seti.webflux_test.infraestructure.entrypoint.web.out.ProductStockReport;
import com.seti.webflux_test.infraestructure.entrypoint.web.out.StockPerBranchInFranchiseReport;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    private final Logger logger = LoggerFactory.getLogger(FranchiseUseCase.class);

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
                })
                .doOnNext(updatedItem ->
                // Cumplimiento punto 4. OnNext / DoOnNext
                // Simulamos el envío de un correo al usuario notificando que
                // la información del producto fue actualizada exitosamente
                logger.info("Franquicia actualizada: {}", updatedItem));
    }

    public Mono<StockPerBranchInFranchiseReport> findProductWithMoreStock(String id) {

        Long franchiseId;

        try {
            franchiseId = Long.valueOf(id);
        } catch (NumberFormatException e) {
            return Mono.error(new CustomException("Error en el formulario."));
        }

        Mono<Franchise> franchiseMono = franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new CustomException("La franquicia que busca no existe.")));

        Mono<List<ProductStockReport>> branchReportMono = branchRepository.findByFranchiseId(franchiseId)
                .flatMap(branch -> productRepository.findMostStockedProductByBranchId(branch.getId())
                        .map(product -> new ProductStockReport(branch.getName(), product))
                        .switchIfEmpty(Mono.just(new ProductStockReport(branch.getName(), null)))

                )
                .collectList();

        return Mono.zip(
                franchiseMono,
                branchReportMono,
                (franchise, branchReports) -> new StockPerBranchInFranchiseReport(franchise.getName(), branchReports));
    }
}