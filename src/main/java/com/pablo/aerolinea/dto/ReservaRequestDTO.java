package com.pablo.aerolinea.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservaRequestDTO {

    @NotNull(message = "El id de usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "El id de vuelo es obligatorio")
    private Long vueloId;
}
