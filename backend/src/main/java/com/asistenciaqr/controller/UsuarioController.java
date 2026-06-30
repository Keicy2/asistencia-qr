package com.asistenciaqr.controller;

import com.asistenciaqr.dto.UsuarioRequest;
import com.asistenciaqr.dto.UsuarioResponse;
import com.asistenciaqr.model.Usuario;
import com.asistenciaqr.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> response = usuarioRepository.findAll().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtener(@PathVariable Long id) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(usuarioOpt.get()));
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody UsuarioRequest request) {
        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            return ResponseEntity.badRequest().body("El correo ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setUsername(generarUsername(request));
        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setCargo(request.getCargo());
        usuario.setInstitucion(request.getInstitucion());
        if (request.getHoraEntrada() != null && !request.getHoraEntrada().isBlank()) {
            usuario.setHoraEntrada(LocalTime.parse(request.getHoraEntrada(), DateTimeFormatter.ofPattern("HH:mm[:ss]")));
        }
        if (request.getHoraSalida() != null && !request.getHoraSalida().isBlank()) {
            usuario.setHoraSalida(LocalTime.parse(request.getHoraSalida(), DateTimeFormatter.ofPattern("HH:mm[:ss]")));
        }
        if (request.getEstado() != null && !request.getEstado().isBlank()) {
            usuario.setEstado(request.getEstado());
        }

        Usuario saved = usuarioRepository.save(usuario);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody UsuarioRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioOpt.get();
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            usuario.setNombre(request.getNombre());
        }
        if (request.getCorreo() != null && !request.getCorreo().isBlank()) {
            Optional<Usuario> existente = usuarioRepository.findByCorreo(request.getCorreo());
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                return ResponseEntity.badRequest().body("El correo ya está en uso");
            }
            usuario.setCorreo(request.getCorreo());
            usuario.setUsername(generarUsername(request));
        }
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getCargo() != null) {
            usuario.setCargo(request.getCargo().isBlank() ? null : request.getCargo());
        }
        if (request.getInstitucion() != null) {
            usuario.setInstitucion(request.getInstitucion().isBlank() ? null : request.getInstitucion());
        }
        if (request.getHoraEntrada() != null) {
            usuario.setHoraEntrada(request.getHoraEntrada().isBlank() ? null
                    : LocalTime.parse(request.getHoraEntrada(), DateTimeFormatter.ofPattern("HH:mm[:ss]")));
        }
        if (request.getHoraSalida() != null) {
            usuario.setHoraSalida(request.getHoraSalida().isBlank() ? null
                    : LocalTime.parse(request.getHoraSalida(), DateTimeFormatter.ofPattern("HH:mm[:ss]")));
        }
        if (request.getEstado() != null) {
            usuario.setEstado(request.getEstado().isBlank() ? "activo" : request.getEstado());
        }

        Usuario saved = usuarioRepository.save(usuario);
        return ResponseEntity.ok(toResponse(saved));
    }

    private String generarUsername(UsuarioRequest request) {
        String base = request.getCorreo().substring(0, request.getCorreo().indexOf('@'));
        return base.toLowerCase();
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        UsuarioResponse r = new UsuarioResponse();
        r.setId(usuario.getId());
        r.setNombre(usuario.getNombre());
        r.setCorreo(usuario.getCorreo());
        r.setUsername(usuario.getUsername());
        r.setCargo(usuario.getCargo());
        r.setInstitucion(usuario.getInstitucion());
        r.setHoraEntrada(usuario.getHoraEntrada());
        r.setHoraSalida(usuario.getHoraSalida());
        r.setEstado(usuario.getEstado());
        r.setActivo(usuario.getActivo());
        r.setCreadoEn(usuario.getCreadoEn());
        return r;
    }
}
