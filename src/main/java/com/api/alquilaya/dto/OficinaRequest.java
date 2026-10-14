package com.api.alquilaya.dto;

import com.api.alquilaya.models.OficinaAlquilerModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OficinaRequest(
    @NotBlank @Size(max = 120) String nombreOficina,
    @NotBlank @Size(max = 255) String direccion,
    @NotBlank @Size(max = 100) String ciudad,
    @NotBlank @Size(max = 100) String region
) {
    public OficinaAlquilerModel toModel() {
        OficinaAlquilerModel oficina = new OficinaAlquilerModel();
        oficina.setNombreOficina(nombreOficina.trim());
        oficina.setDireccion(direccion.trim());
        oficina.setCiudad(ciudad.trim());
        oficina.setRegion(region.trim());
        return oficina;
    }
}
