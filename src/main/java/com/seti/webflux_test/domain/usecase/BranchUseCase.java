package com.seti.webflux_test.domain.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.seti.webflux_test.domain.model.Branch;
import com.seti.webflux_test.domain.port.BranchRepository;
import com.seti.webflux_test.infraestructure.entrypoint.web.exception.CustomException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor

public class BranchUseCase {

    private final BranchRepository branchRepository;

    private final Logger logger = LoggerFactory.getLogger(BranchUseCase.class);

    public Mono<Branch> createBranch(Branch branch) {
        return branchRepository.save(branch);
    }

    public Flux<Branch> listBranchs() {
        return branchRepository.findAll();
    }

    public Mono<Branch> getBranch(Long id) {
        return branchRepository.findById(id);
    }

    public Mono<Branch> updateBranch(Branch branch) {
        return branchRepository.findById(branch.getId())
                .switchIfEmpty(Mono.error(new CustomException("La sucursal que desea actualizar no existe.")))
                .flatMap(existingBranch -> {
                    Branch updatedBranch = existingBranch.applyUpdates(branch);
                    return branchRepository.save(updatedBranch);
                })
                .doOnNext(updatedItem ->
                // Cumplimiento punto 4. OnNext / DoOnNext
                // Simulamos el envío de un correo al usuario notificando que
                // la información del producto fue actualizada exitosamente
                logger.info("Sucursal actualizada: {}", updatedItem));
    }
}
