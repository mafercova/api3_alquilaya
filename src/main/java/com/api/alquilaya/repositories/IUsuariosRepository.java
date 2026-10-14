package com.api.alquilaya.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.api.alquilaya.models.UsuariosModel;

@Repository 
public interface IUsuariosRepository extends JpaRepository<UsuariosModel, Long> {
    Optional<UsuariosModel> findByEmail(String email);

    boolean existsByEmail(String email);
}
