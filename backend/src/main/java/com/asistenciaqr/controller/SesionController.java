package com.asistenciaqr.controller;

import com.asistenciaqr.dto.QrSesionRequest;
import com.asistenciaqr.dto.QrSesionResponse;
import com.asistenciaqr.model.QrSesion;
import com.asistenciaqr.model.Sede;
import com.asistenciaqr.model.Usuario;
import com.asistenciaqr.repository.AsistenciaRegistroRepository;
import com.asistenciaqr.repository.QrSesionRepository;
import com.asistenciaqr.repository.SedeRepository;
import com.asistenciaqr.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/sesiones")
public class SesionController {

    private final QrSesionRepository qrSesionRepository;
    private final SedeRepository sedeRepository;
    private final UsuarioRepository usuarioRepository;
    private final AsistenciaRegistroRepository registroRepository;

    public SesionController(QrSesionRepository qrSesionRepository,
                            SedeRepository sedeRepository,
                            UsuarioRepository usuarioRepository,
                            AsistenciaRegistroRepository registroRepository) {
        this.qrSesionRepository = qrSesionRepository;
        this.sedeRepository = sedeRepository;
        this.usuarioRepository = usuarioRepository;
        this.registroRepository = registroRepository;
    }

    @PostMapping
    public ResponseEntity<QrSesionResponse> crear(@Valid @RequestBody QrSesionRequest request, Authentication auth) {
        Optional<Sede> sedeOpt = sedeRepository.findById(request.getSedeId());
        if (sedeOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(auth.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        LocalDate fecha = LocalDate.parse(request.getFecha(), DateTimeFormatter.ISO_DATE);
        LocalDateTime expiraEn = LocalDateTime.of(fecha, LocalTime.of(23, 59, 59));

        QrSesion sesion = new QrSesion();
        sesion.setSede(sedeOpt.get());
        sesion.setCodigo(UUID.randomUUID().toString().substring(0, 8));
        sesion.setFecha(fecha);
        sesion.setExpiraEn(expiraEn);
        sesion.setCreadoPor(usuarioOpt.get());

        QrSesion saved = qrSesionRepository.save(sesion);
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<QrSesionResponse>> listar() {
        List<QrSesion> sesiones = qrSesionRepository.findAll();
        List<QrSesionResponse> response = sesiones.stream()
                .map(this::toResponse)
                .sorted((a, b) -> b.getCreadoEn().compareTo(a.getCreadoEn()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/publica/{codigo}")
    public ResponseEntity<QrSesionResponse> publica(@PathVariable String codigo) {
        Optional<QrSesion> sesionOpt = qrSesionRepository.findByCodigo(codigo);
        if (sesionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        QrSesion sesion = sesionOpt.get();
        if (!sesion.getActiva() || (sesion.getExpiraEn() != null && sesion.getExpiraEn().isBefore(LocalDateTime.now()))) {
            return ResponseEntity.status(410).build();
        }

        return ResponseEntity.ok(toResponse(sesion));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Optional<QrSesion> sesionOpt = qrSesionRepository.findById(id);
        if (sesionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        registroRepository.deleteBySesionId(id);
        qrSesionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private QrSesionResponse toResponse(QrSesion sesion) {
        QrSesionResponse r = new QrSesionResponse();
        r.setId(sesion.getId());
        r.setSedeId(sesion.getSede().getId());
        r.setSedeNombre(sesion.getSede().getNombre());
        r.setCodigo(sesion.getCodigo());
        r.setFecha(sesion.getFecha());
        r.setExpiraEn(sesion.getExpiraEn());
        r.setActiva(sesion.getActiva() && (sesion.getExpiraEn() == null || sesion.getExpiraEn().isAfter(LocalDateTime.now())));
        r.setCreadoEn(sesion.getCreadoEn());
        r.setTotalRegistros(registroRepository.findBySesionIdOrderByRegistradoEnDesc(sesion.getId()).size());
        return r;
    }
}
