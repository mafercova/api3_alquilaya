package com.api.alquilaya.controllers;

import com.api.alquilaya.dto.OficinaRequest;
import com.api.alquilaya.dto.OficinaResponse;
import com.api.alquilaya.services.OficinaAlquilerService;
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
@RequestMapping("/api/oficinas")
public class OficinaAlquilerController {

    private final OficinaAlquilerService oficinaService;

    public OficinaAlquilerController(OficinaAlquilerService oficinaService) {
        this.oficinaService = oficinaService;
    }

    @GetMapping
    public List<OficinaResponse> listar() {
        return oficinaService.listar().stream().map(OficinaResponse::from).toList();
    }

    @GetMapping("/{id}")
    public OficinaResponse buscarPorId(@PathVariable @Positive Long id) {
        return OficinaResponse.from(oficinaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<OficinaResponse> crear(@Valid @RequestBody OficinaRequest oficina) {
        OficinaResponse creada = OficinaResponse.from(oficinaService.crear(oficina.toModel()));
        return ResponseEntity.created(URI.create("/api/oficinas/" + creada.id())).body(creada);
    }

    @PutMapping("/{id}")
    public OficinaResponse actualizar(@PathVariable @Positive Long id, @Valid @RequestBody OficinaRequest oficina) {
        return OficinaResponse.from(oficinaService.actualizar(id, oficina.toModel()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        oficinaService.eliminar(id);
    }
}