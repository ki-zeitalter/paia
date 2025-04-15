import { bootstrapApplication } from '@angular/platform-browser';
import { AppComponent } from './app/app.component';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AuthInterceptor } from './app/interceptors/auth.interceptor';
import { provideRouter } from '@angular/router';
import { routes } from './app/app.routes';
import { importProvidersFrom } from '@angular/core';
import { OAuthModule, OAuthService } from 'angular-oauth2-oidc';
import { provideAnimations } from '@angular/platform-browser/animations';

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withInterceptors([AuthInterceptor])),
    provideRouter(routes),
    provideAnimations(),
    importProvidersFrom(
      OAuthModule.forRoot({
        resourceServer: {
          allowedUrls: ['http://localhost:8080/api'],
          sendAccessToken: true
        }
      })
    ),
    OAuthService
  ]
}).catch(err => console.error(err));
