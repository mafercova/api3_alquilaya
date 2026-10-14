package com.api.alquilaya.controllers;

import com.api.alquilaya.dto.MantenimientoRequest;
import com.api.alquilaya.dto.MantenimientoResponse;
import com.api.alquilaya.services.MantenimientoService;
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
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {

    private final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {
        this.mantenimientoService = mantenimientoService;
    }

    @GetMapping
    public List<MantenimientoResponse> listar() {
        return mantenimientoService.listar().stream().map(MantenimientoResponse::from).toList();
    }

    @GetMapping("/{id}")
    public MantenimientoResponse buscarPorId(@PathVariable @Positive Long id) {
        return MantenimientoResponse.from(mantenimientoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<MantenimientoResponse> crear(@Valid @RequestBody MantenimientoRequest mantenimiento) {
        MantenimientoResponse creado = MantenimientoResponse.from(mantenimientoService.crear(mantenimiento.toModel()));
        return ResponseEntity.created(URI.create("/api/mantenimientos/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public MantenimientoResponse actualizar(@PathVariable @Positive Long id, @Valid @RequestBody MantenimientoRequest mantenimiento) {
        return MantenimientoResponse.from(mantenimientoService.actualizar(id, mantenimiento.toModel()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        mantenimientoService.eliminar(id);
    }
}