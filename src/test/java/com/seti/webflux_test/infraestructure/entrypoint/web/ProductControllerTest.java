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

import com.seti.webflux_test.domain.model.Product;
import com.seti.webflux_test.domain.usecase.ProductUseCase;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.ProductCreateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.ProductUpdateRequestDTO;

import reactor.core.publisher.Mono;

@WebFluxTest(controllers = ProductController.class)
class ProductControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ProductUseCase productUseCase;

    @Test
    @DisplayName("POST /api/product - Debería crear un nuevo producto")
    void shouldCreateNewProduct() {

        ProductCreateRequestDTO req = ProductCreateRequestDTO
                .builder()
                .name("Nuevo producto")
                .stock(100L)
                .branchId(10L)
                .build();

        Product productCreated = Product
                .builder()
                .id(1L)
                .name("Nuevo producto")
                .stock(100L)
                .branchId(10L)
                .build();

        when(productUseCase.createProduct(any(Product.class))).thenReturn(Mono.just(productCreated));

        webTestClient.post().uri("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Nuevo producto")
                .jsonPath("$.branchId").isEqualTo(10L)
                .jsonPath("$.stock").isEqualTo(100L);

        verify(productUseCase, times(1)).createProduct(any(Product.class));
    }

    @Test
    @DisplayName("PUT /api/product - Debería actualizar un producto")
    void shouldUpdateProduct() {

        Long productId = 1L;

        ProductUpdateRequestDTO req = ProductUpdateRequestDTO
                .builder()
                .id(productId)
                .name("Producto actualizado")
                .branchId(20L)
                .build();

        Product originalProduct = Product
                .builder()
                .id(productId)
                .name("Producto original")
                .stock(100L)
                .branchId(10L)
                .build();

        Product updatedProduct = originalProduct.applyUpdates(req.toDomain());

        when(productUseCase.updateProduct(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        webTestClient.put().uri("/api/product")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(productId)
                .jsonPath("$.name").isEqualTo("Producto actualizado")
                .jsonPath("$.branchId").isEqualTo(20L)
                .jsonPath("$.stock").isEqualTo(100L);

        verify(productUseCase, times(1)).updateProduct(any(Product.class));
    }

    @Test
    @DisplayName("PATCH /api/product - Debería actualizar unicamente el stock de un producto")
    void shouldUpdateOnlyStockProduct() {

        Long originalStock = 100L;
        String productId = "1";
        String stock = "-100";

        // La idea aqui, es verificar que la diferencia entre el stock original
        // y la nueva cantidad (Negativa o positiva) se vea reflejada en el resultado
        Long deltaStock = originalStock + Long.valueOf(stock);

        Product updatedProduct = Product
                .builder()
                .id(1L)
                .name("Producto con stock actualizado")
                .stock(deltaStock)
                .branchId(10L)
                .build();

        when(productUseCase.updateStock(productId, stock)).thenReturn(Mono.just(updatedProduct));

        webTestClient.patch().uri("/api/product/{id}/stock/{quantity}", productId, stock)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1L)
                .jsonPath("$.name").isEqualTo("Producto con stock actualizado")
                .jsonPath("$.branchId").isEqualTo(10L)
                .jsonPath("$.stock").isEqualTo(deltaStock);

        verify(productUseCase, times(1)).updateStock(productId, stock);
    }

}
