import { Injectable } from '@angular/core';
import { ApiService } from './api.service';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { LoginResponse, UserInfo } from '../models/asistencia.models';

@Injectable({ providedIn: 'root' })
export class AuthService {

  constructor(private api: ApiService) {}

  login(correo: string, password: string): Observable<LoginResponse> {
    return this.api.post<LoginResponse>('/api/auth/login', { correo, password });
  }

  me(): Observable<UserInfo | null> {
    return this.api.get<UserInfo>('/api/auth/me').pipe(
      catchError(() => of(null))
    );
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  saveToken(token: string): void {
    localStorage.setItem('token', token);
  }

  logout(): void {
    localStorage.removeItem('token');
  }
}
