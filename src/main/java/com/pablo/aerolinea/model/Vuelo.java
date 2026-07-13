package com.pablo.aerolinea.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vuelos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String origen;

    @Column(nullable = false)
    private String destino;

    @Column(name = "fecha_salida",nullable = false)
    private LocalDateTime fechaSalida;

    @Column(name = "fecha_llegada",nullable = false)
    private LocalDateTime fechaLlegada;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "asientos_disponibles",nullable = false)
    private Integer asientosDisponibles;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVuelo estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avion_id", nullable = false)
    private Avion avion;
}
