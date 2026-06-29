package com.asistenciaqr.controller;

import com.asistenciaqr.dto.ManualRegistroRequest;
import com.asistenciaqr.dto.RegistroRequest;
import com.asistenciaqr.dto.RegistroResponse;
import com.asistenciaqr.model.AsistenciaRegistro;
import com.asistenciaqr.model.QrSesion;
import com.asistenciaqr.model.Usuario;
import com.asistenciaqr.repository.AsistenciaRegistroRepository;
import com.asistenciaqr.repository.QrSesionRepository;
import com.asistenciaqr.repository.UsuarioRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/asistencia")
public class AsistenciaController {

    private final AsistenciaRegistroRepository registroRepository;
    private final QrSesionRepository qrSesionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AsistenciaController(AsistenciaRegistroRepository registroRepository,
                                QrSesionRepository qrSesionRepository,
                                UsuarioRepository usuarioRepository,
                                PasswordEncoder passwordEncoder) {
        this.registroRepository = registroRepository;
        this.qrSesionRepository = qrSesionRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody RegistroRequest request) {
        Optional<QrSesion> sesionOpt = qrSesionRepository.findByCodigo(request.getCodigo());
        if (sesionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Sesión no encontrada");
        }

        QrSesion sesion = sesionOpt.get();
        if (!sesion.getActiva() || (sesion.getExpiraEn() != null && sesion.getExpiraEn().isBefore(LocalDateTime.now()))) {
            return ResponseEntity.status(410).body("Esta sesión ha expirado");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findByCorreo(request.getCorreo());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }

        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }

        if (Boolean.TRUE.equals(usuario.getBloqueado()) || Boolean.FALSE.equals(usuario.getActivo())) {
            return ResponseEntity.status(403).body("Cuenta bloqueada o inactiva");
        }

        LocalTime horaRegistro = LocalTime.now();
        String estado = calcularEstado(usuario, horaRegistro);

        AsistenciaRegistro registro = new AsistenciaRegistro();
        registro.setSesion(sesion);
        registro.setUsuario(usuario);
        registro.setNombre(usuario.getNombre());
        registro.setCorreo(usuario.getCorreo());
        registro.setInstitucion(usuario.getInstitucion());
        registro.setCargo(usuario.getCargo());
        registro.setLatitud(request.getLatitud());
        registro.setLongitud(request.getLongitud());
        registro.setHoraProgramada(usuario.getHoraEntrada());
        registro.setEstado(estado);
        registro.setMetodo("qr");

        AsistenciaRegistro saved = registroRepository.save(registro);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PostMapping("/manual")
    public ResponseEntity<RegistroResponse> manual(@Valid @RequestBody ManualRegistroRequest request) {
        Optional<QrSesion> sesionOpt = qrSesionRepository.findById(request.getSesionId());
        if (sesionOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(request.getUsuarioId());
        if (usuarioOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Usuario usuario = usuarioOpt.get();
        LocalTime horaRegistro = LocalTime.now();
        String estado = calcularEstado(usuario, horaRegistro);

        AsistenciaRegistro registro = new AsistenciaRegistro();
        registro.setSesion(sesionOpt.get());
        registro.setUsuario(usuario);
        registro.setNombre(usuario.getNombre());
        registro.setCorreo(usuario.getCorreo());
        registro.setInstitucion(usuario.getInstitucion());
        registro.setCargo(usuario.getCargo());
        registro.setHoraProgramada(usuario.getHoraEntrada());
        registro.setEstado(estado);
        registro.setMetodo("manual");

        AsistenciaRegistro saved = registroRepository.save(registro);
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<RegistroResponse>> listar(@RequestParam(required = false) Long sesionId) {
        List<AsistenciaRegistro> registros;
        if (sesionId != null) {
            registros = registroRepository.findBySesionIdOrderByRegistradoEnDesc(sesionId);
        } else {
            registros = registroRepository.findAll();
        }

        List<RegistroResponse> response = registros.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/exportar")
    public ResponseEntity<byte[]> exportar(@RequestParam(required = false) Long sesionId) {
        List<AsistenciaRegistro> registros;
        if (sesionId != null) {
            registros = registroRepository.findBySesionIdOrderByRegistradoEnDesc(sesionId);
        } else {
            registros = registroRepository.findAll();
        }

        StringBuilder csv = new StringBuilder();
        csv.append('\uFEFF');
        csv.append("sep=,\n");
        csv.append("ID,Nombre,Correo,Institución,Cargo,Hora Programada,Estado,Método,Fecha\n");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (AsistenciaRegistro r : registros) {
            csv.append(r.getId()).append(",");
            csv.append(escapeCsv(r.getNombre())).append(",");
            csv.append(escapeCsv(r.getCorreo())).append(",");
            csv.append(escapeCsv(r.getInstitucion())).append(",");
            csv.append(escapeCsv(r.getCargo())).append(",");
            csv.append(r.getHoraProgramada() != null ? r.getHoraProgramada().toString() : "").append(",");
            csv.append(r.getEstado() != null ? r.getEstado() : "").append(",");
            csv.append(r.getMetodo()).append(",");
            csv.append(r.getRegistradoEn().format(fmt)).append("\n");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
        headers.setContentDispositionFormData("attachment", "asistencia.csv");

        return ResponseEntity.ok().headers(headers).body(csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    private String calcularEstado(Usuario usuario, LocalTime horaActual) {
        if (usuario.getEstado() != null && !"activo".equals(usuario.getEstado())) {
            return usuario.getEstado();
        }
        if (usuario.getHoraEntrada() == null) {
            return "libre";
        }
        if (horaActual.isBefore(usuario.getHoraEntrada()) || horaActual.equals(usuario.getHoraEntrada())) {
            return "a_tiempo";
        }
        return "tarde";
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private RegistroResponse toResponse(AsistenciaRegistro registro) {
        RegistroResponse r = new RegistroResponse();
        r.setId(registro.getId());
        if (registro.getUsuario() != null) {
            r.setUsuarioId(registro.getUsuario().getId());
        }
        r.setUsuarioNombre(registro.getNombre());
        r.setUsuarioCorreo(registro.getCorreo());
        r.setInstitucion(registro.getInstitucion());
        r.setCargo(registro.getCargo());
        r.setHoraProgramada(registro.getHoraProgramada());
        r.setEstado(registro.getEstado());
        r.setMetodo(registro.getMetodo());
        r.setRegistradoEn(registro.getRegistradoEn());
        return r;
    }
}
