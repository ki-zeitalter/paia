import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { authGuard } from './guards/auth.guard';
import { ConfigComponent } from './config/config.component';
import { ModelConfigComponent } from './config/model-config/model-config.component';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { 
    path: 'config', 
    component: ConfigComponent, 
    canActivate: [authGuard],
    children: [
      { path: 'models', component: ModelConfigComponent }
    ]
  },
  { path: '**', redirectTo: '/login' }
];
