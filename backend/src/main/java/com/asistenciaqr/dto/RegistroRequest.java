package com.asistenciaqr.dto;

import jakarta.validation.constraints.NotBlank;

public class RegistroRequest {

    @NotBlank
    private String codigo;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Double latitud;
    private Double longitud;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
}
