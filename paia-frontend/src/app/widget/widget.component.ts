import {Component, ComponentRef, EventEmitter, Input, OnInit, Output, ViewChild, ViewContainerRef} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button';
import {Widget} from '../models/widget';
import {WidgetRegistryService} from '../services/widget-registry.service';

@Component({
  selector: 'app-widget',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatMenuModule, MatButtonModule],
  templateUrl: './widget.component.html',
  styleUrls: ['./widget.component.scss']
})
export class WidgetComponent implements OnInit {
  @Input() widget!: Widget;
  @Input() index!: number;
  @Output() close = new EventEmitter<number>();
  @ViewChild('widgetContent', { read: ViewContainerRef, static: true }) widgetContentRef!: ViewContainerRef;

  private componentRef: ComponentRef<any> | null = null;

  constructor(private widgetRegistry: WidgetRegistryService) {}

  ngOnInit(): void {
    this.loadWidgetComponent();
  }

  onClose(): void {
    this.close.emit(this.index);
  }

  private loadWidgetComponent(): void {

    // Leere den Container, falls bereits eine Komponente geladen ist
    if (this.componentRef) {
      this.componentRef.destroy();
      this.componentRef = null;
    }

    // Hole die passende Komponente aus der Registry
    const component = this.widgetRegistry.getComponent(this.widget.id);
    if (component) {
      this.componentRef = this.widgetContentRef.createComponent(component);
      // Übergebe Widget-Daten an die Komponente, falls nötig
      if (this.componentRef.instance.widget === undefined) {
        this.componentRef.instance.widget = this.widget;
      }
    }
  }
}
