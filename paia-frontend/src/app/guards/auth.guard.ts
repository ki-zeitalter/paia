import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';
import { of } from 'rxjs';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  
  // Wenn die URL Hash-Fragmente enthält, die auf eine Rückkehr von der Authentifizierung hindeuten (Implicit Flow)
  if (window.location.hash && window.location.hash.includes('access_token')) {
    console.log('Auth-Callback mit Token im Hash erkannt, erlaube Navigation zum Dashboard');
    return of(true);
  }
  
  return authService.isAuthenticated$.pipe(
    take(1), // Nimm nur den ersten Wert, um Endlosschleifen zu vermeiden
    map(isAuthenticated => {
      console.log('Auth-Guard prüft Authentifizierung, Status:', isAuthenticated);
      
      if (isAuthenticated) {
        return true;
      } else {
        console.log('Nicht authentifiziert, starte Login-Prozess');
        router.navigate(['/login']);
        return false;
      }
    })
  );
};
