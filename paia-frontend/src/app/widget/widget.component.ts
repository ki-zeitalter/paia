import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatButtonModule } from '@angular/material/button';
import { Widget } from '../models/widget';

@Component({
  selector: 'app-widget',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatMenuModule, MatButtonModule],
  templateUrl: './widget.component.html',
  styleUrls: ['./widget.component.scss']
})
export class WidgetComponent {
  @Input() widget!: Widget;
  @Input() index!: number;
  @Output() close = new EventEmitter<number>();

  onClose(): void {
    this.close.emit(this.index);
  }
}
