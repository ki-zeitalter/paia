import {Component, ComponentRef, EventEmitter, Input, OnInit, Output, ViewChild, ViewContainerRef} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatIconModule} from '@angular/material/icon';
import {MatMenuModule} from '@angular/material/menu';
import {MatButtonModule} from '@angular/material/button';
import {MatDialog} from '@angular/material/dialog';
import {Widget} from '../models/widget';
import {WidgetRegistryService} from '../services/widget-registry.service';
import {WidgetConfigDialogComponent} from './widget-config-dialog/widget-config-dialog.component';
import {WidgetService} from '../services/widget.service';

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
  @Input() config?: {[key: string]: any};
  @Output() close = new EventEmitter<number>();
  @ViewChild('widgetContent', { read: ViewContainerRef, static: true }) widgetContentRef!: ViewContainerRef;

  private componentRef: ComponentRef<any> | null = null;

  constructor(
    private widgetRegistry: WidgetRegistryService,
    private widgetService: WidgetService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadWidgetComponent();
  }

  onClose(): void {
    this.close.emit(this.index);
  }

  onOpenConfigDialog(): void {
    // Überprüfen, ob Widget Konfigurationsparameter hat
    if (!this.widget.configParameters || this.widget.configParameters.length === 0) {
      return;
    }

    const dialogRef = this.dialog.open(WidgetConfigDialogComponent, {
      width: '500px',
      data: {
        widget: this.widget,
        currentConfig: this.config
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.widgetService.updateWidgetConfig(this.index, result);
        // Komponente neu laden mit aktualisierter Konfiguration
        this.loadWidgetComponent();
      }
    });
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
      
      // Übergebe Konfigurations-Daten, falls vorhanden
      if (this.config && this.componentRef.instance.config === undefined) {
        this.componentRef.instance.config = this.config;
      }
    }
  }
}
