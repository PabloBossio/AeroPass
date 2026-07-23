package com.pablo.aerolinea.controller;

import com.pablo.aerolinea.dto.VueloRequestDto;
import com.pablo.aerolinea.dto.VueloResponseDto;
import com.pablo.aerolinea.mapper.VueloMapper;
import com.pablo.aerolinea.model.Vuelo;
import com.pablo.aerolinea.service.VueloService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vuelos")
public class VueloController {

    private final VueloService vueloService;

    public VueloController(VueloService vueloService) {
        this.vueloService = vueloService;
    }

    @PostMapping
    public ResponseEntity<VueloResponseDto> crear(@Valid @RequestBody VueloRequestDto request) {
        Vuelo creado = vueloService.crearVuelo(VueloMapper.toEntity(request), request.getAvionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(VueloMapper.toResponseDto(creado));
    }

    @GetMapping
    public List<VueloResponseDto> listar() {
        return vueloService.listarTodos().stream()
                .map(VueloMapper::toResponseDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VueloResponseDto> buscarPorId(@PathVariable Long id) {
        return vueloService.buscarPorId(id)
                .map(VueloMapper::toResponseDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public List<VueloResponseDto> buscarPorRuta(@RequestParam String origen, @RequestParam String destino) {
        return vueloService.buscarPorOrigenYDestino(origen, destino).stream()
                .map(VueloMapper::toResponseDto)
                .toList();
    }

}
