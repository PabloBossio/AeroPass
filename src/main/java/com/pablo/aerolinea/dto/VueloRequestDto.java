package com.pablo.aerolinea.dto;

import com.pablo.aerolinea.dto.validation.FechasValidas;
import com.pablo.aerolinea.model.Avion;
import jakarta.validation.constraints.*;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@FechasValidas
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

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "Los asientos disponibles son obligatorios")
    @Min(value = 1, message = "Debe haber al menos 1 asiento disponible")
    private Integer asientosDisponibles;

    @NotNull(message = "El id del avion es obligatorio")
    private Long avionId;
}
