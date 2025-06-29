package com.seti.webflux_test.infraestructure.entrypoint.web;

import com.seti.webflux_test.domain.model.Franchise;
import com.seti.webflux_test.domain.model.Product;
import com.seti.webflux_test.domain.usecase.FranchiseUseCase;
import com.seti.webflux_test.infraestructure.entrypoint.web.exception.CustomException;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.FranchiseCreateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.FranchiseUpdateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.out.ProductStockReport;
import com.seti.webflux_test.infraestructure.entrypoint.web.out.StockPerBranchInFranchiseReport;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

@WebFluxTest(controllers = FranchiseController.class)
class FranchiseControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FranchiseUseCase franchiseUseCase;

    @Test
    @DisplayName("POST /api/franchise - Debería crear una nueva franquicia")
    void shouldCreateNewFranchise() {
        FranchiseCreateRequestDTO requestDTO = new FranchiseCreateRequestDTO("Franquicia de Prueba");
        Franchise createdFranchise = Franchise.builder().id(1L).name("Franquicia de Prueba").build();

        when(franchiseUseCase.createFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(createdFranchise));

        webTestClient.post().uri("/api/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1L)
                .jsonPath("$.name").isEqualTo("Franquicia de Prueba");

        verify(franchiseUseCase, times(1)).createFranchise(any(Franchise.class));
    }

    @Test
    @DisplayName("PUT /api/franchise - Debería actualizar una franquicia existente")
    void shouldUpdateExistingFranchise() {
        Long franchiseId = 1L;

        FranchiseUpdateRequestDTO updateRequestDTO = FranchiseUpdateRequestDTO.builder()
                .id(franchiseId)
                .name("Franquicia a actualizar")
                .build();

        Franchise originalFranchise = Franchise.builder()
                .id(franchiseId)
                .name("Franquicia anterior")
                .build();

        Franchise updatedFranchiseDomain = originalFranchise.applyUpdates(updateRequestDTO.toDomain());

        when(franchiseUseCase.updateFranchise(any(Franchise.class)))
                .thenReturn(Mono.just(updatedFranchiseDomain));

        webTestClient.put().uri("/api/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestDTO)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(franchiseId)
                .jsonPath("$.name").isEqualTo("Franquicia a actualizar");

        verify(franchiseUseCase, times(1)).updateFranchise(any(Franchise.class));
    }

    @Test
    @DisplayName("PUT /api/franchise - Debería retornar error si la franquicia no existe al actualizar")
    void shouldReturnErrorWhenUpdatingFranchise() {
        Long nonExistentId = 99L;
        FranchiseUpdateRequestDTO updateRequestDTO = FranchiseUpdateRequestDTO.builder()
                .id(nonExistentId)
                .name("Registro incorrecto")
                .build();

        when(franchiseUseCase.updateFranchise(any(Franchise.class)))
                .thenReturn(Mono.error(new CustomException("ERR")));

        webTestClient.put().uri("/api/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequestDTO)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("ERR");

        verify(franchiseUseCase, times(1)).updateFranchise(any(Franchise.class));
    }

    @Test
    @DisplayName("GET /api/franchise/{id}/productsWithMoreStock")
    void shouldReturnProductsWithMoreStockByFranchiseId() {
        String franchiseId = "1";

        Product product = Product.builder().id(100L).name("Producto con mas stock").stock(12345L).build();

        ProductStockReport productStock = ProductStockReport.builder().branchName("Sucursal").product(product).build();

        List<ProductStockReport> listResult = List.of(productStock);

        StockPerBranchInFranchiseReport res = StockPerBranchInFranchiseReport
                .builder()
                .franchiseName("Franquicia")
                .productStockReport(listResult)
                .build();

        when(franchiseUseCase.findProductWithMoreStock(franchiseId)).thenReturn(Mono.just(res));

        webTestClient.get().uri("/api/franchise/{id}/productsWithMoreStock", franchiseId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.franchiseName").isEqualTo("Franquicia")
                .jsonPath("$.productStockReport[0].branchName").isEqualTo("Sucursal")
                .jsonPath("$.productStockReport[0].product.stock").isEqualTo(12345L);

        verify(franchiseUseCase, times(1)).findProductWithMoreStock(franchiseId);
    }
}