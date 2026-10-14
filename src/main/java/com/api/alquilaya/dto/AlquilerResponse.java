package com.api.alquilaya.dto;

import com.api.alquilaya.models.AlquilerModel;
import com.api.alquilaya.models.EstadoAlquilerModel;
import java.time.LocalDate;

public record AlquilerResponse(
    Long id,
    Long vehiculoId,
    Long clienteId,
    LocalDate fechaInicio,
    LocalDate fechaFin,
    Double precioTotal,
    EstadoAlquilerModel estado
) {
    public static AlquilerResponse from(AlquilerModel alquiler) {
        return new AlquilerResponse(
            alquiler.getId(),
            alquiler.getVehiculo().getId(),
            alquiler.getCliente().getId(),
            alquiler.getFechaInicio(),
            alquiler.getFechaFin(),
            alquiler.getPrecioTotal(),
            alquiler.getEstado()
        );
    }
}
