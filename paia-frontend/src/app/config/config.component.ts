import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

interface ConfigCategory {
  id: string;
  name: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-config',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule
  ],
  templateUrl: './config.component.html',
  styleUrls: ['./config.component.scss']
})
export class ConfigComponent {
  categories: ConfigCategory[] = [
    {
      id: 'models',
      name: 'Modelle',
      icon: 'smart_toy',
      route: '/config/models'
    }
  ];

  constructor(private router: Router) {}

  isRouteActive(route: string): boolean {
    return this.router.url === route;
  }

  isChildRouteActive(): boolean {
    // Überprüft, ob die aktuelle Route eine Unterroute von '/config' ist
    // aber nicht genau '/config' selbst
    return this.router.url.startsWith('/config/');
  }
} 