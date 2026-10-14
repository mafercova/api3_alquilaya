package com.api.alquilaya.dto;

import com.api.alquilaya.models.EstadoVehiculoModel;
import com.api.alquilaya.models.OficinaAlquilerModel;
import com.api.alquilaya.models.VehiculoModel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record VehiculoRequest(
    @NotBlank @Size(max = 80) String marca,
    @NotBlank @Size(max = 100) String modelo,
    @NotNull @Min(1886) @Max(2100) Integer anio,
    @NotBlank @Size(max = 20) String placa,
    @NotNull @Positive Double tarifaDiaria,
    @NotNull EstadoVehiculoModel estado,
    @NotNull @Positive Long oficinaId
) {
    public VehiculoModel toModel() {
        VehiculoModel vehiculo = new VehiculoModel();
        vehiculo.setMarca(marca.trim());
        vehiculo.setModelo(modelo.trim());
        vehiculo.setAnio(anio);
        vehiculo.setPlaca(placa.trim().toUpperCase(java.util.Locale.ROOT));
        vehiculo.setTarifaDiaria(tarifaDiaria);
        vehiculo.setEstado(estado);
        OficinaAlquilerModel oficina = new OficinaAlquilerModel();
        oficina.setId(oficinaId);
        vehiculo.setOficina(oficina);
        return vehiculo;
    }
}
