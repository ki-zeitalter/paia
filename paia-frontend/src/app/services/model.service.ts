import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LlmModel, ModelConfig } from '../models/model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ModelService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  // Ruft alle verfügbaren Modelle mit Konfigurationsstatus ab
  getAllModels(): Observable<LlmModel[]> {
    return this.http.get<LlmModel[]>(`${this.apiUrl}/models`);
  }

  // Ruft die Konfiguration für ein bestimmtes Modell ab
  getModelConfig(modelId: string): Observable<ModelConfig> {
    return this.http.get<ModelConfig>(`${this.apiUrl}/models/${modelId}/config`);
  }

  // Speichert die Konfiguration für ein Modell
  saveModelConfig(modelId: string, config: ModelConfig): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/models/${modelId}/config`, config);
  }

  // Löscht die Konfiguration für ein Modell
  deleteModelConfig(modelId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/models/${modelId}/config`);
  }
} 