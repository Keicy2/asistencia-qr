import { Component, OnInit } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, NgClass, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { QRCodeComponent } from 'angularx-qrcode';
import { addIcons } from 'ionicons';
import { trashOutline } from 'ionicons/icons';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { TopbarComponent } from '../../components/topbar/topbar.component';
import { AsistenciaService } from '../../services/asistencia.service';
import { AuthService } from '../../services/auth.service';
import { Sede, QrSesion, AsistenciaRegistro } from '../../models/asistencia.models';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [IonicModule, FormsModule, NgFor, NgIf, NgClass, DatePipe, QRCodeComponent, SidebarComponent, TopbarComponent],
  templateUrl: './dashboard.page.html',
  styleUrls: ['./dashboard.page.scss']
})
export class DashboardPage implements OnInit {
  sedes: Sede[] = [];
  sesiones: QrSesion[] = [];
  registros: AsistenciaRegistro[] = [];
  selectedSesion: QrSesion | null = null;
  selectedSedeId: number | null = null;
  fecha = '';
  localIp = '';
  qrData = '';
  showQr = false;
  error = '';
  success = '';
  loading = false;
  deleting = false;
  userName = '';

  manualUsuarioId: number | null = null;
  usuarios: any[] = [];
  showManual = false;

  constructor(
    private asisService: AsistenciaService,
    private auth: AuthService,
    private router: Router
  ) {
    addIcons({ trashOutline });
  }

  ngOnInit() {
    this.loadSedes();
    this.loadSesiones();
    this.loadUser();
    this.detectLocalIp();
    this.loadUsuarios();
  }

  private detectLocalIp() {
    this.asisService.getServerInfo().subscribe({
      next: (info) => this.localIp = info.localIp,
      error: () => this.localIp = '127.0.0.1'
    });
  }

  private loadUser() {
    this.auth.me().subscribe({
      next: (user) => {
        if (user) this.userName = user.nombre;
      }
    });
  }

  private loadSedes() {
    this.asisService.getSedes().subscribe({
      next: (data) => {
        this.sedes = data;
        if (data.length > 0 && !this.selectedSedeId) {
          this.selectedSedeId = data[0].id;
        }
      },
      error: () => this.error = 'Error al cargar sedes'
    });
  }

  loadSesiones() {
    this.asisService.listarSesiones().subscribe({
      next: (data) => this.sesiones = data,
      error: () => this.error = 'Error al cargar sesiones'
    });
  }

  private loadUsuarios() {
    this.asisService.getUsuarios().subscribe({
      next: (data) => this.usuarios = data.filter(u => u.activo)
    });
  }

  selectSesion(sesion: QrSesion) {
    this.selectedSesion = sesion;
    this.qrData = `https://${this.localIp}:4200/#/asistencia/publica/${sesion.codigo}`;
    this.showQr = true;
    this.loadRegistros(sesion.id);
  }

  private loadRegistros(sesionId: number) {
    this.asisService.listarRegistros(sesionId).subscribe({
      next: (data) => this.registros = data,
      error: () => this.error = 'Error al cargar registros'
    });
  }

  generarQr() {
    if (!this.selectedSedeId || !this.fecha) {
      this.error = 'Seleccione sede y fecha';
      return;
    }

    this.loading = true;
    this.error = '';
    this.success = '';

    this.asisService.crearSesion({
      sedeId: this.selectedSedeId,
      fecha: this.fecha
    }).subscribe({
      next: (sesion) => {
        this.qrData = `https://${this.localIp}:4200/#/asistencia/publica/${sesion.codigo}`;
        this.showQr = true;
        this.success = 'Código QR generado exitosamente';
        this.loading = false;
        this.loadSesiones();
      },
      error: () => {
        this.error = 'Error al generar QR';
        this.loading = false;
      }
    });
  }

  eliminarSesion(event: Event, sesion: QrSesion) {
    event.stopPropagation();
    this.deleting = true;
    this.error = '';
    this.success = '';

    this.asisService.eliminarSesion(sesion.id).subscribe({
      next: () => {
        this.success = 'Sesión eliminada';
        this.deleting = false;
        if (this.selectedSesion?.id === sesion.id) {
          this.selectedSesion = null;
          this.registros = [];
        }
        this.loadSesiones();
      },
      error: () => {
        this.error = 'Error al eliminar sesión';
        this.deleting = false;
      }
    });
  }

  descargarCsv() {
    if (!this.selectedSesion) return;
    this.asisService.exportarCsv(this.selectedSesion.id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `asistencia-${this.selectedSesion!.sedeNombre}-${this.selectedSesion!.fecha}.csv`;
        a.click();
        URL.revokeObjectURL(url);
      },
      error: () => this.error = 'Error al descargar CSV'
    });
  }

  agregarManual() {
    if (!this.selectedSesion || !this.manualUsuarioId) {
      this.error = 'Seleccione un usuario';
      return;
    }

    this.asisService.manualRegistro({
      sesionId: this.selectedSesion.id,
      usuarioId: this.manualUsuarioId
    }).subscribe({
      next: () => {
        this.success = 'Registro manual agregado';
        this.manualUsuarioId = null;
        this.showManual = false;
        this.loadRegistros(this.selectedSesion!.id);
      },
      error: () => this.error = 'Error al agregar registro manual'
    });
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/home']);
  }
}
