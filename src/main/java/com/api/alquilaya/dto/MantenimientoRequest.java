package com.api.alquilaya.dto;

import com.api.alquilaya.models.MantenimientoModel;
import com.api.alquilaya.models.VehiculoModel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MantenimientoRequest(
    @NotNull @Positive Long vehiculoId,
    @NotNull LocalDate fechaMantenimiento,
    @NotBlank @Size(max = 1000) String descripcion,
    @NotNull @PositiveOrZero Double costo
) {
    public MantenimientoModel toModel() {
        MantenimientoModel mantenimiento = new MantenimientoModel();
        VehiculoModel vehiculo = new VehiculoModel();
        vehiculo.setId(vehiculoId);
        mantenimiento.setVehiculo(vehiculo);
        mantenimiento.setFechaMantenimiento(fechaMantenimiento);
        mantenimiento.setDescripcion(descripcion.trim());
        mantenimiento.setCosto(costo);
        return mantenimiento;
    }
}
