export interface Widget {
  id: string;
  name: string;
  description?: string;
  x?: number;
  y?: number;
  cols?: number;
  rows?: number;
  component?: string;
}

export interface AvailableWidget {
  widgetId: string;
  name: string;
  description?: string;
}

export interface WidgetPosition {
  x: number;
  y: number;
  cols: number;
  rows: number;
}

export interface DashboardConfiguration {
  widgets: WidgetInstance[];
}

export interface WidgetInstance {
  widgetId: string;
  position: WidgetPosition;
}
