import { Component, OnInit } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf, NgClass, DatePipe } from '@angular/common';
import { Router } from '@angular/router';
import { addIcons } from 'ionicons';
import { createOutline } from 'ionicons/icons';
import { SidebarComponent } from '../../components/sidebar/sidebar.component';
import { TopbarComponent } from '../../components/topbar/topbar.component';
import { AsistenciaService } from '../../services/asistencia.service';
import { AuthService } from '../../services/auth.service';
import { Usuario } from '../../models/asistencia.models';

@Component({
  selector: 'app-asistencia',
  standalone: true,
  imports: [IonicModule, FormsModule, NgFor, NgIf, NgClass, DatePipe, SidebarComponent, TopbarComponent],
  template: `
    <div class="sidebar-layout">
      <app-sidebar></app-sidebar>
      <div class="sidebar-content">
        <app-topbar [title]="'Gestión de Usuarios'" [userName]="userName"></app-topbar>
        <div class="page-content">
          <div *ngIf="error" class="alert alert-error">{{ error }}</div>
          <div *ngIf="success" class="alert alert-success">{{ success }}</div>

          <div class="card">
            <div class="card-header">
              <h3>Usuarios Registrados</h3>
              <ion-button size="small" fill="outline" (click)="showForm = !showForm">
                {{ showForm ? 'Cancelar' : '+ Nuevo usuario' }}
              </ion-button>
            </div>

            <div *ngIf="showForm" class="user-form">
              <div class="form-row">
                <div class="form-group">
                  <label>Nombre *</label>
                  <input type="text" [(ngModel)]="formNombre" class="form-control" />
                </div>
                <div class="form-group">
                  <label>Correo *</label>
                  <input type="email" [(ngModel)]="formCorreo" class="form-control" />
                </div>
                <div class="form-group">
                  <label>Contraseña *</label>
                  <input type="password" [(ngModel)]="formPassword" class="form-control" />
                </div>
              </div>
              <div class="form-row">
                <div class="form-group">
                  <label>Hora entrada</label>
                  <input type="time" [(ngModel)]="formHoraEntrada" class="form-control" />
                </div>
                <div class="form-group">
                  <label>Hora salida</label>
                  <input type="time" [(ngModel)]="formHoraSalida" class="form-control" />
                </div>
                <div class="form-group">
                  <label>Estado</label>
                  <select [(ngModel)]="formEstado" class="form-control">
                    <option value="activo">Activo</option>
                    <option value="licencia">Licencia</option>
                    <option value="vacaciones">Vacaciones</option>
                    <option value="permiso">Permiso</option>
                  </select>
                </div>
              </div>
              <ion-button size="small" (click)="crearUsuario()" [disabled]="saving">
                {{ saving ? 'Guardando...' : 'Guardar usuario' }}
              </ion-button>
            </div>

            <table class="table" *ngIf="usuarios.length > 0">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Nombre</th>
                  <th>Correo</th>
                  <th>Hora entrada</th>
                  <th>Hora salida</th>
                  <th>Estado</th>
                  <th>Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let u of usuarios; let i = index">
                  <td>{{ i + 1 }}</td>
                  <td>{{ u.nombre }}</td>
                  <td>{{ u.correo }}</td>
                  <td>{{ u.horaEntrada || '-' }}</td>
                  <td>{{ u.horaSalida || '-' }}</td>
                  <td>
                    <span class="badge estado" [ngClass]="u.estado">{{ u.estado }}</span>
                  </td>
                  <td>
                    <ion-button size="small" fill="clear" (click)="editarUsuario(u)">
                      <ion-icon name="create-outline"></ion-icon>
                    </ion-button>
                  </td>
                </tr>
              </tbody>
            </table>
            <div *ngIf="usuarios.length === 0" class="empty-state">
              No hay usuarios registrados
            </div>
          </div>

          <div class="card" *ngIf="editUser">
            <h3>Editar: {{ editUser.nombre }}</h3>
            <div class="form-row">
              <div class="form-group">
                <label>Nombre</label>
                <input type="text" [(ngModel)]="editNombre" class="form-control" />
              </div>
              <div class="form-group">
                <label>Correo</label>
                <input type="email" [(ngModel)]="editCorreo" class="form-control" />
              </div>
              <div class="form-group">
                <label>Nueva contraseña (dejar vacío para no cambiar)</label>
                <input type="password" [(ngModel)]="editPassword" class="form-control" />
              </div>
            </div>
            <div class="form-row">
              <div class="form-group">
                <label>Hora entrada</label>
                <input type="time" [(ngModel)]="editHoraEntrada" class="form-control" />
              </div>
              <div class="form-group">
                <label>Hora salida</label>
                <input type="time" [(ngModel)]="editHoraSalida" class="form-control" />
              </div>
              <div class="form-group">
                <label>Estado</label>
                <select [(ngModel)]="editEstado" class="form-control">
                  <option value="activo">Activo</option>
                  <option value="licencia">Licencia</option>
                  <option value="vacaciones">Vacaciones</option>
                  <option value="permiso">Permiso</option>
                </select>
              </div>
            </div>
            <div class="edit-actions">
              <ion-button size="small" (click)="guardarEdicion()" [disabled]="saving">
                {{ saving ? 'Guardando...' : 'Guardar cambios' }}
              </ion-button>
              <ion-button size="small" fill="outline" (click)="cancelarEdicion()">Cancelar</ion-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .sidebar-layout { display: flex; min-height: 100vh; background: #f5f7fa; }
    .sidebar-content { flex: 1; display: flex; flex-direction: column; }
    .page-content { padding: 24px; max-width: 1200px; margin: 0 auto; width: 100%; box-sizing: border-box; }
    .card { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.06); margin-bottom: 20px; }
    .card h3 { margin: 0 0 20px; font-size: 16px; font-weight: 600; color: #1d3557; }
    .card-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
    .card-header h3 { margin: 0; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr 1fr; gap: 12px; margin-bottom: 12px; }
    .form-group { margin-bottom: 12px; }
    .form-group label { display: block; font-size: 13px; font-weight: 500; color: #6c757d; margin-bottom: 4px; }
    .form-control { width: 100%; padding: 10px 12px; border: 1px solid #dde1e6; border-radius: 8px; font-size: 14px; transition: border-color 0.2s; box-sizing: border-box; }
    .form-control:focus { outline: none; border-color: #4361ee; }
    .user-form { background: #f8f9fa; padding: 16px; border-radius: 8px; margin-bottom: 16px; }
    .edit-actions { display: flex; gap: 8px; margin-top: 8px; }
    .table { width: 100%; border-collapse: collapse; font-size: 13px; }
    .table th { text-align: left; padding: 10px 12px; background: #f8f9fa; color: #6c757d; font-weight: 600; border-bottom: 2px solid #e0e0e0; }
    .table td { padding: 10px 12px; border-bottom: 1px solid #f0f0f0; }
    .table tr:hover td { background: #f8f9fa; }
    .badge { font-size: 11px; padding: 3px 8px; border-radius: 12px; font-weight: 500; }
    .badge.estado.activo { background: #d4edda; color: #155724; }
    .badge.estado.licencia, .badge.estado.vacaciones, .badge.estado.permiso { background: #e2e3f1; color: #383d6b; }
    .empty-state { text-align: center; padding: 40px 20px; color: #6c757d; font-size: 14px; }
    .alert { padding: 12px 16px; border-radius: 8px; margin-bottom: 16px; font-size: 14px; }
    .alert-error { background: #f8d7da; color: #721c24; }
    .alert-success { background: #d4edda; color: #155724; }
  `]
})
export class AsistenciaPage implements OnInit {
  usuarios: Usuario[] = [];
  showForm = false;
  formNombre = '';
  formCorreo = '';
  formPassword = '';
  formHoraEntrada = '';
  formHoraSalida = '';
  formEstado = 'activo';
  saving = false;
  error = '';
  success = '';
  userName = '';

  editUser: Usuario | null = null;
  editNombre = '';
  editCorreo = '';
  editPassword = '';
  editHoraEntrada = '';
  editHoraSalida = '';
  editEstado = 'activo';

  constructor(
    private asisService: AsistenciaService,
    private auth: AuthService,
    private router: Router
  ) {
    addIcons({ createOutline });
  }

  ngOnInit() {
    this.loadUser();
    this.loadUsuarios();
  }

  private loadUser() {
    this.auth.me().subscribe({
      next: (user) => { if (user) this.userName = user.nombre; }
    });
  }

  private loadUsuarios() {
    this.asisService.getUsuarios().subscribe({
      next: (data) => this.usuarios = data,
      error: () => this.error = 'Error al cargar usuarios'
    });
  }

  crearUsuario() {
    if (!this.formNombre || !this.formCorreo || !this.formPassword) {
      this.error = 'Complete nombre, correo y contraseña';
      return;
    }

    this.saving = true;
    this.error = '';
    this.success = '';

    this.asisService.crearUsuario({
      nombre: this.formNombre,
      correo: this.formCorreo,
      password: this.formPassword,
      horaEntrada: this.formHoraEntrada || undefined,
      horaSalida: this.formHoraSalida || undefined,
      estado: this.formEstado
    }).subscribe({
      next: () => {
        this.success = 'Usuario creado correctamente';
        this.formNombre = '';
        this.formCorreo = '';
        this.formPassword = '';
        this.formHoraEntrada = '';
        this.formHoraSalida = '';
        this.formEstado = 'activo';
        this.showForm = false;
        this.saving = false;
        this.loadUsuarios();
      },
      error: (err) => {
        this.error = err.error || 'Error al crear usuario';
        this.saving = false;
      }
    });
  }

  editarUsuario(u: Usuario) {
    this.editUser = u;
    this.editNombre = u.nombre;
    this.editCorreo = u.correo;
    this.editPassword = '';
    this.editHoraEntrada = u.horaEntrada || '';
    this.editHoraSalida = u.horaSalida || '';
    this.editEstado = u.estado;
  }

  guardarEdicion() {
    if (!this.editUser) return;

    this.saving = true;
    this.error = '';
    this.success = '';

    const data: any = {
      nombre: this.editNombre,
      correo: this.editCorreo,
      horaEntrada: this.editHoraEntrada || '',
      horaSalida: this.editHoraSalida || '',
      estado: this.editEstado
    };
    if (this.editPassword) data.password = this.editPassword;

    this.asisService.actualizarUsuario(this.editUser.id, data).subscribe({
      next: () => {
        this.success = 'Usuario actualizado';
        this.editUser = null;
        this.saving = false;
        this.loadUsuarios();
      },
      error: (err) => {
        this.error = err.error || 'Error al actualizar';
        this.saving = false;
      }
    });
  }

  cancelarEdicion() {
    this.editUser = null;
  }
}
