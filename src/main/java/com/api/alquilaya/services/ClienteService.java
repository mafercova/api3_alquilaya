package com.api.alquilaya.services;

import com.api.alquilaya.models.ClienteModel;
import com.api.alquilaya.repositories.IClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class ClienteService {

    private final IClienteRepository clienteRepository;

    public ClienteService(IClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional(readOnly = true)
    public List<ClienteModel> listar() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ClienteModel buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
    }

    public ClienteModel crear(ClienteModel cliente) {
        cliente.setId(null);
        return clienteRepository.save(cliente);
    }

    public ClienteModel actualizar(Long id, ClienteModel datos) {
        ClienteModel cliente = buscarPorId(id);
        cliente.setNombre(datos.getNombre());
        cliente.setApellido(datos.getApellido());
        cliente.setEmail(datos.getEmail());
        cliente.setLicencia(datos.getLicencia());
        cliente.setTelefono(datos.getTelefono());
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        clienteRepository.delete(buscarPorId(id));
    }
}