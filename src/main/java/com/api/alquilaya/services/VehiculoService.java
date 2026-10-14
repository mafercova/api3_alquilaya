package com.api.alquilaya.services;

import com.api.alquilaya.models.OficinaAlquilerModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.repositories.IOficinaAlquilerRepository;
import com.api.alquilaya.repositories.IVehiculoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class VehiculoService {

    private final IVehiculoRepository vehiculoRepository;
    private final IOficinaAlquilerRepository oficinaRepository;

    public VehiculoService(IVehiculoRepository vehiculoRepository, IOficinaAlquilerRepository oficinaRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.oficinaRepository = oficinaRepository;
    }

    @Transactional(readOnly = true)
    public List<VehiculoModel> listar() {
        return vehiculoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public VehiculoModel buscarPorId(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vehiculo no encontrado"));
    }

    public VehiculoModel crear(VehiculoModel vehiculo) {
        vehiculo.setId(null);
        vehiculo.setOficina(resolverOficina(vehiculo.getOficina()));
        return vehiculoRepository.save(vehiculo);
    }

    public VehiculoModel actualizar(Long id, VehiculoModel datos) {
        VehiculoModel vehiculo = buscarPorId(id);
        vehiculo.setMarca(datos.getMarca());
        vehiculo.setModelo(datos.getModelo());
        vehiculo.setAnio(datos.getAnio());
        vehiculo.setPlaca(datos.getPlaca());
        vehiculo.setTarifaDiaria(datos.getTarifaDiaria());
        vehiculo.setEstado(datos.getEstado());
        vehiculo.setOficina(resolverOficina(datos.getOficina()));
        return vehiculoRepository.save(vehiculo);
    }

    public void eliminar(Long id) {
        vehiculoRepository.delete(buscarPorId(id));
    }

    private OficinaAlquilerModel resolverOficina(OficinaAlquilerModel oficina) {
        if (oficina == null || oficina.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El vehiculo debe incluir el id de una oficina");
        }
        return oficinaRepository.findById(oficina.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "La oficina indicada no existe"));
    }
}