package com.seti.webflux_test.domain.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.seti.webflux_test.domain.model.Branch;
import com.seti.webflux_test.domain.model.Franchise;
import com.seti.webflux_test.domain.model.Product;
import com.seti.webflux_test.domain.port.BranchRepository;
import com.seti.webflux_test.domain.port.FranchiseRepository;
import com.seti.webflux_test.domain.port.ProductRepository;
import com.seti.webflux_test.infraestructure.entrypoint.web.exception.CustomException;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.FranchiseCreateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.FranchiseUpdateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.out.ProductStockReport;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;

    private FranchiseCreateRequestDTO testCreateRequestDTO;
    private FranchiseUpdateRequestDTO testUpdateRequestDTO;
    private Franchise savedFranchiseWithId;
    private Franchise originalUpdatedFranchiseWithId;
    private Franchise updatedFranchiseWithId;

    private Long testFranchiseId;
    private Franchise testFranchise;
    private Branch branch1;
    private Branch branch2;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        // Variables para test de creacion
        testCreateRequestDTO = FranchiseCreateRequestDTO.builder().name("Franquicia nueva").build();
        testUpdateRequestDTO = FranchiseUpdateRequestDTO.builder().id(10L).name("Franquicia actualizada").build();
        savedFranchiseWithId = Franchise.builder().id(1L).name("Franquicia nueva").build();
        updatedFranchiseWithId = Franchise.builder().id(10L).name("Franquicia actualizada").build();
        originalUpdatedFranchiseWithId = Franchise.builder().id(5L).name("Franquicia original").build();

        // Variables para test de busqueda de stock
        testFranchiseId = 1L;
        testFranchise = Franchise.builder().id(testFranchiseId).name("Franquicia A").build();
        branch1 = Branch.builder().id(10L).name("Sucursal 1").franchiseId(testFranchiseId).build();
        branch2 = Branch.builder().id(20L).name("Sucursal 2").franchiseId(testFranchiseId).build();
        product1 = Product.builder().id(100L).name("Producto X").stock(500L).branchId(branch1.getId()).build();
        product2 = Product.builder().id(200L).name("Producto Y").stock(700L).branchId(branch2.getId()).build();
    }

    @Test
    @DisplayName("SAVE / Logica de creación de una Franquicia")
    void shouldSaveFranchise() {
        when(franchiseRepository.existsByName(testCreateRequestDTO.getName()))
                .thenReturn(Mono.just(false));

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(savedFranchiseWithId));

        StepVerifier.create(franchiseUseCase.createFranchise(testCreateRequestDTO.toDomain()))
                .expectNext(savedFranchiseWithId)
                .verifyComplete();

        verify(franchiseRepository, times(1)).existsByName(testCreateRequestDTO.getName());
        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("SAVE / Error en creación de Franquicia por nombre repetido")
    void shouldThrowErrSaveFranchiseByRepeatedName() {
        when(franchiseRepository.existsByName(testCreateRequestDTO.getName()))
                .thenReturn(Mono.just(true));

        StepVerifier.create(franchiseUseCase.createFranchise(testCreateRequestDTO.toDomain()))
                .expectErrorMatches(CustomException.class::isInstance)
                .verify();

        verify(franchiseRepository, times(1)).existsByName(testCreateRequestDTO.getName());
        verify(franchiseRepository, never()).save(any(Franchise.class));
    }

    @Test
    @DisplayName("UPDATE / Logica de actualización de una Franquicia")
    void shouldUpdateFranchise() {
        when(franchiseRepository.existsByName(testUpdateRequestDTO.getName()))
                .thenReturn(Mono.just(false));

        when(franchiseRepository.findById(testUpdateRequestDTO.getId()))
                .thenReturn(Mono.just(originalUpdatedFranchiseWithId));

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(updatedFranchiseWithId));

        StepVerifier.create(franchiseUseCase.updateFranchise(testUpdateRequestDTO.toDomain()))
                .expectNext(updatedFranchiseWithId)
                .verifyComplete();

        verify(franchiseRepository, times(1)).existsByName(testUpdateRequestDTO.getName());
        verify(franchiseRepository, times(1)).findById(testUpdateRequestDTO.getId());
        verify(franchiseRepository, times(1)).save(any(Franchise.class));
    }

    @Test
    @DisplayName("FIND / Debería retornar un reporte de stock por sucursal para una franquicia por id")
    void shouldReturnProductStockReportForExistingFranchise() {
        when(franchiseRepository.findById(testFranchiseId)).thenReturn(Mono.just(testFranchise));
        when(branchRepository.findByFranchiseId(testFranchiseId))
                .thenReturn(Flux.fromIterable(Arrays.asList(branch1, branch2)));
        when(productRepository.findMostStockedProductByBranchId(branch1.getId())).thenReturn(Mono.just(product1));
        when(productRepository.findMostStockedProductByBranchId(branch2.getId())).thenReturn(Mono.just(product2));

        StepVerifier.create(franchiseUseCase.findProductWithMoreStock(testFranchiseId.toString()))
                .expectNextMatches(report -> {
                    assertEquals("Franquicia A", report.getFranchiseName());
                    assertEquals(2, report.getProductStockReport().size());

                    ProductStockReport psr1 = report.getProductStockReport().get(0);
                    assertEquals("Sucursal 1", psr1.branchName());
                    assertEquals(product1.getName(), psr1.product().getName());
                    assertEquals(product1.getStock(), psr1.product().getStock());

                    ProductStockReport psr2 = report.getProductStockReport().get(1);
                    assertEquals("Sucursal 2", psr2.branchName());
                    assertEquals(product2.getName(), psr2.product().getName());
                    assertEquals(product2.getStock(), psr2.product().getStock());
                    return true;
                })
                .verifyComplete();

        verify(franchiseRepository, times(1)).findById(testFranchiseId);
        verify(branchRepository, times(1)).findByFranchiseId(testFranchiseId);
        verify(productRepository, times(1)).findMostStockedProductByBranchId(branch1.getId());
        verify(productRepository, times(1)).findMostStockedProductByBranchId(branch2.getId());
    }

    @Test
    @DisplayName("Debería retornar error si el ID de la franquicia no es un número válido")
    void shouldReturnErrorIfFranchiseIdIsInvalidNumber() {
        String invalidId = "12345a";

        StepVerifier.create(franchiseUseCase.findProductWithMoreStock(invalidId))
                .expectErrorMatches(
                        e -> e instanceof CustomException && e.getMessage().equals("Error en el formulario."))
                .verify();

        verify(franchiseRepository, never()).findById(anyLong());
        verify(branchRepository, never()).findByFranchiseId(anyLong());
        verify(productRepository, never()).findMostStockedProductByBranchId(anyLong());
    }
}
