import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ApiService } from './api.service';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Sede, QrSesion, AsistenciaRegistro, RegistroPayload, ManualRegistroPayload, Usuario, UsuarioPayload } from '../models/asistencia.models';

@Injectable({ providedIn: 'root' })
export class AsistenciaService {

  constructor(private api: ApiService, private http: HttpClient) {}

  getSedes(): Observable<Sede[]> {
    return this.api.get<Sede[]>('/api/sedes');
  }

  crearSesion(data: { sedeId: number; fecha: string }): Observable<QrSesion> {
    return this.api.post<QrSesion>('/api/sesiones', data);
  }

  listarSesiones(): Observable<QrSesion[]> {
    return this.api.get<QrSesion[]>('/api/sesiones');
  }

  eliminarSesion(id: number): Observable<void> {
    return this.api.delete<void>(`/api/sesiones/${id}`);
  }

  getSesionPublica(codigo: string): Observable<QrSesion> {
    return this.api.get<QrSesion>(`/api/sesiones/publica/${codigo}`);
  }

  registrar(data: RegistroPayload): Observable<AsistenciaRegistro> {
    return this.api.post<AsistenciaRegistro>('/api/asistencia/registrar', data);
  }

  listarRegistros(sesionId?: number): Observable<AsistenciaRegistro[]> {
    const params = sesionId ? `?sesionId=${sesionId}` : '';
    return this.api.get<AsistenciaRegistro[]>(`/api/asistencia${params}`);
  }

  manualRegistro(data: ManualRegistroPayload): Observable<AsistenciaRegistro> {
    return this.api.post<AsistenciaRegistro>('/api/asistencia/manual', data);
  }

  exportarCsv(sesionId?: number): Observable<Blob> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    const params: any = {};
    if (sesionId) params.sesionId = sesionId;
    return this.http.get(`${environment.apiUrl}/api/asistencia/exportar`, {
      headers,
      params,
      responseType: 'blob'
    });
  }

  getServerInfo(): Observable<{ localIp: string; serverUrl: string }> {
    return this.api.get<{ localIp: string; serverUrl: string }>('/api/server/info');
  }

  getUsuarios(): Observable<Usuario[]> {
    return this.api.get<Usuario[]>('/api/usuarios');
  }

  getUsuario(id: number): Observable<Usuario> {
    return this.api.get<Usuario>(`/api/usuarios/${id}`);
  }

  crearUsuario(data: UsuarioPayload): Observable<Usuario> {
    return this.api.post<Usuario>('/api/usuarios', data);
  }

  actualizarUsuario(id: number, data: Partial<UsuarioPayload>): Observable<Usuario> {
    return this.api.put<Usuario>(`/api/usuarios/${id}`, data);
  }

  haversineDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
    const R = 6371000;
    const dLat = this.toRad(lat2 - lat1);
    const dLon = this.toRad(lon2 - lon1);
    const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
              Math.cos(this.toRad(lat1)) * Math.cos(this.toRad(lat2)) *
              Math.sin(dLon / 2) * Math.sin(dLon / 2);
    const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return R * c;
  }

  private toRad(deg: number): number {
    return deg * (Math.PI / 180);
  }
}
