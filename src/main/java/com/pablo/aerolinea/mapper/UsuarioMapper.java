package com.pablo.aerolinea.mapper;

import com.pablo.aerolinea.dto.UsuarioRequestDTO;
import com.pablo.aerolinea.dto.UsuarioResponseDTO;
import com.pablo.aerolinea.model.Usuario;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioRequestDTO dto) {
        return Usuario.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }

    public static UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setRol(usuario.getRol());
        dto.setEmail(usuario.getEmail());
        return dto;
    }
}
