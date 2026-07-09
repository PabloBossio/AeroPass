package com.pablo.aerolinea.mapper;

import com.pablo.aerolinea.dto.VueloRequestDto;
import com.pablo.aerolinea.dto.VueloResponseDto;
import com.pablo.aerolinea.model.Vuelo;

public class VueloMapper {

    public static Vuelo toEntity(VueloRequestDto dto) {
        return Vuelo.builder()
                .origen(dto.getOrigen())
                .destino(dto.getDestino())
                .fechaSalida(dto.getFechaSalida())
                .fechaLlegada(dto.getFechaLlegada())
                .aerolinea(dto.getAerolinea())
                .precio(dto.getPrecio())
                .asientosDisponibles(dto.getAsientosDisponibles())
                .build();
    }

    public static VueloResponseDto toResponseDto(Vuelo vuelo) {
        VueloResponseDto dto = new VueloResponseDto();
        dto.setId(vuelo.getId());
        dto.setOrigen(vuelo.getOrigen());
        dto.setDestino(vuelo.getDestino());
        dto.setFechaSalida(vuelo.getFechaSalida());
        dto.setFechaLlegada(vuelo.getFechaLlegada());
        dto.setAeerolinea(vuelo.getAerolinea());
        dto.setPrecio(vuelo.getPrecio());
        dto.setAsientosDisponibles(vuelo.getAsientosDisponibles());
        dto.setEstado(vuelo.getEstado());
        return dto;
    }
}
