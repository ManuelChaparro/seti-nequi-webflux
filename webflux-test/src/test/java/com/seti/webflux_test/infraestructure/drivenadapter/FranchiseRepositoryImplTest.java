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

import com.seti.webflux_test.domain.model.Franchise;
import com.seti.webflux_test.infraestructure.drivenadapter.data.FranchiseR2DBCEntity;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class) 
class FranchiseRepositoryImplTest {

    @Mock
    private SpringDataFranchiseR2dbcRepository springDataRepo;

    @InjectMocks
    private FranchiseRepositoryImpl franchiseRepositoryImpl;

    private Franchise testFranchise;
    private FranchiseR2DBCEntity testFranchiseEntity;

    @BeforeEach
    void setUp() {
        testFranchise = Franchise.builder()
                .id(1L)
                .name("Test Franchise")
                .build();

        testFranchiseEntity = FranchiseR2DBCEntity.builder()
                .id(1L)
                .name("Test Franchise")
                .build();
    }

    @Test
    @DisplayName("CREATE / Crear una franquicia")
    void shouldReturnSavedFranchise() {
        when(springDataRepo.save(any(FranchiseR2DBCEntity.class)))
                .thenReturn(Mono.just(testFranchiseEntity));

        Mono<Franchise> result = franchiseRepositoryImpl.save(testFranchise);

        StepVerifier.create(result)
                .expectNext(testFranchise)
                .verifyComplete();

        verify(springDataRepo, times(1)).save(any(FranchiseR2DBCEntity.class));
    }

    @Test
    @DisplayName("GET / Busca una Franquicia por ID")
    void shouldReturnFranchiseById() {
        when(springDataRepo.findById(1L)).thenReturn(Mono.just(testFranchiseEntity));

        Mono<Franchise> result = franchiseRepositoryImpl.findById(1L);

        StepVerifier.create(result)
                .expectNext(testFranchise)
                .verifyComplete();

        verify(springDataRepo, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET / Busca la lista de Franquicias")
    void shouldReturnFranchiseList() {
        Franchise anotherFranchise = Franchise.builder().id(2L).name("Franquicia 2").build();
        FranchiseR2DBCEntity anotherFranchiseEntity = FranchiseR2DBCEntity.fromDomain(anotherFranchise);

        when(springDataRepo.findAll()).thenReturn(Flux.just(testFranchiseEntity, anotherFranchiseEntity));

        Flux<Franchise> result = franchiseRepositoryImpl.findAll();

        StepVerifier.create(result)
                .expectNext(testFranchise)
                .expectNext(anotherFranchise)
                .verifyComplete();

        verify(springDataRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("GET / Busca una Franquicia por nombre")
    void shouldReturnIfExistFranchiseByName() {
        when(springDataRepo.existsByName("Test Franchise")).thenReturn(Mono.just(true));

        Mono<Boolean> result = franchiseRepositoryImpl.existsByName("Test Franchise");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(springDataRepo, times(1)).existsByName("Test Franchise");
    }
}