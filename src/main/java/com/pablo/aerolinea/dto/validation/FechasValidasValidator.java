package com.pablo.aerolinea.dto.validation;

import com.pablo.aerolinea.dto.VueloRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FechasValidasValidator implements ConstraintValidator<FechasValidas, VueloRequestDto> {

    @Override
    public boolean isValid(VueloRequestDto dto, ConstraintValidatorContext context) {
        if (dto.getFechaSalida() == null || dto.getFechaLlegada() == null) {
            return true; // si falta alguna, que se encargue @NotNull de esas
        }
        return dto.getFechaLlegada().isAfter(dto.getFechaSalida());
    }
}