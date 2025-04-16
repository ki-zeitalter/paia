import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { WidgetRegistryService } from './services/widget-registry.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  constructor(
    private widgetRegistryService: WidgetRegistryService
  ) {}

  title = 'PAIA - Personal AI Assistant';
  currentYear = new Date().getFullYear();
}
