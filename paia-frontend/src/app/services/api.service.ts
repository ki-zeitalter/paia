import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Widget, DashboardConfiguration } from '../models/widget';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Ruft die verfügbaren Widgets vom Backend ab
  getAvailableWidgets(): Observable<Widget[]> {
    return this.http.get<Widget[]>(`${this.apiUrl}/widgets/available`);
  }

  // Ruft die Dashboard-Konfiguration des Benutzers ab
  getDashboardConfiguration(): Observable<DashboardConfiguration> {
    return this.http.get<DashboardConfiguration>(`${this.apiUrl}/users/me/dashboard`);
  }

  // Speichert die Dashboard-Konfiguration des Benutzers
  saveDashboardConfiguration(config: DashboardConfiguration): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/users/me/dashboard`, config);
  }
}
