package com.pablo.aerolinea.controller;

import com.pablo.aerolinea.dto.AvionRequestDTO;
import com.pablo.aerolinea.dto.AvionResponseDTO;
import com.pablo.aerolinea.mapper.AvionMapper;
import com.pablo.aerolinea.model.Avion;
import com.pablo.aerolinea.service.AvionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aviones")
public class AvionController {

    private final AvionService avionService;

    public AvionController(AvionService avionService) {
        this.avionService = avionService;
    }

    @PostMapping
    public ResponseEntity<AvionResponseDTO> crear(@Valid @RequestBody AvionRequestDTO request) {
        Avion creado = avionService.crearAvion(AvionMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(AvionMapper.toResponseDTO(creado));
    }

    @GetMapping
    public List<AvionResponseDTO> listar() {
        return avionService.listarTodos().stream()
                .map(AvionMapper::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvionResponseDTO> buscarPorId(@PathVariable Long id) {
        return avionService.buscarPorId(id)
                .map(AvionMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }



}
