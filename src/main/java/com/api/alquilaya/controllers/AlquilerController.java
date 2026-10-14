package com.api.alquilaya.controllers;

import com.api.alquilaya.dto.AlquilerRequest;
import com.api.alquilaya.dto.AlquilerResponse;
import com.api.alquilaya.services.AlquilerService;
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
@RequestMapping("/api/alquileres")
public class AlquilerController {

    private final AlquilerService alquilerService;

    public AlquilerController(AlquilerService alquilerService) {
        this.alquilerService = alquilerService;
    }

    @GetMapping
    public List<AlquilerResponse> listar() {
        return alquilerService.listar().stream().map(AlquilerResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AlquilerResponse buscarPorId(@PathVariable @Positive Long id) {
        return AlquilerResponse.from(alquilerService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<AlquilerResponse> crear(@Valid @RequestBody AlquilerRequest alquiler) {
        AlquilerResponse creado = AlquilerResponse.from(alquilerService.crear(alquiler.toModel()));
        return ResponseEntity.created(URI.create("/api/alquileres/" + creado.id())).body(creado);
    }

    @PutMapping("/{id}")
    public AlquilerResponse actualizar(@PathVariable @Positive Long id, @Valid @RequestBody AlquilerRequest alquiler) {
        return AlquilerResponse.from(alquilerService.actualizar(id, alquiler.toModel()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable @Positive Long id) {
        alquilerService.eliminar(id);
    }
}