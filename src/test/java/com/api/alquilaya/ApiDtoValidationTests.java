package com.api.alquilaya;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.api.alquilaya.dto.AlquilerRequest;
import com.api.alquilaya.dto.ClienteRequest;
import com.api.alquilaya.models.EstadoAlquilerModel;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ApiDtoValidationTests {

    private final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = validatorFactory.getValidator();

    @AfterEach
    void cerrarValidador() {
        validatorFactory.close();
    }

    @Test
    void rechazaDatosDeClienteInvalidos() {
        ClienteRequest request = new ClienteRequest("", "Pérez", "correo-invalido", "LIC-123", "123");

        var camposInvalidos = validator.validate(request).stream()
            .map(error -> error.getPropertyPath().toString())
            .collect(Collectors.toSet());

        assertTrue(camposInvalidos.contains("nombre"));
        assertTrue(camposInvalidos.contains("email"));
        assertTrue(camposInvalidos.contains("telefono"));
    }

    @Test
    void rechazaAlquilerConFechasInvertidas() {
        AlquilerRequest request = new AlquilerRequest(
            1L,
            1L,
            LocalDate.of(2026, 11, 4),
            LocalDate.of(2026, 11, 1),
            EstadoAlquilerModel.PENDIENTE
        );

        assertTrue(validator.validate(request).stream()
            .anyMatch(error -> error.getPropertyPath().toString().equals("rangoFechasValido")));
    }
}
