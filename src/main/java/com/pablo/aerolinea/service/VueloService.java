package com.pablo.aerolinea.service;

import com.pablo.aerolinea.exception.RecursoNoEncontradoException;
import com.pablo.aerolinea.exception.ReglaDeNegocioException;
import com.pablo.aerolinea.model.Avion;
import com.pablo.aerolinea.model.EstadoVuelo;
import com.pablo.aerolinea.model.Vuelo;
import com.pablo.aerolinea.repository.AvionRepository;
import com.pablo.aerolinea.repository.VueloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VueloService {

    private final VueloRepository vueloRepository;
    private final AvionRepository avionRepository;


    public VueloService(VueloRepository vueloRepository, AvionRepository avionRepository) {
        this.vueloRepository = vueloRepository;
        this.avionRepository = avionRepository;
    }

    public List<Vuelo> listarTodos() {
        return vueloRepository.findAll();
    }

    public Optional<Vuelo> buscarPorId(Long id) {
        return vueloRepository.findById(id);
    }

    public List<Vuelo> buscarPorOrigenYDestino(String origen, String destino) {
        return vueloRepository.findByOrigenAndDestino(origen, destino);
    }

    public Vuelo crearVuelo(Vuelo vuelo, Long avionId) {
        Avion avion = avionRepository.findById(avionId)
                .orElseThrow(()-> new RecursoNoEncontradoException("No existe un avión con id: " + avionId));

        if (vuelo.getAsientosDisponibles() > avion.getCapacidad()) {
            throw new ReglaDeNegocioException(
                    "Los asientos disponibles (" + vuelo.getAsientosDisponibles() +
                    ") no pueden superar la capacidad del avion (" + avion.getCapacidad() + ")");
        }
        if (vuelo.getFechaLlegada().isBefore(vuelo.getFechaSalida())) {
            throw new ReglaDeNegocioException("La fecha de llegafa no puede ser anterior a la fecha de salida.");
        }
        if (vuelo.getPrecio().signum() <= 0) {
            throw new ReglaDeNegocioException("El precio debe ser mayor a 0.");
        }

        vuelo.setAvion(avion);
        vuelo.setEstado(EstadoVuelo.PROGRAMADO);
        return vueloRepository.save(vuelo);
    }
}
