package com.seti.webflux_test.domain.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.seti.webflux_test.domain.model.Branch;
import com.seti.webflux_test.domain.port.BranchRepository;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.BranchCreateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.BranchUpdateRequestDTO;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {
    
    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private BranchUseCase branchUseCase;

    private BranchCreateRequestDTO testCreateRequestDTO;
    private BranchUpdateRequestDTO testUpdateRequestDTO;
    private Branch savedBranchWithId;
    private Branch originalUpdatedBranchWithId;
    private Branch updatedBranchWithId;

    @BeforeEach
    void setUp() {
        // Variables para test de creacion
        testCreateRequestDTO = BranchCreateRequestDTO.builder().name("Sucursal nueva").franchiseId(10L).build();
        testUpdateRequestDTO = BranchUpdateRequestDTO.builder().id(10L).name("Sucursal actualizada").franchiseId(20L).build();
        savedBranchWithId = Branch.builder().id(1L).name("Sucursal nueva").franchiseId(10L).build();
        updatedBranchWithId = Branch.builder().id(10L).name("Sucursal actualizada").franchiseId(20L).build();
        originalUpdatedBranchWithId = Branch.builder().id(5L).name("Sucursal original").franchiseId(2L).build();
    }

    @Test
    @DisplayName("SAVE / Logica de creación de una Sucursal")
    void shouldSaveBranch() {
        when(branchRepository.save(any(Branch.class)))
                .thenReturn(Mono.just(savedBranchWithId));

        StepVerifier.create(branchUseCase.createBranch(testCreateRequestDTO.toDomain()))
                .expectNext(savedBranchWithId)
                .verifyComplete();

        verify(branchRepository, times(1)).save(any(Branch.class));
    }

    @Test
    @DisplayName("UPDATE / Logica de actualización de una Franquicia")
    void shouldUpdateBranch() {
        when(branchRepository.findById(testUpdateRequestDTO.getId()))
                .thenReturn(Mono.just(originalUpdatedBranchWithId));

        when(branchRepository.save(any(Branch.class)))
                .thenReturn(Mono.just(updatedBranchWithId));

        StepVerifier.create(branchUseCase.updateBranch(testUpdateRequestDTO.toDomain()))
                .expectNext(updatedBranchWithId)
                .verifyComplete();

        verify(branchRepository, times(1)).findById(testUpdateRequestDTO.getId());
        verify(branchRepository, times(1)).save(any(Branch.class));
    }

}
