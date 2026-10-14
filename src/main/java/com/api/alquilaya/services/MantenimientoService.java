package com.api.alquilaya.services;

import com.api.alquilaya.models.MantenimientoModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.repositories.IMantenimientoRepository;
import com.api.alquilaya.repositories.IVehiculoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class MantenimientoService {

    private final IMantenimientoRepository mantenimientoRepository;
    private final IVehiculoRepository vehiculoRepository;

    public MantenimientoService(IMantenimientoRepository mantenimientoRepository,
                                IVehiculoRepository vehiculoRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    @Transactional(readOnly = true)
    public List<MantenimientoModel> listar() {
        return mantenimientoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MantenimientoModel buscarPorId(Long id) {
        return mantenimientoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mantenimiento no encontrado"));
    }

    public MantenimientoModel crear(MantenimientoModel mantenimiento) {
        mantenimiento.setId(null);
        mantenimiento.setVehiculo(resolverVehiculo(mantenimiento.getVehiculo()));
        return mantenimientoRepository.save(mantenimiento);
    }

    public MantenimientoModel actualizar(Long id, MantenimientoModel datos) {
        MantenimientoModel mantenimiento = buscarPorId(id);
        mantenimiento.setVehiculo(resolverVehiculo(datos.getVehiculo()));
        mantenimiento.setFechaMantenimiento(datos.getFechaMantenimiento());
        mantenimiento.setDescripcion(datos.getDescripcion());
        mantenimiento.setCosto(datos.getCosto());
        return mantenimientoRepository.save(mantenimiento);
    }

    public void eliminar(Long id) {
        mantenimientoRepository.delete(buscarPorId(id));
    }

    private VehiculoModel resolverVehiculo(VehiculoModel vehiculo) {
        if (vehiculo == null || vehiculo.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El mantenimiento debe incluir el id de un vehiculo");
        }
        return vehiculoRepository.findById(vehiculo.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El vehiculo indicado no existe"));
    }
}