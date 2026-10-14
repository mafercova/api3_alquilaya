package com.api.alquilaya.repositories;

import com.api.alquilaya.models.OficinaAlquilerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOficinaAlquilerRepository extends JpaRepository<OficinaAlquilerModel, Long> {
}