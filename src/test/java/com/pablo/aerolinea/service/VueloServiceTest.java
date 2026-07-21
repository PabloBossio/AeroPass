package com.pablo.aerolinea.service;

import com.pablo.aerolinea.exception.RecursoNoEncontradoException;
import com.pablo.aerolinea.exception.ReglaDeNegocioException;
import com.pablo.aerolinea.model.Avion;
import com.pablo.aerolinea.model.EstadoVuelo;
import com.pablo.aerolinea.model.Vuelo;
import com.pablo.aerolinea.repository.AvionRepository;
import com.pablo.aerolinea.repository.VueloRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VueloServiceTest {

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private AvionRepository avionRepository;

    @InjectMocks
    private VueloService vueloService;

    private Avion avionValido() {
        return Avion.builder()
                .id(1L)
                .modelo("Boeing 737")
                .matricula("ABC123")
                .capacidad(180)
                .aerolinea("Aerolineas Argentinas")
                .build();
    }

    private Vuelo vueloValido() {
        return Vuelo.builder()
                .origen("Buenos aires")
                .destino("Madrid")
                .fechaSalida(LocalDateTime.now().plusDays(10))
                .fechaLlegada(LocalDateTime.now().plusDays(10).plusHours(12))
                .precio(new BigDecimal("850.00"))
                .asientosDisponibles(150)
                .build();
    }

    @Test
    void crearVuelo_conDatosValidos_deberiaCrearCorrectamente() {
        Avion avion = avionValido();
        Vuelo vuelo = vueloValido();

        when(avionRepository.findById(1L)).thenReturn(Optional.of(avion));
        when(vueloRepository.save(vuelo)).thenReturn(vuelo);

        Vuelo resultado = vueloService.crearVuelo(vuelo, 1L);

        assertNotNull(resultado);
        assertEquals(EstadoVuelo.PROGRAMADO, resultado.getEstado());
        assertEquals(avion, resultado.getAvion());
        verify(vueloRepository, times(1)).save(vuelo);

    }

    @Test
    void crearVuelo_conAvionInexistente_deberiaLanzarRecursoNoEncontrado() {
        Vuelo vuelo = vueloValido();

        when(avionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> vueloService.crearVuelo(vuelo, 99L));

        verify(vueloRepository, never()).save(any());
    }

    @Test
    void crearVuelo_conAsientosSuperandoCapacidad_deberiaLanzarReglaDeNegocio() {
        Avion avion = avionValido();
        Vuelo vuelo = vueloValido();
        vuelo.setAsientosDisponibles(200);

        when(avionRepository.findById(1L)).thenReturn(Optional.of(avion));

        assertThrows(ReglaDeNegocioException.class, () -> vueloService.crearVuelo(vuelo, 1L));

        verify(vueloRepository, never()).save(any());
    }

    @Test
    void crearVuelo_conFechaLlegadaAnteriorFechaSalida_DeberialanzarReglaDeNegocio() {
        Avion avion = avionValido();
        Vuelo vuelo = vueloValido();
        vuelo.setFechaSalida(LocalDateTime.now().plusDays(10));
        vuelo.setFechaLlegada(LocalDateTime.now().plusDays(5));

        when(avionRepository.findById(1L)).thenReturn(Optional.of(avion));

        assertThrows(ReglaDeNegocioException.class, () -> vueloService.crearVuelo(vuelo, 1L));

        verify(vueloRepository, never()).save(any());
    }

    @Test
    void crearVuelo_conPrecioCero_deberiaLanzarReglaDeNegocio() {
        Avion avion = avionValido();
        Vuelo vuelo = vueloValido();
        vuelo.setPrecio(BigDecimal.ZERO);

        when(avionRepository.findById(1L)).thenReturn(Optional.of(avion));

        assertThrows(ReglaDeNegocioException.class, () -> vueloService.crearVuelo(vuelo, 1L));

        verify(vueloRepository, never()).save(any());
    }
}
