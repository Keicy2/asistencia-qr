package com.asistenciaqr.repository;

import com.asistenciaqr.model.QrSesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QrSesionRepository extends JpaRepository<QrSesion, Long> {
    Optional<QrSesion> findByCodigo(String codigo);
}
