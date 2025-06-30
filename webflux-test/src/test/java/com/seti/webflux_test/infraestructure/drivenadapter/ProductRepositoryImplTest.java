package com.seti.webflux_test.infraestructure.drivenadapter;

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

import com.seti.webflux_test.domain.model.Product;
import com.seti.webflux_test.infraestructure.drivenadapter.data.ProductR2DBCEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryImplTest {

    @Mock
    private SpringDataProductR2dbcRepository springDataRepo;

    @InjectMocks
    private ProductRepositoryImpl productRepositoryImpl;

    private Product testProduct;
    private ProductR2DBCEntity testProductEntity;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .name("Test Product")
                .stock(100L)
                .branchId(10L)
                .build();

        testProductEntity = ProductR2DBCEntity.fromDomain(testProduct);
    }

    @Test
    @DisplayName("CREATE / Crea un producto")
    void shouldReturnSavedProduct() {
        when(springDataRepo.save(any(ProductR2DBCEntity.class)))
                .thenReturn(Mono.just(testProductEntity));

        Mono<Product> result = productRepositoryImpl.save(testProduct);

        StepVerifier.create(result)
                .expectNext(testProduct)
                .verifyComplete();

        verify(springDataRepo, times(1)).save(any(ProductR2DBCEntity.class));
    }

    @Test
    @DisplayName("DELETE / Elimina un producto si existe")
    void shouldDeleteWhenProductExists() {
        when(springDataRepo.existsById(1L)).thenReturn(Mono.just(true));
        when(springDataRepo.deleteById(1L)).thenReturn(Mono.empty());

        Mono<Void> result = productRepositoryImpl.delete(1L);

        StepVerifier.create(result)
                .verifyComplete();

        verify(springDataRepo, times(1)).existsById(1L);
        verify(springDataRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("GET / Elimina un producto si existe")
    void shouldReturnProductById() {
        when(springDataRepo.findById(1L)).thenReturn(Mono.just(testProductEntity));

        Mono<Product> result = productRepositoryImpl.findById(1L);

        StepVerifier.create(result)
                .expectNext(testProduct)
                .verifyComplete();

        verify(springDataRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET / Busca el producto con mas stock por Branch Id")
    void shouldReturnMostStockedProductByBranchId() {
        Product highStockProduct = Product.builder().id(2L).name("Producto con mas stock").stock(200L).branchId(10L)
                .build();
        ProductR2DBCEntity highStockProductEntity = ProductR2DBCEntity.fromDomain(highStockProduct);

        when(springDataRepo.findMostStockedProductByBranchIdCustomQuery(10L))
                .thenReturn(Mono.just(highStockProductEntity));

        Mono<Product> result = productRepositoryImpl.findMostStockedProductByBranchId(10L);

        StepVerifier.create(result)
                .expectNext(highStockProduct)
                .verifyComplete();

        verify(springDataRepo, times(1)).findMostStockedProductByBranchIdCustomQuery(10L);
    }

    @Test
    @DisplayName("GET / Busca la lista de productos")
    void shouldReturnProductsList() {
        Product anotherProduct = Product.builder().id(2L).name("Otro producto").stock(50L).branchId(11L).build();
        ProductR2DBCEntity anotherProductEntity = ProductR2DBCEntity.fromDomain(anotherProduct);

        when(springDataRepo.findAll()).thenReturn(Flux.just(testProductEntity, anotherProductEntity));

        Flux<Product> result = productRepositoryImpl.findAll();

        StepVerifier.create(result)
                .expectNext(testProduct)
                .expectNext(anotherProduct)
                .verifyComplete();

        verify(springDataRepo, times(1)).findAll();
    }

}
