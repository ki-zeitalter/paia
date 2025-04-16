import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { ModelService } from '../../../services/model.service';
import { ModelConfig } from '../../../models/model';

interface DialogData {
  modelId: string;
  modelName: string;
}

@Component({
  selector: 'app-model-config-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './model-config-dialog.component.html',
  styleUrls: ['./model-config-dialog.component.scss']
})
export class ModelConfigDialogComponent implements OnInit {
  configForm: FormGroup;
  loading = false;
  error = '';

  constructor(
    public dialogRef: MatDialogRef<ModelConfigDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData,
    private fb: FormBuilder,
    private modelService: ModelService
  ) {
    this.configForm = this.fb.group({
      apiKey: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadModelConfig();
  }

  loadModelConfig(): void {
    this.loading = true;
    this.error = '';

    this.modelService.getModelConfig(this.data.modelId).subscribe({
      next: (config) => {
        if (config && config.apiKey) {
          // API-Key wird aus Sicherheitsgründen nicht vollständig geladen
          // Wir zeigen an, dass ein API-Key konfiguriert ist, ohne ihn anzuzeigen
          this.configForm.patchValue({
            apiKey: '' // Leerer String, damit der Benutzer einen neuen API-Key eingeben kann
          });
        }
        this.loading = false;
      },
      error: (err) => {
        // Wenn 404 zurückkommt, ist das in Ordnung - es bedeutet, dass noch keine Konfiguration existiert
        if (err.status !== 404) {
          this.error = 'Fehler beim Laden der Konfiguration.';
          console.error('Fehler beim Laden der Modell-Konfiguration:', err);
        }
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.configForm.invalid) {
      return;
    }

    const config: ModelConfig = {
      modelId: this.data.modelId,
      apiKey: this.configForm.value.apiKey
    };

    this.dialogRef.close(config);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
} 