package com.pablo.aerolinea.service;

import com.pablo.aerolinea.exception.ReglaDeNegocioException;
import com.pablo.aerolinea.model.Avion;
import com.pablo.aerolinea.repository.AvionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvionService {

    private final AvionRepository avionRepository;

    public AvionService(AvionRepository avionRepository) {
        this.avionRepository = avionRepository;
    }

    public List<Avion> listarTodos() {
        return avionRepository.findAll();
    }

    public Optional<Avion> buscarPorId(Long id) {
        return avionRepository.findById(id);
    }

    @Transactional
    public Avion crearAvion(Avion avion) {
        if (avionRepository.existsByMatricula(avion.getMatricula())) {
            throw new ReglaDeNegocioException("Ya existe un avión registrado con la matricula: " + avion.getMatricula());
        }
        return avionRepository.save(avion);
    }
}
