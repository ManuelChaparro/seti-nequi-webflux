package com.seti.webflux_test.infraestructure.entrypoint.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.seti.webflux_test.domain.model.Branch;
import com.seti.webflux_test.domain.usecase.BranchUseCase;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.BranchCreateRequestDTO;
import com.seti.webflux_test.infraestructure.entrypoint.web.in.BranchUpdateRequestDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/branch")
@RequiredArgsConstructor

public class BranchController {
    
    private final BranchUseCase branchUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Branch> create(@Valid @RequestBody BranchCreateRequestDTO branchRequestDTO){
        return branchUseCase.createBranch(branchRequestDTO.toDomain());
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<Branch> update(@Valid @RequestBody BranchUpdateRequestDTO branchRequestDTO){
        return branchUseCase.updateBranch(branchRequestDTO.toDomain());
    }
}
