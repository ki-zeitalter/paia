import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const oauthService = inject(OAuthService);
  
  // Bei Auth-Endpunkten kein Token hinzufügen
  if (req.url.includes('/auth/login') || req.url.includes('/auth/register') || req.url.includes('/auth/google')) {
    console.log('Auth-Endpoint-Anfrage, kein Token nötig:', req.url);
    return next(req);
  }
  
  // Token direkt aus dem localStorage holen, statt den AuthService zu injizieren
  const localToken = localStorage.getItem('auth_token');
  const token = localToken || oauthService.getIdToken();
  
  // Prüfe, ob die Anfrage an unsere API geht
  const isApiRequest = req.url.includes('/api');
  
  if (token && isApiRequest) {
    console.log('Auth-Interceptor fügt Token hinzu', {
      url: req.url,
      tokenVorhanden: !!token,
      tokenLänge: token.length,
      tokenTyp: localToken ? 'JWT-Token (lokal)' : 'ID-Token (Google)'
    });
    
    // Validiere das Token-Format (minimale Prüfung)
    const isValidJwt = /^[A-Za-z0-9-_]+\.[A-Za-z0-9-_]+\.[A-Za-z0-9-_]*$/.test(token);
    
    if (!isValidJwt) {
      console.error('Auth-Interceptor: Token hat kein gültiges JWT-Format');
      // Hier könnten wir auch einen Refresh oder Redirect zur Login-Seite auslösen
      return next(req); // Original-Request ohne Token senden
    }
    
    // Stelle sicher, dass das Token nicht "Bearer" bereits enthält
    const tokenValue = token.startsWith('Bearer ') ? token : `Bearer ${token}`;
    
    const authReq = req.clone({
      setHeaders: {
        Authorization: tokenValue
      }
    });
    
    console.log('Request mit Authorization-Header:', {
      url: authReq.url,
      headers: authReq.headers.keys()
    });
    return next(authReq);
  } else {
    if (isApiRequest && !req.url.includes('/auth/')) {
      // Nur warnen, wenn es keine Auth-Anfrage ist
      console.warn('Auth-Interceptor: Kein Token verfügbar für API-Anfrage', {
        url: req.url,
        tokenVorhanden: !!token
      });
    }
  }

  return next(req);
}; 