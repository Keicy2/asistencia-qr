import { Component, OnInit } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { NgIf, DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { addIcons } from 'ionicons';
import { closeCircleOutline, checkmarkCircleOutline, checkmarkCircle, warning, locationOutline } from 'ionicons/icons';
import { AsistenciaService } from '../../services/asistencia.service';
import { QrSesion } from '../../models/asistencia.models';

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

  correo = '';
  password = '';
  userLat: number | null = null;
  userLon: number | null = null;
  registrando = false;
  registrado = false;
  successMsg = '';
  estado = '';

  constructor(
    private route: ActivatedRoute,
    private asisService: AsistenciaService
  ) {
    addIcons({ closeCircleOutline, checkmarkCircleOutline, checkmarkCircle, warning, locationOutline });
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

  registrar() {
    if (!this.correo || !this.password || !this.sesion) {
      this.error = 'Ingrese correo y contraseña';
      return;
    }

    this.registrando = true;
    this.error = '';

    this.asisService.registrar({
      codigo: this.sesion.codigo,
      correo: this.correo,
      password: this.password,
      latitud: this.userLat,
      longitud: this.userLon
    }).subscribe({
      next: (res) => {
        this.registrado = true;
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
        if (err.status === 401 || err.status === 403) {
          this.error = err.error || 'Credenciales incorrectas';
        } else {
          this.error = 'Error al registrar. Intente nuevamente.';
        }
        this.registrando = false;
      }
    });
  }

  reintentarGps() {
    this.gpsStatus = 'solicitando';
    this.solicitarGps();
  }
}
