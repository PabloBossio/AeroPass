package com.pablo.aerolinea.mapper;

import com.pablo.aerolinea.dto.ReservaResponseDTO;
import com.pablo.aerolinea.model.Reserva;

public class ReservarMapper {

    public  static ReservaResponseDTO toResponseDTO(Reserva reserva) {
        ReservaResponseDTO dto = new ReservaResponseDTO();
        dto.setId(reserva.getId());
        dto.setUsuario(UsuarioMapper.toResponseDTO(reserva.getUsuario()));
        dto.setVuelo(VueloMapper.toResponseDto(reserva.getVuelo()));
        dto.setFechaReserva(reserva.getFechaReserva());
        dto.setPrecioPagado(reserva.getPrecioPagado());
        dto.setEstado(reserva.getEstado());
        return dto;
    }
}
