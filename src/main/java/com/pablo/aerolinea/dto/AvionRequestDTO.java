package com.pablo.aerolinea.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AvionRequestDTO {

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    @NotBlank(message = "La matricula es obligatoria")
    private String matricula;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser almenos 1")
    private Integer capacidad;

    @NotBlank(message = "La aerolinea es obligatoria")
    private String aerolinea;
}
