package com.asistenciaqr.repository;

import com.asistenciaqr.model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeRepository extends JpaRepository<Sede, Long> {
    List<Sede> findByActivaTrue();
}
