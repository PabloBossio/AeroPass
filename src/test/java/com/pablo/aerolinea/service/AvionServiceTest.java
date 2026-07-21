package com.pablo.aerolinea.service;

import com.pablo.aerolinea.exception.ReglaDeNegocioException;
import com.pablo.aerolinea.model.Avion;
import com.pablo.aerolinea.repository.AvionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvionServiceTest {

    @Mock
    private AvionRepository avionRepository;

    @InjectMocks
    private AvionService avionService;

    @Test
    void crearAvion_conMatriculaNueva_deberiaGuardarCorrectamente() {
        Avion avion = Avion.builder()
                .modelo("Boeing 737")
                .matricula("ABC123")
                .capacidad(180)
                .aerolinea("Aerolineas Argentinas")
                .build();

        when(avionRepository.existsByMatricula("ABC123")).thenReturn(false);
        when(avionRepository.save(avion)).thenReturn(avion);

        Avion resultado = avionService.crearAvion(avion);

        assertNotNull(resultado);
        assertEquals("ABC123", resultado.getMatricula());
        verify(avionRepository, times(1)).save(avion);
    }

    @Test
    void crearAvion_conMatriculaExistente_deberiaLanzarExcepcion() {
        Avion avion = Avion.builder()
                .matricula("ABC123")
                .build();

        when(avionRepository.existsByMatricula("ABC123")).thenReturn(true);

        assertThrows(ReglaDeNegocioException.class, () -> avionService.crearAvion(avion));

        verify(avionRepository, never()).save(any());
    }

}
