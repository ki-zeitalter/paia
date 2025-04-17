import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Widget, WidgetConfigParameter } from '../../models/widget';

export interface WidgetConfigDialogData {
  widget: Widget;
  currentConfig?: {[key: string]: any};
}

@Component({
  selector: 'app-widget-config-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './widget-config-dialog.component.html',
  styleUrls: ['./widget-config-dialog.component.scss']
})
export class WidgetConfigDialogComponent implements OnInit {
  configForm!: FormGroup;
  configParams: WidgetConfigParameter[] = [];

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<WidgetConfigDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: WidgetConfigDialogData
  ) {}

  ngOnInit() {
    this.configParams = this.data.widget.configParameters || [];
    this.createForm();
  }

  private createForm() {
    const group: any = {};

    this.configParams.forEach(param => {
      // Füge einen FormControl für jeden Parameter hinzu
      const currentValue = this.data.currentConfig?.[param.propertyName] || '';
      group[param.propertyName] = [
        currentValue, 
        param.required ? Validators.required : []
      ];
    });

    this.configForm = this.fb.group(group);
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.configForm.valid) {
      this.dialogRef.close(this.configForm.value);
    }
  }
} 