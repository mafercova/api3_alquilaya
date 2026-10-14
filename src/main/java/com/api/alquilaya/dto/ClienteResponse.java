package com.api.alquilaya.dto;

import com.api.alquilaya.models.ClienteModel;

public record ClienteResponse(
    Long id,
    String nombre,
    String apellido,
    String email,
    String licencia,
    String telefono
) {
    public static ClienteResponse from(ClienteModel cliente) {
        return new ClienteResponse(
            cliente.getId(),
            cliente.getNombre(),
            cliente.getApellido(),
            cliente.getEmail(),
            cliente.getLicencia(),
            cliente.getTelefono()
        );
    }
}
