package com.pablo.aerolinea.controller;

import com.pablo.aerolinea.config.SecurityConfig;
import com.pablo.aerolinea.dto.AvionRequestDTO;
import com.pablo.aerolinea.model.Avion;
import com.pablo.aerolinea.security.JwtUtil;
import com.pablo.aerolinea.security.UsuarioDetailsService;
import com.pablo.aerolinea.service.AvionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AvionController.class)
@Import(SecurityConfig.class)
public class AvionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper objectMapper;

    @MockitoBean
    private AvionService avionService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;


    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_conDatosValidos_deberiaDevolver201() throws Exception {
        AvionRequestDTO request = new AvionRequestDTO();
        request.setModelo("Boeing 737");
        request.setMatricula("ABC123");
        request.setCapacidad(180);
        request.setAerolinea("Aerolineas Argentinas");

        Avion avionCreado = Avion.builder()
                .id(1L)
                .modelo("Boeing 737")
                .matricula("ABC123")
                .capacidad(180)
                .aerolinea("Aerolineas Argentinas")
                .build();

        when(avionService.crearAvion(any(Avion.class))).thenReturn(avionCreado);

        mockMvc.perform(post("/api/aviones")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("ABC123"))
                .andExpect(jsonPath("$.capacidad").value(180));

    }

    @Test
    @WithAnonymousUser
    void crear_sinAutenticacion_deberiaDevolver403() throws Exception {
        AvionRequestDTO request = new AvionRequestDTO();
        request.setModelo("Boeing 737");
        request.setMatricula("ABC123");
        request.setCapacidad(180);
        request.setAerolinea("Aerolineas Argentinas");

        mockMvc.perform(post("/api/aviones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crear_conMatriculaFaltante_deberiaDevolver400() throws Exception {
        AvionRequestDTO request = new AvionRequestDTO();
        request.setModelo("Boeing 737");
        request.setCapacidad(180);
        request.setAerolinea("Aerolineas Argentinas");

        mockMvc.perform(post("/api/aviones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listar_deberiaDevolverListaAviones() throws Exception {
        Avion avion = Avion.builder()
                .id(1L)
                .modelo("Boeing 737")
                .matricula("ABC123")
                .capacidad(180)
                .aerolinea("Aerolineas Argentinas")
                .build();

        when(avionService.listarTodos()).thenReturn(List.of(avion));

        mockMvc.perform(get("/api/aviones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matricula").value("ABC123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorId_conIdExistente_deberiaDevolver200() throws Exception {
        Avion avion = Avion.builder()
                .id(1L)
                .modelo("Boeing 737")
                .matricula("ABC123")
                .capacidad(180)
                .aerolinea("Aerolineas Argentinas")
                .build();

        when(avionService.buscarPorId(1L)).thenReturn(Optional.of(avion));

        mockMvc.perform(get("/api/aviones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("ABC123"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void buscarPorId_conIdInexistente_deberiaDevolver404() throws Exception {
        when(avionService.buscarPorId(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/aviones/99"))
                .andExpect(status().isNotFound());
    }
}
