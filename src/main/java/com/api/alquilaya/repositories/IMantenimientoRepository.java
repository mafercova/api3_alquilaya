package com.api.alquilaya.repositories;

import com.api.alquilaya.models.MantenimientoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMantenimientoRepository extends JpaRepository<MantenimientoModel, Long> {
}