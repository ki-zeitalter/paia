import {Injectable} from '@angular/core';
import {BehaviorSubject, Observable, of} from 'rxjs';
import {ApiService} from './api.service';
import {AvailableWidget, DashboardConfiguration, Widget, WidgetInstance} from '../models/widget';
import {map, tap, catchError} from 'rxjs/operators';

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
      map(this.mapAvailableWidgetsToWidgets),
      tap(widgets => this.availableWidgetsSubject.next(widgets)),
      tap(widgets => console.log('Verfügbare Widgets geladen:', widgets.length)),
      catchError(error => {
        console.error('Fehler beim Laden der verfügbaren Widgets:', error);
        // Bei Fehler behalten wir die bisherigen Widgets bei
        return of(this.availableWidgetsSubject.getValue());
      })
    );
  }

  private mapAvailableWidgetsToWidgets(availableWidgets: AvailableWidget[]): Widget[] {
    return availableWidgets.map(available => ({
      id: available.widgetId,
      name: available.name,
      description: available.description,
      configParameters: available.configParameters
    }));
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
      tap(config => {
        console.log('Dashboard-Konfiguration geladen:', config.widgets.length, 'Widgets');
        this.dashboardConfigurationSubject.next(config);
      }),
      catchError(error => {
        console.error('Fehler beim Laden der Dashboard-Konfiguration:', error);
        // Bei Fehler leere Konfiguration zurückgeben oder die bisherige beibehalten
        const currentConfig = this.dashboardConfigurationSubject.getValue();
        if (currentConfig.widgets.length > 0) {
          return of(currentConfig);
        }
        return of({ widgets: [] });
      })
    );
  }

  saveDashboardConfiguration(): Observable<void> {
    const config = this.dashboardConfigurationSubject.getValue();
    return this.apiService.saveDashboardConfiguration(config);
  }

  addWidget(widgetId: string): void {
    console.log('addWidget', widgetId);
    const widget = this.availableWidgetsSubject.getValue().find(w => w.id === widgetId);
    if (!widget){
      console.error('Widget nicht gefunden');
      return;
    }

    const config = this.dashboardConfigurationSubject.getValue();
    console.log('current config', config);
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

  updateDashboardConfig(config: DashboardConfiguration): void {
    this.dashboardConfigurationSubject.next(config);
  }

  updateWidgetConfig(widgetIndex: number, config: {[key: string]: any}): void {
    const dashboardConfig = this.dashboardConfigurationSubject.getValue();
    
    if (widgetIndex >= 0 && widgetIndex < dashboardConfig.widgets.length) {
      dashboardConfig.widgets[widgetIndex].config = config;
      
      this.dashboardConfigurationSubject.next({ ...dashboardConfig });
      
      this.saveDashboardConfiguration().subscribe();
    }
  }
}
