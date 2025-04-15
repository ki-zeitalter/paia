import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Widget, DashboardConfiguration, AvailableWidget } from '../models/widget';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Ruft die verfügbaren Widgets vom Backend ab
  getAvailableWidgets(): Observable<AvailableWidget[]> {
    return this.http.get<AvailableWidget[]>(`${this.apiUrl}/widgets/available`);
  }

  // Ruft die Dashboard-Konfiguration des Benutzers ab
  getDashboardConfiguration(): Observable<DashboardConfiguration> {
    return this.http.get<{configuration: string}>(`${this.apiUrl}/users/me/dashboard`)
      .pipe(
        map(response => {
          try {
            return JSON.parse(response.configuration) as DashboardConfiguration;
          } catch (error) {
            console.error('Fehler beim Parsen der Dashboard-Konfiguration:', error);
            return {} as DashboardConfiguration;
          }
        })
      );
  }

  // Speichert die Dashboard-Konfiguration des Benutzers
  saveDashboardConfiguration(config: DashboardConfiguration): Observable<void> {
    console.log('saveDashboardConfiguration', config);
    const configDto = {
      configuration: JSON.stringify(config)
    };
    return this.http.put<void>(`${this.apiUrl}/users/me/dashboard`, configDto);
  }
}
