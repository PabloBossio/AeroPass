package com.pablo.aerolinea.dto;

import com.pablo.aerolinea.model.EstadoVuelo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VueloResponseDto {

    private Long id;
    private String origen;
    private String destino;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private BigDecimal precio;
    private Integer asientosDisponibles;
    private EstadoVuelo estado;
    private AvionResponseDTO avion;
}
