import {Injectable, Type} from '@angular/core';
import {ClockWidgetComponent} from '../widget/clock/clock-widget.component';

import {ChatComponent} from '../widget/chat/chat.component';

@Injectable({
  providedIn: 'root'
})
export class WidgetRegistryService {
  private widgetMap = new Map<string, Type<any>>();

  constructor() {
    this.registerWidgets();
  }

  private registerWidgets(): void {
    // Registriere alle verfügbaren Widget-Komponenten
    this.widgetMap.set('clock', ClockWidgetComponent);
    this.widgetMap.set('chat', ChatComponent);
  }

  getComponent(type: string): Type<any> | undefined {
    return this.widgetMap.get(type);
  }
}
