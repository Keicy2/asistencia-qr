package com.asistenciaqr.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class RegistroResponse {

    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private String usuarioCorreo;
    private String institucion;
    private String cargo;
    private String metodo;
    private LocalTime horaProgramada;
    private String estado;
    private LocalDateTime registradoEn;
    private LocalDateTime salidaEn;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    public String getUsuarioCorreo() { return usuarioCorreo; }
    public void setUsuarioCorreo(String usuarioCorreo) { this.usuarioCorreo = usuarioCorreo; }
    public String getInstitucion() { return institucion; }
    public void setInstitucion(String institucion) { this.institucion = institucion; }
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
    public LocalTime getHoraProgramada() { return horaProgramada; }
    public void setHoraProgramada(LocalTime horaProgramada) { this.horaProgramada = horaProgramada; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getRegistradoEn() { return registradoEn; }
    public void setRegistradoEn(LocalDateTime registradoEn) { this.registradoEn = registradoEn; }
    public LocalDateTime getSalidaEn() { return salidaEn; }
    public void setSalidaEn(LocalDateTime salidaEn) { this.salidaEn = salidaEn; }
}
