package com.pablo.aerolinea.service;

import org.springframework.stereotype.Service;

@Service
public class HelloService {

    public String saludar(String nombre) {
        return "Hola, " + nombre + "¡Bienvenido al sistema de reservas para aerolineas!";
    }
}
