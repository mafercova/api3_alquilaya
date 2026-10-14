package com.api.alquilaya.repositories;

import com.api.alquilaya.models.AlquilerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.api.alquilaya.models.EstadoAlquilerModel;
import java.time.LocalDate;
import java.util.Collection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.Lock;

@Repository
public interface IAlquilerRepository extends JpaRepository<AlquilerModel, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AlquilerModel a where a.id = :id")
    Optional<AlquilerModel> buscarParaActualizar(@Param("id") Long id);

    @Query("""
        select count(a) from AlquilerModel a
        where a.vehiculo.id = :vehiculoId and a.id <> :excluirId
        and a.estado in :estados and a.fechaInicio < :fin and a.fechaFin > :inicio
        """)
    long contarSolapamientos(@Param("vehiculoId") Long vehiculoId,
                            @Param("excluirId") Long excluirId,
                            @Param("estados") Collection<EstadoAlquilerModel> estados,
                            @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    boolean existsByVehiculoIdAndEstadoAndIdNot(Long vehiculoId, EstadoAlquilerModel estado, Long id);
    boolean existsByVehiculoIdAndEstado(Long vehiculoId, EstadoAlquilerModel estado);
}
