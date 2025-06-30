package com.seti.webflux_test.infraestructure.entrypoint.web.in;

import com.seti.webflux_test.domain.model.Branch;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class BranchUpdateRequestDTO {

    @NotNull(message = "El campo id es obligatorio.")
    @Min(value = 0, message = "Error en formulario.")
    Long id;

    @NotNull(message = "El campo name es obligatorio.")
    @NotEmpty(message = "El campo name es obligatorio.")
    @NotBlank(message = "El campo name es obligatorio.")
    @Size(min = 3, max = 100, message = "El campo name debe tener entre 3 a 10 caracteres.")
    String name;

    @Min(value = 0, message = "Error en el formulario.")
    Long franchiseId;

    public Branch toDomain() {
        return Branch.builder().id(id).name(name).franchiseId(franchiseId).build();
    }
}