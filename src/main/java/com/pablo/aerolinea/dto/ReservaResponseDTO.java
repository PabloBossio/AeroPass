package com.pablo.aerolinea.dto;

import com.pablo.aerolinea.model.EstadoReserva;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservaResponseDTO {

    private Long id;
    private UsuarioResponseDTO usuario;
    private VueloResponseDto vuelo;
    private LocalDateTime fechaReserva;
    private BigDecimal precioPagado;
    private EstadoReserva estado;
}
