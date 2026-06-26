package com.asistenciaqr.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "asistencia_registros")
public class AsistenciaRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private QrSesion sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 200)
    private String nombre;

    @Column(length = 200)
    private String correo;

    @Column(length = 50)
    private String telefono;

    @Column(length = 300)
    private String institucion;

    @Column(length = 200)
    private String cargo;

    @Column(name = "hora_programada")
    private LocalTime horaProgramada;

    @Column(length = 50)
    private String estado;

    private Double latitud;

    private Double longitud;

    @Column(nullable = false, length = 20)
    private String metodo = "qr";

    @Column(name = "registrado_en", nullable = false, updatable = false)
    private LocalDateTime registradoEn = LocalDateTime.now();

    public AsistenciaRegistro() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public QrSesion getSesion() { return sesion; }
    public void setSesion(QrSesion sesion) { this.sesion = sesion; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getInstitucion() { return institucion; }
    public void setInstitucion(String institucion) { this.institucion = institucion; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public LocalTime getHoraProgramada() { return horaProgramada; }
    public void setHoraProgramada(LocalTime horaProgramada) { this.horaProgramada = horaProgramada; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public LocalDateTime getRegistradoEn() { return registradoEn; }
    public void setRegistradoEn(LocalDateTime registradoEn) { this.registradoEn = registradoEn; }
}
