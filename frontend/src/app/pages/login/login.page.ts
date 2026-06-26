import { Component } from '@angular/core';
import { IonicModule } from '@ionic/angular';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { addIcons } from 'ionicons';
import { qrCodeOutline, mailOutline, lockClosedOutline, alertCircleOutline } from 'ionicons/icons';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [IonicModule, FormsModule, NgIf],
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.scss']
})
export class LoginPage {
  correo = '';
  password = '';
  error = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {
    addIcons({ qrCodeOutline, mailOutline, lockClosedOutline, alertCircleOutline });
    if (this.auth.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  login() {
    if (!this.correo || !this.password) {
      this.error = 'Ingrese correo y contraseña';
      return;
    }

    this.loading = true;
    this.error = '';

    this.auth.login(this.correo, this.password).subscribe({
      next: (res) => {
        if (res.status === 'success' && res.token) {
          this.auth.saveToken(res.token);
          this.router.navigate(['/dashboard']);
        } else {
          this.error = res.message || 'Error al iniciar sesión';
          this.loading = false;
        }
      },
      error: (err) => {
        this.error = err.error?.message || 'Correo o contraseña incorrectos';
        this.loading = false;
      }
    });
  }
}
