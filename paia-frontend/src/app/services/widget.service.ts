import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ApiService } from './api.service';
import { Widget, DashboardConfiguration, WidgetInstance } from '../models/widget';
import { map, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class WidgetService {
  private availableWidgetsSubject = new BehaviorSubject<Widget[]>([]);
  public availableWidgets$ = this.availableWidgetsSubject.asObservable();

  private dashboardConfigurationSubject = new BehaviorSubject<DashboardConfiguration>({ widgets: [] });
  public dashboardConfiguration$ = this.dashboardConfigurationSubject.asObservable();

  constructor(private apiService: ApiService) { }

  loadAvailableWidgets(): Observable<Widget[]> {
    return this.apiService.getAvailableWidgets().pipe(
      tap(widgets => this.availableWidgetsSubject.next(widgets))
    );
  }

  loadDashboardConfiguration(): Observable<DashboardConfiguration> {
    return this.apiService.getDashboardConfiguration().pipe(
      map(config => {
        if (!config) {
          config = { widgets: [] };
        }
        if (!config.widgets) {
          config.widgets = [];
        }
        return config;
      }),
      tap(config => this.dashboardConfigurationSubject.next(config))
    );
  }

  saveDashboardConfiguration(): Observable<void> {
    const config = this.dashboardConfigurationSubject.getValue();
    return this.apiService.saveDashboardConfiguration(config);
  }

  addWidget(widgetId: string): void {
    const widget = this.availableWidgetsSubject.getValue().find(w => w.id === widgetId);
    if (!widget) return;

    const config = this.dashboardConfigurationSubject.getValue();
    const widgetInstance: WidgetInstance = {
      widgetId: widget.id,
      position: {
        x: widget.x || 0,
        y: widget.y || 0,
        cols: widget.cols || 3,
        rows: widget.rows || 2
      }
    };

    config.widgets.push(widgetInstance);
    this.dashboardConfigurationSubject.next({ ...config });
    this.saveDashboardConfiguration().subscribe();
  }

  removeWidget(widgetIndex: number): void {
    const config = this.dashboardConfigurationSubject.getValue();
    config.widgets.splice(widgetIndex, 1);
    this.dashboardConfigurationSubject.next({ ...config });
    this.saveDashboardConfiguration().subscribe();
  }

  updateWidgetPosition(index: number, position: { x: number, y: number, cols: number, rows: number }): void {
    const config = this.dashboardConfigurationSubject.getValue();
    if (index >= 0 && index < config.widgets.length) {
      config.widgets[index].position = position;
      this.dashboardConfigurationSubject.next({ ...config });
      this.saveDashboardConfiguration().subscribe();
    }
  }
}
