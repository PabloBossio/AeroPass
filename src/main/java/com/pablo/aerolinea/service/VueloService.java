package com.pablo.aerolinea.service;

import com.pablo.aerolinea.model.EstadoVuelo;
import com.pablo.aerolinea.model.Vuelo;
import com.pablo.aerolinea.repository.VueloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VueloService {

    private final VueloRepository vueloRepository;


    public VueloService(VueloRepository vueloRepository) {
        this.vueloRepository = vueloRepository;
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

    public Vuelo crearVuelo(Vuelo vuelo) {
        if (vuelo.getFechaLlegada().isBefore(vuelo.getFechaSalida())) {
            throw new IllegalArgumentException("La fecha de llegafa no puede ser anterior a la fecha de salida.");
        }
        if (vuelo.getPrecio().signum() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0.");
        }
        vuelo.setEstado(EstadoVuelo.PROGRAMADO);
        return vueloRepository.save(vuelo);
    }
}
