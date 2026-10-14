package com.api.alquilaya.services;

import com.api.alquilaya.models.OficinaAlquilerModel;
import com.api.alquilaya.repositories.IOficinaAlquilerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class OficinaAlquilerService {

    private final IOficinaAlquilerRepository oficinaRepository;

    public OficinaAlquilerService(IOficinaAlquilerRepository oficinaRepository) {
        this.oficinaRepository = oficinaRepository;
    }

    @Transactional(readOnly = true)
    public List<OficinaAlquilerModel> listar() {
        return oficinaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public OficinaAlquilerModel buscarPorId(Long id) {
        return oficinaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Oficina no encontrada"));
    }

    public OficinaAlquilerModel crear(OficinaAlquilerModel oficina) {
        oficina.setId(null);
        return oficinaRepository.save(oficina);
    }

    public OficinaAlquilerModel actualizar(Long id, OficinaAlquilerModel datos) {
        OficinaAlquilerModel oficina = buscarPorId(id);
        oficina.setNombreOficina(datos.getNombreOficina());
        oficina.setDireccion(datos.getDireccion());
        oficina.setCiudad(datos.getCiudad());
        oficina.setRegion(datos.getRegion());
        return oficinaRepository.save(oficina);
    }

    public void eliminar(Long id) {
        oficinaRepository.delete(buscarPorId(id));
    }
}