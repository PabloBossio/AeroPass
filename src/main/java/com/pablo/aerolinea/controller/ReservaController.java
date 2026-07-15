package com.pablo.aerolinea.controller;

import com.pablo.aerolinea.dto.ReservaRequestDTO;
import com.pablo.aerolinea.dto.ReservaResponseDTO;
import com.pablo.aerolinea.mapper.ReservarMapper;
import com.pablo.aerolinea.model.Reserva;
import com.pablo.aerolinea.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    public final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    public ResponseEntity<ReservaResponseDTO> crear(@Valid @RequestBody ReservaRequestDTO request) {
        Reserva creada = reservaService.crearReserva(request.getUsuarioId(), request.getVueloId());
        return  ResponseEntity.status(HttpStatus.CREATED).body(ReservarMapper.toResponseDTO(creada));
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ReservaResponseDTO> cancelar(@PathVariable Long id) {
        Reserva cancelada = reservaService.cancelarReserva(id);
        return ResponseEntity.ok(ReservarMapper.toResponseDTO(cancelada));
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<ReservaResponseDTO> listarPorUsuario(@PathVariable Long usuarioId) {
        return reservaService.listarPorUsuario(usuarioId).stream()
                .map(ReservarMapper::toResponseDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaResponseDTO> bucarPorId(@PathVariable Long id) {
        return reservaService.buscarPorId(id)
                .map(ReservarMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }






}
