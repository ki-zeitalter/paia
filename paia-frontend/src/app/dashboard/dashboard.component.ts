import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA, ElementRef, ViewChildren, QueryList, AfterViewInit, Renderer2, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterModule } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { WidgetService } from '../services/widget.service';
import { AuthService } from '../services/auth.service';
import { WidgetComponent } from '../widget/widget.component';
import { Widget, DashboardConfiguration, WidgetInstance, WidgetPosition } from '../models/widget';
import { take, switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    RouterModule,
    WidgetComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {
  availableWidgets$: Observable<Widget[]>;
  dashboardConfig$: Observable<DashboardConfiguration>;
  widgets: { widget: Widget, instance: WidgetInstance }[] = [];
  userInfo: any;
  
  // Grid Konfiguration
  gridCols = 12;
  gridRows = 10;
  cellSize = 80; // Standardgröße einer Zelle in Pixeln
  
  // Drag & Drop
  isDragging = false;
  isResizing = false;
  dragStartX = 0;
  dragStartY = 0;
  currentDragElement: HTMLElement | null = null;
  currentResizeElement: HTMLElement | null = null;
  draggedWidgetIndex: number = -1;
  resizedWidgetIndex: number = -1;
  draggedWidgetStartPos = { x: 0, y: 0 };
  resizedWidgetStartSize = { cols: 0, rows: 0 };
  
  private subscriptions: Subscription[] = [];
  
  @ViewChildren('widgetElement') widgetElements!: QueryList<ElementRef>;

  constructor(
    private widgetService: WidgetService,
    private authService: AuthService,
    private renderer: Renderer2
  ) {
    this.availableWidgets$ = this.widgetService.availableWidgets$;
    this.dashboardConfig$ = this.widgetService.dashboardConfiguration$;
    this.userInfo = this.authService.identityClaims;
  }

  ngOnInit(): void {
    this.loadDashboard();
  }
  
  ngAfterViewInit(): void {
    this.setupDragAndResize();
  }
  
  ngOnDestroy(): void {
    // Alle Subscriptions aufräumen
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  private loadDashboard(): void {
    // Die verfügbaren Widgets laden und dann erst die Dashboard-Konfiguration laden
    const dashboardSub = this.widgetService.loadAvailableWidgets().pipe(
      switchMap(() => this.widgetService.loadDashboardConfiguration())
    ).subscribe();
    
    this.subscriptions.push(dashboardSub);
    
    // Auf Änderungen der Konfiguration reagieren
    const configSub = this.dashboardConfig$.subscribe(config => {
      this.updateWidgets(config);
    });
    this.subscriptions.push(configSub);
  }

  private updateWidgets(config: DashboardConfiguration): void {
    if (!config || !config.widgets) {
      this.widgets = [];
      console.log('no config');
      return;
    }
    
    const availableSub = this.availableWidgets$.pipe(take(1)).subscribe(availableWidgets => {
      const widgetsMap: Record<string, Widget> = availableWidgets.reduce((map: Record<string, Widget>, widget: Widget) => {
        map[widget.id] = widget;
        return map;
      }, {});

      this.widgets = config.widgets.map(instance => {
        return {
          widget: widgetsMap[instance.widgetId],
          instance: instance
        };
      }).filter(item => item.widget); // Filter out widgets that don't exist
      
    });
    this.subscriptions.push(availableSub);
    
    // Nach dem Laden der Widgets muss ggf. Drag & Drop neu initialisiert werden
    setTimeout(() => {
      this.setupDragAndResize();
    }, 100);
  }

  addWidget(widget: Widget): void {
    this.widgetService.addWidget(widget.id);
  }

  removeWidget(index: number): void {
    this.widgetService.removeWidget(index);
  }

  // Berechnung der Grid-Position und Positionierung der Widgets
  getWidgetStyle(widget: WidgetInstance): any {
    return {
      'grid-column': `${widget.position.x + 1} / span ${widget.position.cols}`,
      'grid-row': `${widget.position.y + 1} / span ${widget.position.rows}`,
    };
  }
  
  // Drag & Drop Logik
  private setupDragAndResize(): void {
    this.widgetElements.forEach((elem, index) => {
      const element = elem.nativeElement;
      
      // Drag Handle hinzufügen (Header des Widgets)
      const dragHandle = element.querySelector('.widget-header');
      if (dragHandle) {
        this.renderer.listen(dragHandle, 'mousedown', (event) => {
          this.startDrag(event, element, index);
        });
      }
      
      // Resize Handle hinzufügen
      const resizeHandle = element.querySelector('.resize-handle');
      if (resizeHandle) {
        this.renderer.listen(resizeHandle, 'mousedown', (event) => {
          this.startResize(event, element, index);
        });
      }
    });
    
    // Globale Event Listener für Drag & Resize
    this.renderer.listen('document', 'mousemove', (event) => {
      if (this.isDragging) {
        this.onDrag(event);
      }
      if (this.isResizing) {
        this.onResize(event);
      }
    });
    
    this.renderer.listen('document', 'mouseup', () => {
      if (this.isDragging || this.isResizing) {
        this.stopDragAndResize();
      }
    });
  }
  
  private startDrag(event: MouseEvent, element: HTMLElement, widgetIndex: number): void {
    // Überprüfen, ob der Klick auf einem Button oder Icon im Header war
    const target = event.target as HTMLElement;
    if (target.closest('button') || target.closest('mat-icon')) {
      return; // Drag nicht starten, wenn auf Button oder Icon geklickt wurde
    }
    
    event.preventDefault();
    
    this.isDragging = true;
    this.currentDragElement = element;
    this.draggedWidgetIndex = widgetIndex;
    
    // Startposition des Drag-Vorgangs
    this.dragStartX = event.clientX;
    this.dragStartY = event.clientY;
    
    // Ursprüngliche Widget-Position speichern
    this.draggedWidgetStartPos = {
      x: this.widgets[widgetIndex].instance.position.x,
      y: this.widgets[widgetIndex].instance.position.y
    };
    
    // Styling während des Drags
    this.renderer.addClass(element, 'dragging');
  }
  
  private onDrag(event: MouseEvent): void {
    if (!this.isDragging || this.draggedWidgetIndex === -1 || !this.currentDragElement) {
      return;
    }
    
    const deltaX = event.clientX - this.dragStartX;
    const deltaY = event.clientY - this.dragStartY;
    
    // Berechnung der Grid-Zellen-Veränderung
    const gridDeltaX = Math.round(deltaX / this.cellSize);
    const gridDeltaY = Math.round(deltaY / this.cellSize);
    
    // Neue Position berechnen
    const newX = Math.max(0, Math.min(this.gridCols - 1, this.draggedWidgetStartPos.x + gridDeltaX));
    const newY = Math.max(0, Math.min(this.gridRows - 1, this.draggedWidgetStartPos.y + gridDeltaY));
    
    // Widget-Position aktualisieren
    const widget = this.widgets[this.draggedWidgetIndex];
    widget.instance.position.x = newX;
    widget.instance.position.y = newY;
    
    // View aktualisieren
    this.updateWidgetPosition(widget.instance);
  }
  
  private startResize(event: MouseEvent, element: HTMLElement, widgetIndex: number): void {
    event.preventDefault();
    event.stopPropagation();
    
    this.isResizing = true;
    this.currentResizeElement = element;
    this.resizedWidgetIndex = widgetIndex;
    
    // Startposition des Resize-Vorgangs
    this.dragStartX = event.clientX;
    this.dragStartY = event.clientY;
    
    // Ursprüngliche Widget-Größe speichern
    const widget = this.widgets[widgetIndex].instance;
    this.resizedWidgetStartSize = {
      cols: widget.position.cols,
      rows: widget.position.rows
    };
    
    // Styling während des Resizings
    this.renderer.addClass(element, 'resizing');
  }
  
  private onResize(event: MouseEvent): void {
    if (!this.isResizing || this.resizedWidgetIndex === -1 || !this.currentResizeElement) {
      return;
    }
    
    const deltaX = event.clientX - this.dragStartX;
    const deltaY = event.clientY - this.dragStartY;
    
    // Berechnung der Grid-Zellen-Veränderung
    const gridDeltaX = Math.round(deltaX / this.cellSize);
    const gridDeltaY = Math.round(deltaY / this.cellSize);
    
    // Neue Größe berechnen
    const newCols = Math.max(1, Math.min(this.gridCols, this.resizedWidgetStartSize.cols + gridDeltaX));
    const newRows = Math.max(1, Math.min(this.gridRows, this.resizedWidgetStartSize.rows + gridDeltaY));
    
    // Widget-Größe aktualisieren
    const widget = this.widgets[this.resizedWidgetIndex];
    widget.instance.position.cols = newCols;
    widget.instance.position.rows = newRows;
    
    // View aktualisieren
    this.updateWidgetPosition(widget.instance);
  }
  
  private stopDragAndResize(): void {
    if (this.isDragging) {
      if (this.currentDragElement) {
        this.renderer.removeClass(this.currentDragElement, 'dragging');
      }
      this.isDragging = false;
      this.currentDragElement = null;
      this.saveDashboardState();
    }
    
    if (this.isResizing) {
      if (this.currentResizeElement) {
        this.renderer.removeClass(this.currentResizeElement, 'resizing');
      }
      this.isResizing = false;
      this.currentResizeElement = null;
      this.saveDashboardState();
    }
  }
  
  private updateWidgetPosition(widget: WidgetInstance): void {
    // Die View aktualisieren, ohne Change Detection auszulösen
    // Dies ist nur eine visuelle Aktualisierung während des Drag & Drop
    
    // In einem realen Projekt könnten wir hier direkt DOM-Manipulationen vermeiden
    // und stattdessen ChangeDetectorRef.detectChanges() verwenden, wenn nötig
    
    // Wir machen nichts in dieser Methode, sie ist nur ein Platzhalter
  }
  
  private saveDashboardState(): void {
    // Aktualisiere die Konfiguration im Dashboard-Konfigurationssubject 
    const widgets = this.widgets.map(item => item.instance);
    
    // Den aktuellen Zustand aus dem Service holen
    this.widgetService.dashboardConfiguration$.pipe(take(1)).subscribe(currentConfig => {
      // Neue Konfiguration erstellen und im Service aktualisieren
      const updatedConfig: DashboardConfiguration = { ...currentConfig, widgets };
      
      // Aktualisiere die Konfiguration im Service
      this.widgetService.updateDashboardConfig(updatedConfig);
      
      // Speichere die Konfiguration mit der Methode ohne Parameter
      this.widgetService.saveDashboardConfiguration().subscribe({
        next: () => {
          console.log('Dashboard state saved');
        },
        error: (error) => {
          console.error('Error saving dashboard state:', error);
        }
      });
    });
  }
  
  logout(): void {
    this.authService.logout();
  }
}

