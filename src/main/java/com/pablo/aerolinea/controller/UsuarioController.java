package com.pablo.aerolinea.controller;

import com.pablo.aerolinea.dto.UsuarioRequestDTO;
import com.pablo.aerolinea.dto.UsuarioResponseDTO;
import com.pablo.aerolinea.mapper.UsuarioMapper;
import com.pablo.aerolinea.model.Usuario;
import com.pablo.aerolinea.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> registrar(@Valid @RequestBody UsuarioRequestDTO request) {
        Usuario creado = usuarioService.registrar(UsuarioMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponseDTO(creado));
    }

    @GetMapping
    public List<UsuarioResponseDTO> listar() {
        return usuarioService.listarTodos().stream()
                .map(UsuarioMapper::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> bucarPorId(@PathVariable Long id) {
        return usuarioService.buscarPorId(id)
                .map(UsuarioMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
