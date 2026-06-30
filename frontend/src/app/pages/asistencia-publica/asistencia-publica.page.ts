import { Component, OnInit } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { NgIf, DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { addIcons } from 'ionicons';
import { closeCircleOutline, checkmarkCircleOutline, checkmarkCircle, warning, locationOutline, logOutOutline } from 'ionicons/icons';
import { AsistenciaService } from '../../services/asistencia.service';
import { QrSesion, AsistenciaRegistro } from '../../models/asistencia.models';

@Component({
  selector: 'app-asistencia-publica',
  standalone: true,
  imports: [IonicModule, FormsModule, NgIf, DatePipe],
  templateUrl: './asistencia-publica.page.html',
  styleUrls: ['./asistencia-publica.page.scss']
})
export class AsistenciaPublicaPage implements OnInit {
  sesion: QrSesion | null = null;
  loading = true;
  error = '';
  gpsStatus: 'solicitando' | 'validando' | 'dentro' | 'fuera' | 'denegado' | 'no_disponible' = 'solicitando';
  distanciaMetros = 0;

  username = '';
  password = '';
  userLat: number | null = null;
  userLon: number | null = null;
  registrando = false;
  registrado = false;
  successMsg = '';
  estado = '';

  activeEntry: AsistenciaRegistro | null = null;
  showExitPrompt = false;
  exitoSalida = false;
  salidaEn: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private asisService: AsistenciaService
  ) {
    addIcons({ closeCircleOutline, checkmarkCircleOutline, checkmarkCircle, warning, locationOutline, logOutOutline });
  }

  ngOnInit() {
    const codigo = this.route.snapshot.paramMap.get('codigo');
    if (codigo) {
      this.cargarSesion(codigo);
    } else {
      this.error = 'Código de sesión no válido';
      this.loading = false;
    }
  }

  private cargarSesion(codigo: string) {
    this.asisService.getSesionPublica(codigo).subscribe({
      next: (sesion) => {
        this.sesion = sesion;
        this.loading = false;
        this.solicitarGps();
      },
      error: (err) => {
        if (err.status === 410) {
          this.error = 'Esta sesión ha expirado';
        } else {
          this.error = 'Sesión no encontrada';
        }
        this.loading = false;
      }
    });
  }

  private solicitarGps() {
    if (!navigator.geolocation) {
      this.gpsStatus = 'no_disponible';
      return;
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        this.userLat = position.coords.latitude;
        this.userLon = position.coords.longitude;
        this.validarUbicacion();
      },
      (err) => {
        if (err.code === err.PERMISSION_DENIED) {
          this.gpsStatus = 'denegado';
        } else {
          this.gpsStatus = 'no_disponible';
        }
      },
      { enableHighAccuracy: true, timeout: 15000 }
    );
  }

  private validarUbicacion() {
    if (!this.sesion || this.userLat === null || this.userLon === null) return;

    this.gpsStatus = 'validando';

    this.asisService.getSedes().subscribe({
      next: (sedes) => {
        const sede = sedes.find(s => s.id === this.sesion!.sedeId);
        if (!sede) {
          this.gpsStatus = 'no_disponible';
          return;
        }

        this.distanciaMetros = this.asisService.haversineDistance(
          this.userLat!, this.userLon!, sede.latitud, sede.longitud
        );

        if (this.distanciaMetros <= sede.geocercaMetros) {
          this.gpsStatus = 'dentro';
        } else {
          this.gpsStatus = 'fuera';
        }
      },
      error: () => {
        this.gpsStatus = 'no_disponible';
      }
    });
  }

  private getPayload() {
    return {
      codigo: this.sesion!.codigo,
      username: this.username,
      password: this.password,
      latitud: this.userLat,
      longitud: this.userLon
    };
  }

  confirmarAsistencia() {
    if (!this.username || !this.password || !this.sesion) {
      this.error = 'Ingrese usuario y contraseña';
      return;
    }

    this.registrando = true;
    this.error = '';
    this.showExitPrompt = false;
    this.activeEntry = null;

    this.asisService.verificarRegistro(this.getPayload()).subscribe({
      next: (res) => {
        if (res.tieneRegistro && res.registro) {
          this.activeEntry = res.registro;
          this.showExitPrompt = true;
          this.registrando = false;
        } else {
          this.registrarEntrada();
        }
      },
      error: (err) => {
        this.manejarError(err);
      }
    });
  }

  registrarEntrada() {
    this.registrando = true;
    this.error = '';

    this.asisService.registrar(this.getPayload()).subscribe({
      next: (res) => {
        this.registrado = true;
        this.activeEntry = res;
        this.estado = res.estado || '';
        const estadoLabels: Record<string, string> = {
          'a_tiempo': 'Llegó a tiempo',
          'tarde': 'Llegó tarde',
          'libre': 'Sin horario asignado',
          'licencia': 'De licencia',
          'vacaciones': 'De vacaciones',
          'permiso': 'Con permiso'
        };
        this.successMsg = `Registro exitoso. ${estadoLabels[res.estado] || res.estado}`;
        this.registrando = false;
      },
      error: (err) => {
        this.manejarError(err);
      }
    });
  }

  registrarSalida() {
    if (!this.sesion) return;

    this.registrando = true;
    this.error = '';

    this.asisService.registrarSalida(this.getPayload()).subscribe({
      next: (res) => {
        this.exitoSalida = true;
        this.salidaEn = res.salidaEn;
        this.registrado = false;
        this.registrando = false;
      },
      error: (err) => {
        this.manejarError(err);
      }
    });
  }

  cancelarSalida() {
    this.showExitPrompt = false;
    this.activeEntry = null;
  }

  private manejarError(err: any) {
    if (err.status === 401 || err.status === 403) {
      this.error = err.error || 'Credenciales incorrectas';
    } else {
      this.error = err.error || 'Error. Intente nuevamente.';
    }
    this.registrando = false;
  }

  reintentarGps() {
    this.gpsStatus = 'solicitando';
    this.solicitarGps();
  }

  reiniciar() {
    this.registrado = false;
    this.exitoSalida = false;
    this.salidaEn = null;
    this.registrando = false;
    this.showExitPrompt = false;
    this.activeEntry = null;
    this.username = '';
    this.password = '';
    this.successMsg = '';
    this.estado = '';
    this.error = '';
  }
}
