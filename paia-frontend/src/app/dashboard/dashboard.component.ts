import { Component, OnInit, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GridsterModule, GridsterConfig, GridsterItem, GridType, CompactType, DisplayGrid } from 'angular-gridster2';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { Observable } from 'rxjs';
import { WidgetService } from '../services/widget.service';
import { AuthService } from '../services/auth.service';
import { WidgetComponent } from '../widget/widget.component';
import { Widget, DashboardConfiguration, WidgetInstance } from '../models/widget';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    GridsterModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    WidgetComponent
  ],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  options!: GridsterConfig;
  availableWidgets$: Observable<Widget[]>;
  dashboardConfig$: Observable<DashboardConfiguration>;
  widgets: { widget: Widget, instance: WidgetInstance }[] = [];
  userInfo: any;

  constructor(
    private widgetService: WidgetService,
    private authService: AuthService
  ) {
    this.availableWidgets$ = this.widgetService.availableWidgets$;
    this.dashboardConfig$ = this.widgetService.dashboardConfiguration$;
    this.userInfo = this.authService.identityClaims;
  }

  ngOnInit(): void {
    this.initGridsterOptions();
    this.loadDashboard();
  }

  private initGridsterOptions(): void {
    this.options = {
      gridType: GridType.Fit,
      compactType: CompactType.None,
      margin: 10,
      outerMargin: true,
      outerMarginTop: null,
      outerMarginRight: null,
      outerMarginBottom: null,
      outerMarginLeft: null,
      mobileBreakpoint: 640,
      minCols: 12,
      maxCols: 12,
      minRows: 10,
      maxRows: 100,
      maxItemCols: 12,
      minItemCols: 1,
      maxItemRows: 100,
      minItemRows: 1,
      maxItemArea: 2500,
      minItemArea: 1,
      defaultItemCols: 3,
      defaultItemRows: 2,
      fixedColWidth: 105,
      fixedRowHeight: 105,
      keepFixedHeightInMobile: false,
      keepFixedWidthInMobile: false,
      scrollSensitivity: 10,
      scrollSpeed: 20,
      enableEmptyCellClick: false,
      enableEmptyCellContextMenu: false,
      enableEmptyCellDrop: false,
      enableEmptyCellDrag: false,
      emptyCellDragMaxCols: 50,
      emptyCellDragMaxRows: 50,
      ignoreMarginInRow: false,
      draggable: {
        enabled: true,
      },
      resizable: {
        enabled: true,
      },
      swap: false,
      pushItems: true,
      disablePushOnDrag: false,
      disablePushOnResize: false,
      pushDirections: { north: true, east: true, south: true, west: true },
      pushResizeItems: false,
      displayGrid: DisplayGrid.OnDragAndResize,
      disableWindowResize: false,
      disableWarnings: false,
      scrollToNewItems: false,
      itemChangeCallback: this.onItemChange.bind(this),
    };
  }

  private loadDashboard(): void {
    this.widgetService.loadAvailableWidgets().subscribe();
    this.widgetService.loadDashboardConfiguration().subscribe(config => {
      this.updateWidgets(config);
    });
  }

  private updateWidgets(config: DashboardConfiguration): void {
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
  }

  addWidget(widget: Widget): void {
    this.widgetService.addWidget(widget.id);
  }

  removeWidget(index: number): void {
    this.widgetService.removeWidget(index);
  }

  onItemChange(item: GridsterItem, itemComponent: any): void {
    // Find the index of the widget in the array
    const index = this.widgets.findIndex(w => 
      w.instance.position.x === item.x && 
      w.instance.position.y === item.y
    );

    if (index !== -1) {
      this.widgetService.updateWidgetPosition(index, {
        x: item.x as number,
        y: item.y as number,
        cols: item.cols as number,
        rows: item.rows as number
      });
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
