import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { OAuthService } from 'angular-oauth2-oidc';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const oauthService = inject(OAuthService);
  // ID-Token statt Access-Token verwenden
  const token = oauthService.getIdToken();
  
  // Prüfe, ob die Anfrage an unsere API geht
  const isApiRequest = req.url.includes('/api');
  
  if (token && isApiRequest) {
    console.log('Auth-Interceptor fügt Token hinzu', {
      url: req.url,
      tokenVorhanden: !!token,
      tokenLänge: token.length,
      tokenTyp: 'ID-Token'
    });
    
    // Validiere das Token-Format (minimale Prüfung)
    const isValidJwt = /^[A-Za-z0-9-_]+\.[A-Za-z0-9-_]+\.[A-Za-z0-9-_]*$/.test(token);
    
    if (!isValidJwt) {
      console.error('Auth-Interceptor: Token hat kein gültiges JWT-Format');
      // Hier könnten wir auch einen Refresh oder Redirect zur Login-Seite auslösen
      return next(req); // Original-Request ohne Token senden
    }
    
    const authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    
    console.log('Neue Headers:', authReq.headers.keys());
    return next(authReq);
  } else {
    if (isApiRequest) {
      console.warn('Auth-Interceptor: Kein Token verfügbar für API-Anfrage', {
        url: req.url,
        tokenVorhanden: !!token
      });
    }
  }

  return next(req);
}; 