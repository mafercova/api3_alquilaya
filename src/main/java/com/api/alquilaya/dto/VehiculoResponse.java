package com.api.alquilaya.dto;

import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.models.EstadoVehiculoModel;

public record VehiculoResponse(
    Long id,
    String marca,
    String modelo,
    Integer anio,
    String placa,
    Double tarifaDiaria,
    EstadoVehiculoModel estado,
    OficinaResponse oficina
) {
    public static VehiculoResponse from(VehiculoModel vehiculo) {
        return new VehiculoResponse(
            vehiculo.getId(),
            vehiculo.getMarca(),
            vehiculo.getModelo(),
            vehiculo.getAnio(),
            vehiculo.getPlaca(),
            vehiculo.getTarifaDiaria(),
            vehiculo.getEstado(),
            OficinaResponse.from(vehiculo.getOficina())
        );
    }
}
