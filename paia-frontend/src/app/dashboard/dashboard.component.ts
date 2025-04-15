import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA, ElementRef, ViewChildren, QueryList, AfterViewInit, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { Observable } from 'rxjs';
import { WidgetService } from '../services/widget.service';
import { AuthService } from '../services/auth.service';
import { WidgetComponent } from '../widget/widget.component';
import { Widget, DashboardConfiguration, WidgetInstance, WidgetPosition } from '../models/widget';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    WidgetComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit, AfterViewInit {
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

  private loadDashboard(): void {
    this.widgetService.loadAvailableWidgets().subscribe();
    this.widgetService.loadDashboardConfiguration().subscribe(config => {
      this.updateWidgets(config);
    });
  }

  private updateWidgets(config: DashboardConfiguration): void {
    if (!config || !config.widgets) {
      this.widgets = [];
      console.log('no config');
      return;
    }
    console.log('config', config);
    this.availableWidgets$.pipe(take(1)).subscribe(availableWidgets => {
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
    console.log('widgets', this.widgets);
    
    // Nach dem Laden der Widgets muss ggf. Drag & Drop neu initialisiert werden
    setTimeout(() => {
      this.setupDragAndResize();
    }, 100);
  }

  addWidget(widget: Widget): void {
    console.log('addWidget', widget);
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
    if (!this.isDragging || !this.currentDragElement || this.draggedWidgetIndex === -1) return;
    
    // Berechne die Bewegung in Pixeln
    const deltaX = event.clientX - this.dragStartX;
    const deltaY = event.clientY - this.dragStartY;
    
    // Berechne die neue Grid-Position (umgerechnet von Pixeln zu Grid-Zellen)
    const deltaGridX = Math.round(deltaX / this.cellSize);
    const deltaGridY = Math.round(deltaY / this.cellSize);
    
    // Aktualisiere die Position des Widgets
    const newX = Math.max(0, Math.min(this.gridCols - this.widgets[this.draggedWidgetIndex].instance.position.cols, 
                               this.draggedWidgetStartPos.x + deltaGridX));
    const newY = Math.max(0, Math.min(this.gridRows - this.widgets[this.draggedWidgetIndex].instance.position.rows, 
                               this.draggedWidgetStartPos.y + deltaGridY));
    
    // Aktualisiere die Position im Modell
    const updatedPosition = {
      ...this.widgets[this.draggedWidgetIndex].instance.position,
      x: newX,
      y: newY
    };
    
    // UI aktualisieren
    this.renderer.setStyle(this.currentDragElement, 'grid-column', `${updatedPosition.x + 1} / span ${updatedPosition.cols}`);
    this.renderer.setStyle(this.currentDragElement, 'grid-row', `${updatedPosition.y + 1} / span ${updatedPosition.rows}`);
  }
  
  private startResize(event: MouseEvent, element: HTMLElement, widgetIndex: number): void {
    event.preventDefault();
    
    this.isResizing = true;
    this.currentResizeElement = element;
    this.resizedWidgetIndex = widgetIndex;
    
    // Startposition des Resize-Vorgangs
    this.dragStartX = event.clientX;
    this.dragStartY = event.clientY;
    
    // Ursprüngliche Widget-Größe speichern
    this.resizedWidgetStartSize = {
      cols: this.widgets[widgetIndex].instance.position.cols,
      rows: this.widgets[widgetIndex].instance.position.rows
    };
    
    // Styling während des Resizes
    this.renderer.addClass(element, 'resizing');
  }
  
  private onResize(event: MouseEvent): void {
    if (!this.isResizing || !this.currentResizeElement || this.resizedWidgetIndex === -1) return;
    
    // Berechne die Bewegung in Pixeln
    const deltaX = event.clientX - this.dragStartX;
    const deltaY = event.clientY - this.dragStartY;
    
    // Berechne die neue Größe (umgerechnet von Pixeln zu Grid-Zellen)
    const deltaGridCols = Math.round(deltaX / this.cellSize);
    const deltaGridRows = Math.round(deltaY / this.cellSize);
    
    // Aktualisiere die Größe des Widgets
    const newCols = Math.max(1, Math.min(this.gridCols - this.widgets[this.resizedWidgetIndex].instance.position.x, 
                                 this.resizedWidgetStartSize.cols + deltaGridCols));
    const newRows = Math.max(1, Math.min(this.gridRows - this.widgets[this.resizedWidgetIndex].instance.position.y, 
                                 this.resizedWidgetStartSize.rows + deltaGridRows));
    
    // UI aktualisieren
    this.renderer.setStyle(this.currentResizeElement, 'grid-column', 
                           `${this.widgets[this.resizedWidgetIndex].instance.position.x + 1} / span ${newCols}`);
    this.renderer.setStyle(this.currentResizeElement, 'grid-row', 
                           `${this.widgets[this.resizedWidgetIndex].instance.position.y + 1} / span ${newRows}`);
  }
  
  private stopDragAndResize(): void {
    // Drag beenden
    if (this.isDragging && this.currentDragElement && this.draggedWidgetIndex !== -1) {
      this.renderer.removeClass(this.currentDragElement, 'dragging');
      
      // Aktuelle Position aus dem Styling extrahieren
      const gridColumn = this.currentDragElement.style.gridColumn;
      const gridRow = this.currentDragElement.style.gridRow;
      
      // Grid-Position aus dem Style extrahieren (Format: "start / span width")
      const colMatch = gridColumn.match(/(\d+)\s*\/\s*span\s*(\d+)/);
      const rowMatch = gridRow.match(/(\d+)\s*\/\s*span\s*(\d+)/);
      
      if (colMatch && rowMatch) {
        const x = parseInt(colMatch[1], 10) - 1; // Grid beginnt bei 1, unser Modell bei 0
        const cols = parseInt(colMatch[2], 10);
        const y = parseInt(rowMatch[1], 10) - 1;
        const rows = parseInt(rowMatch[2], 10);
        
        // Position im Modell aktualisieren
        this.widgetService.updateWidgetPosition(this.draggedWidgetIndex, {
          x, y, cols, rows
        });
      }
    }
    
    // Resize beenden
    if (this.isResizing && this.currentResizeElement && this.resizedWidgetIndex !== -1) {
      this.renderer.removeClass(this.currentResizeElement, 'resizing');
      
      // Aktuelle Größe aus dem Styling extrahieren
      const gridColumn = this.currentResizeElement.style.gridColumn;
      const gridRow = this.currentResizeElement.style.gridRow;
      
      // Grid-Position aus dem Style extrahieren (Format: "start / span width")
      const colMatch = gridColumn.match(/(\d+)\s*\/\s*span\s*(\d+)/);
      const rowMatch = gridRow.match(/(\d+)\s*\/\s*span\s*(\d+)/);
      
      if (colMatch && rowMatch) {
        const x = parseInt(colMatch[1], 10) - 1; // Grid beginnt bei 1, unser Modell bei 0
        const cols = parseInt(colMatch[2], 10);
        const y = parseInt(rowMatch[1], 10) - 1;
        const rows = parseInt(rowMatch[2], 10);
        
        // Position im Modell aktualisieren
        this.widgetService.updateWidgetPosition(this.resizedWidgetIndex, {
          x, y, cols, rows
        });
      }
    }
    
    // Zurücksetzen der Drag & Resize Variablen
    this.isDragging = false;
    this.isResizing = false;
    this.currentDragElement = null;
    this.currentResizeElement = null;
    this.draggedWidgetIndex = -1;
    this.resizedWidgetIndex = -1;
  }

  logout(): void {
    this.authService.logout();
  }
}

