package com.api.alquilaya.repositories;

import com.api.alquilaya.models.VehiculoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface IVehiculoRepository extends JpaRepository<VehiculoModel, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select v from VehiculoModel v where v.id = :id")
    Optional<VehiculoModel> buscarParaActualizar(@Param("id") Long id);
}
