package com.asistenciaqr.controller;

import com.asistenciaqr.dto.LoginRequest;
import com.asistenciaqr.dto.LoginResponse;
import com.asistenciaqr.dto.UserInfoResponse;
import com.asistenciaqr.model.Usuario;
import com.asistenciaqr.repository.UsuarioRepository;
import com.asistenciaqr.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(request.getCorreo());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (Boolean.TRUE.equals(usuario.getBloqueado())) {
                LoginResponse error = new LoginResponse("error", "Su cuenta ha sido bloqueada. Contacte al administrador.");
                return ResponseEntity.status(401).body(error);
            }
            if (Boolean.TRUE.equals(usuario.getActivo()) && passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
                String token = jwtUtil.generateToken(usuario.getCorreo(), "ADMIN");
                usuario.setUltimoAcceso(LocalDateTime.now());
                usuarioRepository.save(usuario);

                LoginResponse response = new LoginResponse();
                response.setStatus("success");
                response.setToken(token);
                response.setCorreo(usuario.getCorreo());
                response.setNombre(usuario.getNombre());
                response.setRoles("ADMIN");
                return ResponseEntity.ok(response);
            }
        }

        LoginResponse errorResponse = new LoginResponse("error", "Correo o contraseña incorrectos");
        return ResponseEntity.status(401).body(errorResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> me(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(authentication.getName());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Usuario usuario = usuarioOpt.get();
        if (Boolean.TRUE.equals(usuario.getBloqueado())) {
            return ResponseEntity.status(403).build();
        }

        UserInfoResponse response = new UserInfoResponse(usuario.getNombre(), usuario.getCorreo(), "ADMIN");
        return ResponseEntity.ok(response);
    }
}
