package com.pablo.aerolinea.mapper;

import com.pablo.aerolinea.dto.AvionRequestDTO;
import com.pablo.aerolinea.dto.AvionResponseDTO;
import com.pablo.aerolinea.model.Avion;

public class AvionMapper {

    public static Avion toEntity(AvionRequestDTO dto) {
        return Avion.builder()
                .modelo(dto.getModelo())
                .matricula(dto.getMatricula())
                .capacidad(dto.getCapacidad())
                .aerolinea(dto.getAerolinea())
                .build();
    }

    public static AvionResponseDTO toResponseDTO(Avion avion) {
        AvionResponseDTO dto = new AvionResponseDTO();
        dto.setId(avion.getId());
        dto.setModelo(avion.getModelo());
        dto.setMatricula(avion.getMatricula());
        dto.setCapacidad(avion.getCapacidad());
        dto.setAerolinea(avion.getAerolinea());
        return dto;
    }

}
