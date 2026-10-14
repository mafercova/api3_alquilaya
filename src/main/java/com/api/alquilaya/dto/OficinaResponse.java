package com.api.alquilaya.dto;

import com.api.alquilaya.models.OficinaAlquilerModel;

public record OficinaResponse(
    Long id,
    String nombreOficina,
    String direccion,
    String ciudad,
    String region
) {
    public static OficinaResponse from(OficinaAlquilerModel oficina) {
        return new OficinaResponse(
            oficina.getId(),
            oficina.getNombreOficina(),
            oficina.getDireccion(),
            oficina.getCiudad(),
            oficina.getRegion()
        );
    }
}
