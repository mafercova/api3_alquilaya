package com.api.alquilaya.dto;

import com.api.alquilaya.models.MantenimientoModel;
import java.time.LocalDate;

public record MantenimientoResponse(
    Long id,
    Long vehiculoId,
    LocalDate fechaMantenimiento,
    String descripcion,
    Double costo
) {
    public static MantenimientoResponse from(MantenimientoModel mantenimiento) {
        return new MantenimientoResponse(
            mantenimiento.getId(),
            mantenimiento.getVehiculo().getId(),
            mantenimiento.getFechaMantenimiento(),
            mantenimiento.getDescripcion(),
            mantenimiento.getCosto()
        );
    }
}
