package com.pablo.aerolinea.dto;

import com.pablo.aerolinea.model.Rol;
import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
}
