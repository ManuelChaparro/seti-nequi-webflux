package com.seti.webflux_test.infraestructure.entrypoint.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.seti.webflux_test.domain.model.Branch;
import com.seti.webflux_test.domain.usecase.BranchUseCase;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.BranchCreateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.BranchUpdateRequestDTO;

import reactor.core.publisher.Mono;

@WebFluxTest(controllers = BranchController.class)
class BranchControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BranchUseCase branchUseCase;

    @Test
    @DisplayName("POST /api/branch - Debería crear una nueva sucursal")
    void shouldCreateNewBranch() {
        BranchCreateRequestDTO requestDTO = BranchCreateRequestDTO.builder().name("Sucursal").franchiseId(10L).build();
        Branch createdBranch = Branch.builder().id(1L).name("Sucursal").franchiseId(10L).build();

        when(branchUseCase.createBranch(any(Branch.class)))
                .thenReturn(Mono.just(createdBranch));

        webTestClient.post().uri("/api/branch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1L)
                .jsonPath("$.name").isEqualTo("Sucursal");

        verify(branchUseCase, times(1)).createBranch(any(Branch.class));
    }

    @Test
    @DisplayName("PUT /api/branch - Debería actualizar una sucursal existente")
    void shouldUpdateExistingBranch() {
        Long branchId = 1L;

        BranchUpdateRequestDTO updateRequestDTO = BranchUpdateRequestDTO.builder()
                .id(branchId)
                .name("Sucursal a actualizar")
                .franchiseId(10L)
                .build();

        Branch originalBranch = Branch.builder()
                .id(branchId)
                .name("Sucursal anterior")
                .franchiseId(20L)
                .build();

        Branch updatedBranchDomain = originalBranch.applyUpdates(updateRequestDTO.toDomain());

        when(branchUseCase.updateBranch(any(Branch.class)))
                .thenReturn(Mono.just(updatedBranchDomain));

        webTestClient.put().uri("/api/branch")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(branchId)
                .jsonPath("$.franchiseId").isEqualTo(10L)
                .jsonPath("$.name").isEqualTo("Sucursal a actualizar");

        verify(branchUseCase, times(1)).updateBranch(any(Branch.class));
    }
}