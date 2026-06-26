export interface Usuario {
  id: number;
  nombre: string;
  correo: string;
  horaEntrada: string | null;
  horaSalida: string | null;
  estado: string;
  activo: boolean;
  creadoEn: string;
}

export interface Sede {
  id: number;
  nombre: string;
  direccion: string;
  latitud: number;
  longitud: number;
  geocercaMetros: number;
  activa: boolean;
}

export interface QrSesion {
  id: number;
  sedeId: number;
  sedeNombre: string;
  codigo: string;
  fecha: string;
  expiraEn: string;
  activa: boolean;
  creadoEn: string;
  totalRegistros: number;
}

export interface AsistenciaRegistro {
  id: number;
  usuarioId: number;
  usuarioNombre: string;
  usuarioCorreo: string;
  institucion: string;
  cargo: string;
  horaProgramada: string;
  estado: string;
  metodo: string;
  registradoEn: string;
}

export interface LoginResponse {
  status: string;
  message: string;
  token: string;
  nombre: string;
  correo: string;
  roles: string;
}

export interface UserInfo {
  nombre: string;
  correo: string;
  roles: string;
}

export interface RegistroPayload {
  codigo: string;
  correo: string;
  password: string;
  latitud: number | null;
  longitud: number | null;
}

export interface ManualRegistroPayload {
  sesionId: number;
  usuarioId: number;
}

export interface UsuarioPayload {
  nombre: string;
  correo: string;
  password: string;
  horaEntrada?: string;
  horaSalida?: string;
  estado?: string;
}
