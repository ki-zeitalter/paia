import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule, MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ModelConfigDialogComponent } from './model-config-dialog/model-config-dialog.component';
import { ModelService } from '../../services/model.service';
import { LlmModel, ModelConfig } from '../../models/model';

@Component({
  selector: 'app-model-config',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatDividerModule,
    MatListModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './model-config.component.html',
  styleUrls: ['./model-config.component.scss']
})
export class ModelConfigComponent implements OnInit {
  models: LlmModel[] = [];
  isLoading = true;
  hasError = false;

  constructor(
    private modelService: ModelService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadModels();
  }

  loadModels(): void {
    this.isLoading = true;
    this.hasError = false;
    
    this.modelService.getAllModels().subscribe({
      next: (models) => {
        this.models = models;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Fehler beim Laden der Modelle:', error);
        this.isLoading = false;
        this.hasError = true;
      }
    });
  }

  openConfigDialog(model: LlmModel): void {
    const dialogRef = this.dialog.open(ModelConfigDialogComponent, {
      width: '500px',
      data: { modelId: model.id, modelName: model.name }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.saveModelConfig(model.id, result);
      }
    });
  }

  private saveModelConfig(modelId: string, config: ModelConfig): void {
    this.modelService.saveModelConfig(modelId, config).subscribe({
      next: () => {
        this.loadModels(); // Modelle neu laden, um den aktualisierten Status zu erhalten
      },
      error: (error) => {
        console.error('Fehler beim Speichern der Konfiguration:', error);
        // Hier könnte eine Fehlerbehandlung erfolgen (z.B. Benachrichtigung des Benutzers)
      }
    });
  }
} 