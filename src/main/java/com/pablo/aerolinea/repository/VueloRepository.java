package com.pablo.aerolinea.repository;

import com.pablo.aerolinea.model.EstadoVuelo;
import com.pablo.aerolinea.model.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VueloRepository extends JpaRepository<Vuelo, Long>{

    List<Vuelo> findByOrigenAndDestino(String origen, String destino);

    List<Vuelo> findByEstado(EstadoVuelo estado);
}
