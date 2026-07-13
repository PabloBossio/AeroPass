package com.pablo.aerolinea.dto;

import lombok.Data;

@Data
public class AvionResponseDTO {

    private Long id;
    private String modelo;
    private String matricula;
    private Integer capacidad;
    private String aerolinea;
}
