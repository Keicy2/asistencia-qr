package com.asistenciaqr.repository;

import com.asistenciaqr.model.AsistenciaRegistro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsistenciaRegistroRepository extends JpaRepository<AsistenciaRegistro, Long> {
    List<AsistenciaRegistro> findBySesionIdOrderByRegistradoEnDesc(Long sesionId);
    void deleteBySesionId(Long sesionId);
    Optional<AsistenciaRegistro> findTopBySesionCodigoAndUsuarioUsernameOrderByRegistradoEnDesc(String sesionCodigo, String username);
}
