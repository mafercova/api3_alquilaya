package com.api.alquilaya.dto;

import com.api.alquilaya.models.AlquilerModel;
import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.models.EstadoAlquilerModel;
import com.api.alquilaya.models.VehiculoModel;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record AlquilerRequest(
    @NotNull @Positive Long vehiculoId,
    @NotNull @Positive Long clienteId,
    @NotNull LocalDate fechaInicio,
    @NotNull LocalDate fechaFin,
    @NotNull EstadoAlquilerModel estado
) {
    @AssertTrue(message = "La fecha de fin debe ser posterior a la fecha de inicio")
    public boolean isRangoFechasValido() {
        return fechaInicio == null || fechaFin == null || fechaFin.isAfter(fechaInicio);
    }

    public AlquilerModel toModel() {
        AlquilerModel alquiler = new AlquilerModel();
        VehiculoModel vehiculo = new VehiculoModel();
        vehiculo.setId(vehiculoId);
        ClienteModel cliente = new ClienteModel();
        cliente.setId(clienteId);
        alquiler.setVehiculo(vehiculo);
        alquiler.setCliente(cliente);
        alquiler.setFechaInicio(fechaInicio);
        alquiler.setFechaFin(fechaFin);
        alquiler.setEstado(estado);
        return alquiler;
    }
}
