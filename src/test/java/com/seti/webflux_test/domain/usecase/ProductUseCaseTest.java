package com.seti.webflux_test.domain.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.seti.webflux_test.domain.model.Product;
import com.seti.webflux_test.domain.port.ProductRepository;
import com.seti.webflux_test.infraestructure.entrypoint.web.exception.CustomException;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.ProductCreateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.ProductUpdateRequestDTO;

import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductUseCase productUseCase;

    private ProductCreateRequestDTO testCreateRequestDTO;
    private ProductUpdateRequestDTO testUpdateRequestDTO;
    private Product savedProductWithId;
    private Product originalUpdatedProductWithId;
    private Product updatedProductWithId;

    @BeforeEach
    void setUp() {
        // Variables para test de creacion
        testCreateRequestDTO = ProductCreateRequestDTO.builder().name("Producto nuevo").stock(100L).branchId(10L)
                .build();
        testUpdateRequestDTO = ProductUpdateRequestDTO.builder().id(10L).name("Producto actualizado").branchId(20L)
                .build();
        savedProductWithId = Product.builder().id(1L).name("Producto nuevo").stock(100L).branchId(10L).build();
        updatedProductWithId = Product.builder().id(10L).name("Producto actualizado").branchId(20L).build();
        originalUpdatedProductWithId = Product.builder().id(5L).name("Producto original").stock(50L).branchId(2L)
                .build();

    }

    @Test
    @DisplayName("SAVE / Logica de creación de un Producto")
    void shouldSaveProduct() {
        when(productRepository.save(any(Product.class)))
                .thenReturn(Mono.just(savedProductWithId));

        StepVerifier.create(productUseCase.createProduct(testCreateRequestDTO.toDomain()))
                .expectNext(savedProductWithId)
                .verifyComplete();

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("UPDATE / Logica de actualización de un Producto")
    void shouldUpdateProduct() {
        when(productRepository.findById(testUpdateRequestDTO.getId()))
                .thenReturn(Mono.just(originalUpdatedProductWithId));

        when(productRepository.save(any(Product.class)))
                .thenReturn(Mono.just(updatedProductWithId));

        StepVerifier.create(productUseCase.updateProduct(testUpdateRequestDTO.toDomain()))
                .expectNext(updatedProductWithId)
                .verifyComplete();

        verify(productRepository, times(1)).findById(testUpdateRequestDTO.getId());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("UPDATE / Debería actualizar el stock del producto con cantidad positiva")
    void shouldUpdateStockWithPositiveQuantityAndLogSuccess() {
        String productIdStr = "1";
        String quantityStr = "10";
        Long productId = 1L;
        Long initialStock = 50L;
        Long quantityDifference = 10L;

        Product existingProduct = Product.builder().id(productId).name("Laptop").stock(initialStock).branchId(10L)
                .build();
        Product updatedProduct = existingProduct.applyStockChanges(quantityDifference);

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(productUseCase.updateStock(productIdStr, quantityStr))
                .expectNext(updatedProduct)
                .verifyComplete();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("UPDATE / Debería actualizar el stock del producto con cantidad negativa sin resultar en stock negativo")
    void shouldUpdateStockWithNegativeQuantityAndLogSuccess() {
        String productIdStr = "2";
        String quantityStr = "-5";
        Long productId = 2L;
        Long initialStock = 20L;
        Long quantityDifference = -5L;

        Product existingProduct = Product.builder().id(productId).name("Teclado").stock(initialStock).branchId(10L)
                .build();
        Product updatedProduct = existingProduct.applyStockChanges(quantityDifference);

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(productUseCase.updateStock(productIdStr, quantityStr))
                .expectNext(updatedProduct)
                .verifyComplete();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("UPDATE / Debería retornar un error si el ID del producto no es un número válido")
    void shouldReturnErrorIfProductIdIsNotValidNumber() {
        String invalidProductId = "abc";
        String quantity = "10";

        StepVerifier.create(productUseCase.updateStock(invalidProductId, quantity))
                .expectErrorMatches(
                        e -> e instanceof CustomException && e.getMessage().equals("Error en el formulario."))
                .verify();

        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("UPDATE / Debería retornar un error si la cantidad no es un número válido")
    void shouldReturnErrorIfQuantityIsNotValidNumber() {
        String productId = "1";
        String invalidQuantity = "xyz";

        StepVerifier.create(productUseCase.updateStock(productId, invalidQuantity))
                .expectErrorMatches(
                        e -> e instanceof CustomException && e.getMessage().equals("Error en el formulario."))
                .verify();

        verify(productRepository, never()).findById(anyLong());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("UPDATE / Debería retornar un error si el producto no existe")
    void shouldReturnErrorIfProductDoesNotExist() {
        String productIdStr = "99";
        String quantityStr = "5";
        Long productId = 99L;

        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateStock(productIdStr, quantityStr))
                .expectErrorMatches(e -> e instanceof CustomException
                        && e.getMessage().equals("El producto al cual desea actualizar el stock no existe."))
                .verify();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("UPDATE / Debería retornar un error si el stock resultante es negativo")
    void shouldReturnErrorIfResultingStockIsNegative() {
        String productIdStr = "3";
        String quantityStr = "-30";
        Long productId = 3L;
        Long initialStock = 20L;

        Product existingProduct = Product.builder().id(productId).name("Mouse").stock(initialStock).branchId(10L)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(existingProduct));

        StepVerifier.create(productUseCase.updateStock(productIdStr, quantityStr))
                .expectErrorMatches(e -> e instanceof CustomException
                        && e.getMessage().equals("Error -> La cantidad resultante de stock es negativa."))
                .verify();

        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, never()).save(any(Product.class));
    }
}
