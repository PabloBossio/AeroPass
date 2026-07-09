package com.pablo.aerolinea.dto;

import jakarta.validation.constraints.*;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VueloRequestDto {

    @NotBlank(message = "El origen es obligatorio")
    private String origen;

    @NotBlank(message = "El destino es obligatorio")
    private String destino;

    @NotNull(message = "La fecha de salida es obligatoria")
    @Future(message = "La fecha de salida debe ser futura")
    private LocalDateTime fechaSalida;

    @NotNull(message = "La fecha de llegada es obligatoria")
    private LocalDateTime fechaLlegada;

    @NotBlank(message = "La aerolinea es obligatoria")
    private String aerolinea;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayot a 0")
    private BigDecimal precio;

    @NotNull(message = "Los asientos disponibles son obligatorios")
    @Min(value = 1, message = "Debe haber al menos 1 asiento disponible")
    private Integer asientosDisponibles;
}
