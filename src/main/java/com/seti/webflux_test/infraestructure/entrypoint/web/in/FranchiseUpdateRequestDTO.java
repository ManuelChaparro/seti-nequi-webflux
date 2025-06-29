package com.seti.webflux_test.infraestructure.entrypoint.web.in;

import com.seti.webflux_test.domain.model.Franchise;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FranchiseUpdateRequestDTO {

    @NotNull(message = "El campo id es obligatorio.")
    @Min(value = 0, message = "Error en formulario.")
    Long id;

    @NotNull(message = "El campo name es obligatorio.")
    @NotEmpty(message = "El campo name es obligatorio.")
    @NotBlank(message = "El campo name es obligatorio.")
    @Size(min = 3, max = 100, message = "El campo name debe tener entre 3 a 10 caracteres.")
    String name;

    public Franchise toDomain() {
        return Franchise.builder().id(id).name(name).build();
    }
}