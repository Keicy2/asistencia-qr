package com.asistenciaqr.controller;

import com.asistenciaqr.dto.SedeResponse;
import com.asistenciaqr.model.Sede;
import com.asistenciaqr.repository.SedeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    private final SedeRepository sedeRepository;

    public SedeController(SedeRepository sedeRepository) {
        this.sedeRepository = sedeRepository;
    }

    @GetMapping
    public ResponseEntity<List<SedeResponse>> listar() {
        List<Sede> sedes = sedeRepository.findByActivaTrue();
        List<SedeResponse> response = sedes.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SedeResponse> obtener(@PathVariable Long id) {
        Optional<Sede> sedeOpt = sedeRepository.findById(id);
        if (sedeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(sedeOpt.get()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Sede request) {
        Optional<Sede> sedeOpt = sedeRepository.findById(id);
        if (sedeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Sede sede = sedeOpt.get();
        if (request.getNombre() != null) sede.setNombre(request.getNombre());
        if (request.getDireccion() != null) sede.setDireccion(request.getDireccion());
        if (request.getLatitud() != null) sede.setLatitud(request.getLatitud());
        if (request.getLongitud() != null) sede.setLongitud(request.getLongitud());
        if (request.getGeocercaMetros() != null) sede.setGeocercaMetros(request.getGeocercaMetros());
        if (request.getActiva() != null) sede.setActiva(request.getActiva());

        Sede saved = sedeRepository.save(sede);
        return ResponseEntity.ok(toResponse(saved));
    }

    private SedeResponse toResponse(Sede sede) {
        SedeResponse r = new SedeResponse();
        r.setId(sede.getId());
        r.setNombre(sede.getNombre());
        r.setDireccion(sede.getDireccion());
        r.setLatitud(sede.getLatitud());
        r.setLongitud(sede.getLongitud());
        r.setGeocercaMetros(sede.getGeocercaMetros());
        r.setActiva(sede.getActiva());
        return r;
    }
}
