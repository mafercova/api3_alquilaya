package com.api.alquilaya.controllers;

import com.api.alquilaya.dto.VehiculoRequest;
import com.api.alquilaya.dto.VehiculoResponse;
import com.api.alquilaya.services.VehiculoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping
    public List<VehiculoResponse> listar() {
        return vehiculoService.listar().stream().map(VehiculoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public VehiculoResponse buscarPorId(@PathVariable @Positive Long id) {
        return VehiculoResponse.from(vehiculoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<VehiculoResponse> crear(@Valid @RequestBody VehiculoRequest vehiculo) {
        VehiculoResponse creado = VehiculoResponse.from(vehiculoService.crear(vehiculo.toModel()));
        return ResponseEntity.created(URI.create("/api/vehiculos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public VehiculoResponse actualizar(@PathVariable @Positive Long id, @Valid @RequestBody VehiculoRequest vehiculo) {
        return VehiculoResponse.from(vehiculoService.actualizar(id, vehiculo.toModel()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        vehiculoService.eliminar(id);
    }
}