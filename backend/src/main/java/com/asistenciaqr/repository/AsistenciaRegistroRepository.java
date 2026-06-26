package com.asistenciaqr.repository;

import com.asistenciaqr.model.AsistenciaRegistro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsistenciaRegistroRepository extends JpaRepository<AsistenciaRegistro, Long> {
    List<AsistenciaRegistro> findBySesionIdOrderByRegistradoEnDesc(Long sesionId);
    void deleteBySesionId(Long sesionId);
}
