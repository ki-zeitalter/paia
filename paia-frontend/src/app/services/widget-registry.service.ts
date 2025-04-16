import { Injectable, Type } from '@angular/core';
import { ClockWidgetComponent } from '../widget/clock/clock-widget.component';
import { WidgetType } from '../models/widget';

@Injectable({
  providedIn: 'root'
})
export class WidgetRegistryService {
  private widgetMap = new Map<WidgetType, Type<any>>();

  constructor() {
    this.registerWidgets();
  }

  private registerWidgets(): void {
    // Registriere alle verfügbaren Widget-Komponenten
    this.widgetMap.set(WidgetType.CLOCK, ClockWidgetComponent);
  }

  getComponent(type: WidgetType): Type<any> | undefined {
    return this.widgetMap.get(type);
  }
} 