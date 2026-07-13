package com.pablo.aerolinea.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aviones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false, unique = true)
    private String matricula;

    @Column(nullable = false)
    private Integer capacidad;

    @Column(nullable = false)
    private String aerolinea;
}
