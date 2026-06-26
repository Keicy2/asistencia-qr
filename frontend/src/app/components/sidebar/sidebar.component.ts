import { Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { addIcons } from 'ionicons';
import { gridOutline, peopleOutline, logOutOutline } from 'ionicons/icons';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [IonicModule],
  template: `
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>Asistencia QR</h2>
      </div>
      <div class="sidebar-menu">
        <div class="menu-item active" (click)="goDashboard()">
          <ion-icon name="grid-outline"></ion-icon>
          <span>Panel</span>
        </div>
        <div class="menu-item" (click)="goAsistencia()">
          <ion-icon name="people-outline"></ion-icon>
          <span>Asistencia</span>
        </div>
      </div>
      <div class="sidebar-footer">
        <div class="menu-item" (click)="logout()">
          <ion-icon name="log-out-outline"></ion-icon>
          <span>Cerrar sesión</span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .sidebar {
      width: 250px;
      background: #1d3557;
      color: white;
      display: flex;
      flex-direction: column;
      height: 100vh;
    }
    .sidebar-header {
      padding: 20px;
      border-bottom: 1px solid rgba(255,255,255,0.1);
    }
    .sidebar-header h2 {
      margin: 0;
      font-size: 18px;
      font-weight: 600;
    }
    .sidebar-menu {
      flex: 1;
      padding: 10px 0;
    }
    .menu-item {
      display: flex;
      align-items: center;
      padding: 12px 20px;
      cursor: pointer;
      transition: background 0.2s;
      gap: 12px;
      color: rgba(255,255,255,0.7);
    }
    .menu-item:hover, .menu-item.active {
      background: rgba(255,255,255,0.1);
      color: white;
    }
    .menu-item ion-icon {
      font-size: 20px;
    }
    .sidebar-footer {
      border-top: 1px solid rgba(255,255,255,0.1);
    }
  `]
})
export class SidebarComponent {
  constructor(private router: Router, private auth: AuthService) {
    addIcons({ gridOutline, peopleOutline, logOutOutline });
  }

  goDashboard() {
    this.router.navigate(['/dashboard']);
  }

  goAsistencia() {
    this.router.navigate(['/asistencia']);
  }

  logout() {
    this.auth.logout();
    this.router.navigate(['/home']);
  }
}
