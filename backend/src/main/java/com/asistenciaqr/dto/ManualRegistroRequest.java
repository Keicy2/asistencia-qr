package com.asistenciaqr.dto;

import jakarta.validation.constraints.NotNull;

public class ManualRegistroRequest {

    @NotNull
    private Long sesionId;

    @NotNull
    private Long usuarioId;

    public Long getSesionId() { return sesionId; }
    public void setSesionId(Long sesionId) { this.sesionId = sesionId; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
}
