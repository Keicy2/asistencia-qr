package com.asistenciaqr.dto;

public class SedeResponse {

    private Long id;
    private String nombre;
    private String direccion;
    private Double latitud;
    private Double longitud;
    private Double geocercaMetros;
    private Boolean activa;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
    public Double getGeocercaMetros() { return geocercaMetros; }
    public void setGeocercaMetros(Double geocercaMetros) { this.geocercaMetros = geocercaMetros; }
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
}
