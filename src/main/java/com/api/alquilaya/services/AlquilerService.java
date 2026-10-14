package com.api.alquilaya.services;

import com.api.alquilaya.models.AlquilerModel;
import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.models.VehiculoModel;
import com.api.alquilaya.models.EstadoAlquilerModel;
import com.api.alquilaya.models.EstadoVehiculoModel;
import com.api.alquilaya.repositories.IAlquilerRepository;
import com.api.alquilaya.repositories.IClienteRepository;
import com.api.alquilaya.repositories.IVehiculoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(isolation = Isolation.READ_COMMITTED)
public class AlquilerService {

    private final IAlquilerRepository alquilerRepository;
    private final IVehiculoRepository vehiculoRepository;
    private final IClienteRepository clienteRepository;

    public AlquilerService(IAlquilerRepository alquilerRepository,
                           IVehiculoRepository vehiculoRepository,
                           IClienteRepository clienteRepository) {
        this.alquilerRepository = alquilerRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<AlquilerModel> listar() {
        return alquilerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public AlquilerModel buscarPorId(Long id) {
        return alquilerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alquiler no encontrado"));
    }

    public AlquilerModel crear(AlquilerModel alquiler) {
        alquiler.setId(null);
        alquiler.setVehiculo(resolverVehiculo(alquiler.getVehiculo()));
        alquiler.setCliente(resolverCliente(alquiler.getCliente()));
        validarAlquiler(alquiler);
        alquiler.setPrecioTotal(calcularPrecio(alquiler));
        AlquilerModel creado = alquilerRepository.saveAndFlush(alquiler);
        sincronizarEstado(creado.getVehiculo());
        return creado;
    }

    public AlquilerModel actualizar(Long id, AlquilerModel datos) {
        AlquilerModel alquiler = buscarParaActualizar(id);
        VehiculoModel anterior = alquiler.getVehiculo();
        Long nuevoId = datos.getVehiculo() == null ? null : datos.getVehiculo().getId();
        if (nuevoId != null && nuevoId < anterior.getId()) {
            resolverVehiculo(datos.getVehiculo());
        }
        anterior = resolverVehiculo(anterior);
        datos.setId(id);
        datos.setVehiculo(resolverVehiculo(datos.getVehiculo()));
        datos.setCliente(resolverCliente(datos.getCliente()));
        validarAlquiler(datos);
        alquiler.setVehiculo(datos.getVehiculo());
        alquiler.setCliente(datos.getCliente());
        alquiler.setFechaInicio(datos.getFechaInicio());
        alquiler.setFechaFin(datos.getFechaFin());
        alquiler.setEstado(datos.getEstado());
        alquiler.setPrecioTotal(calcularPrecio(alquiler));
        alquilerRepository.saveAndFlush(alquiler);
        sincronizarEstado(anterior);
        if (!anterior.getId().equals(alquiler.getVehiculo().getId())) {
            sincronizarEstado(alquiler.getVehiculo());
        }
        return alquiler;
    }

    public void eliminar(Long id) {
        AlquilerModel alquiler = buscarParaActualizar(id);
        VehiculoModel vehiculo = resolverVehiculo(alquiler.getVehiculo());
        alquilerRepository.delete(alquiler);
        alquilerRepository.flush();
        sincronizarEstado(vehiculo);
    }

    private VehiculoModel resolverVehiculo(VehiculoModel vehiculo) {
        if (vehiculo == null || vehiculo.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alquiler debe incluir el id de un vehiculo");
        }
        return vehiculoRepository.buscarParaActualizar(vehiculo.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El vehiculo indicado no existe"));
    }

    private AlquilerModel buscarParaActualizar(Long id) {
        return alquilerRepository.buscarParaActualizar(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alquiler no encontrado"));
    }

    private ClienteModel resolverCliente(ClienteModel cliente) {
        if (cliente == null || cliente.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alquiler debe incluir el id de un cliente");
        }
        return clienteRepository.findById(cliente.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "El cliente indicado no existe"));
    }

    private Double calcularPrecio(AlquilerModel alquiler) {
        long dias = ChronoUnit.DAYS.between(alquiler.getFechaInicio(), alquiler.getFechaFin());
        return dias * alquiler.getVehiculo().getTarifaDiaria();
    }

    private void validarAlquiler(AlquilerModel alquiler) {
        if (alquiler.getFechaInicio() == null || alquiler.getFechaFin() == null
                || !alquiler.getFechaFin().isAfter(alquiler.getFechaInicio()) || alquiler.getEstado() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fechas o estado del alquiler inválidos");
        }
        if (alquiler.getEstado() == EstadoAlquilerModel.COMPLETADO) {
            return;
        }
        VehiculoModel vehiculo = alquiler.getVehiculo();
        if (vehiculo.getEstado() == EstadoVehiculoModel.MANTENIMIENTO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El vehículo está en mantenimiento");
        }
        Long excluirId = alquiler.getId() == null ? -1L : alquiler.getId();
        if (alquilerRepository.contarSolapamientos(vehiculo.getId(), excluirId,
                List.of(EstadoAlquilerModel.PENDIENTE, EstadoAlquilerModel.ACTIVO),
                alquiler.getFechaInicio(), alquiler.getFechaFin()) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El vehículo tiene un alquiler en ese periodo");
        }
        if (alquiler.getEstado() == EstadoAlquilerModel.ACTIVO
                && alquilerRepository.existsByVehiculoIdAndEstadoAndIdNot(
                    vehiculo.getId(), EstadoAlquilerModel.ACTIVO, excluirId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El vehículo ya tiene un alquiler activo");
        }
        if (alquiler.getEstado() == EstadoAlquilerModel.ACTIVO
                && vehiculo.getEstado() == EstadoVehiculoModel.ALQUILADO
                && !alquilerRepository.existsByVehiculoIdAndEstado(vehiculo.getId(), EstadoAlquilerModel.ACTIVO)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El vehículo no está disponible para activar el alquiler");
        }
    }

    private void sincronizarEstado(VehiculoModel vehiculo) {
        if (vehiculo.getEstado() != EstadoVehiculoModel.MANTENIMIENTO) {
            vehiculo.setEstado(alquilerRepository.existsByVehiculoIdAndEstado(
                vehiculo.getId(), EstadoAlquilerModel.ACTIVO)
                ? EstadoVehiculoModel.ALQUILADO : EstadoVehiculoModel.DISPONIBLE);
        }
    }
}
