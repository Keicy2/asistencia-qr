import { Routes } from '@angular/router';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  {
    path: 'home',
    loadComponent: () => import('./pages/login/login.page').then(m => m.LoginPage)
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./pages/dashboard/dashboard.page').then(m => m.DashboardPage),
    canActivate: [authGuard]
  },
  {
    path: 'asistencia',
    loadComponent: () => import('./pages/asistencia/asistencia.page').then(m => m.AsistenciaPage),
    canActivate: [authGuard]
  },
  {
    path: 'asistencia/publica/:codigo',
    loadComponent: () => import('./pages/asistencia-publica/asistencia-publica.page').then(m => m.AsistenciaPublicaPage)
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full'
  }
];
