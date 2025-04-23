import { Injectable } from '@angular/core';
import { OAuthService, AuthConfig, OAuthEvent, OAuthInfoEvent } from 'angular-oauth2-oidc';
import { environment } from '../../environments/environment';
import { Observable, BehaviorSubject, throwError, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, switchMap, tap } from 'rxjs/operators';

export const authConfig: AuthConfig = environment.auth;

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
}

export interface AuthResponse {
  token: string;
  id: string;
  email: string;
  name: string;
  roles: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private loginInProgress = false;
  private initialized = false;
  private authToken = new BehaviorSubject<string | null>(null);
  private apiUrl = `${environment.apiUrl}`;

  constructor(
    private oauthService: OAuthService,
    private http: HttpClient
  ) {
    this.configureOAuth();
    this.checkLocalToken();
  }

  private checkLocalToken() {
    const token = localStorage.getItem('auth_token');
    if (token) {
      // Prüfe, ob das Token gültig ist (minimale JWT-Validierung)
      const isValidJwt = /^[A-Za-z0-9-_]+\.[A-Za-z0-9-_]+\.[A-Za-z0-9-_]*$/.test(token);
      
      if (isValidJwt) {
        console.log('Gültiges Token im localStorage gefunden');
        this.authToken.next(token);
        this.isAuthenticatedSubject.next(true);
      } else {
        console.warn('Ungültiges Token im localStorage gefunden, wird entfernt');
        localStorage.removeItem('auth_token');
        localStorage.removeItem('user_id');
        localStorage.removeItem('user_email');
        localStorage.removeItem('user_name');
      }
    }
  }

  private navigateTo(path: string) {
    // Verhindere Endlosschleifen bei Weiterleitungen
    if (path === 'login' && window.location.pathname === '/login') {
      console.log('Bereits auf der Login-Seite, keine erneute Weiterleitung');
      return;
    }
    
    // Verwende die HTML5 History API statt des Angular Routers
    window.location.href = path.startsWith('http') ? path : `/${path}`;
  }

  private configureOAuth() {
    // Überprüfe lokales Token vor der OAuth-Konfiguration
    this.checkLocalToken();
    
    this.oauthService.events.subscribe(event => {
      if (event.type === 'token_received' || event.type === 'token_refreshed') {
        console.log('Token-Ereignis empfangen:', event.type);
        this.handleGoogleAuthentication();
      }
    });
    
    this.oauthService.configure(authConfig);
    console.log('OAuth Konfiguration gestartet');
    console.log('Redirect URI:', authConfig.redirectUri);
    
    // Prüfe zuerst, ob ein gültiger lokaler Token existiert
    const hasValidLocalToken = !!localStorage.getItem('auth_token') && this.isAuthenticatedSubject.value;
    
    // Wenn wir bereits ein gültiges lokales Token haben, können wir den Rest überspringen
    if (hasValidLocalToken) {
      console.log('Bereits authentifiziert mit lokalem Token, überspringe OAuth-Login');
      this.initialized = true;
      return;
    }
    
    this.oauthService.loadDiscoveryDocument()
      .then(() => {
        this.oauthService.setupAutomaticSilentRefresh();
        
        return this.oauthService.tryLogin();
      })
      .then(loginResult => {
        console.log('Login-Ergebnis:', loginResult);
        this.initialized = true;
        
        if (this.oauthService.hasValidAccessToken()) {
          this.handleGoogleAuthentication();
        } else if (!window.location.hash.includes('access_token') && window.location.pathname !== '/login') {
          // Wenn nicht authentifiziert und nicht auf der Login-Seite, leite zur Login-Seite weiter
          this.navigateTo('login');
        }
      })
      .catch(error => {
        console.error('Fehler beim OAuth-Setup:', error);
        this.initialized = true;
        
        // Wenn wir einen gültigen lokalen Token haben, sind wir trotz OAuth-Fehler authentifiziert
        if (hasValidLocalToken) {
          console.log('OAuth-Fehler, aber gültiger lokaler Token vorhanden');
          this.isAuthenticatedSubject.next(true);
        } else {
          this.isAuthenticatedSubject.next(false);
          // Nur zur Login-Seite weiterleiten, wenn wir nicht bereits dort sind
          if (window.location.pathname !== '/login') {
            this.navigateTo('login');
          }
        }
      });
  }

  private handleGoogleAuthentication() {
    const idToken = this.oauthService.getIdToken();
    if (idToken) {
      this.authenticateWithBackend({ idToken })
        .subscribe({
          next: (response) => {
            this.storeAuthData(response);
            this.isAuthenticatedSubject.next(true);
            this.navigateTo('dashboard');
          },
          error: (error) => {
            console.error('Fehler bei Google-Authentifizierung mit Backend:', error);
            this.isAuthenticatedSubject.next(false);
            this.navigateTo('login');
          }
        });
    }
  }

  private authenticateWithBackend(request: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/google`, request);
  }

  private storeAuthData(response: AuthResponse) {
    console.log('Speichere Auth-Daten:', {
      tokenVorhanden: !!response.token,
      tokenLänge: response.token?.length || 0,
      id: response.id,
      email: response.email
    });
    
    // Prüfe, ob es sich um eine Fehlermeldung handelt
    if (!response.token || response.token.startsWith('ERROR:')) {
      console.error('Auth-Fehler:', response.token || 'Kein Token erhalten');
      // Token nicht speichern, wenn es sich um eine Fehlermeldung handelt
      return;
    }
    
    // Validiere das Token-Format (minimale JWT-Validierung)
    const isValidJwt = /^[A-Za-z0-9-_]+\.[A-Za-z0-9-_]+\.[A-Za-z0-9-_]*$/.test(response.token);
    if (!isValidJwt) {
      console.error('Ungültiges Token-Format erhalten:', response.token.substring(0, 20) + '...');
      return;
    }
    
    // Speichere die Auth-Daten im localStorage
    localStorage.setItem('auth_token', response.token);
    localStorage.setItem('user_id', response.id);
    localStorage.setItem('user_email', response.email);
    localStorage.setItem('user_name', response.name);
    
    // Aktualisiere den internen Zustand
    this.authToken.next(response.token);
    this.isAuthenticatedSubject.next(true);
    
    console.log('Auth-Daten erfolgreich gespeichert');
  }

  public get token(): string | null {
    return this.authToken.value;
  }

  public loginWithGoogle() {
    if (this.loginInProgress || !this.initialized) {
      console.log('Login wird übersprungen: Login läuft bereits oder Service nicht initialisiert');
      return;
    }
    
    if (this.oauthService.hasValidAccessToken()) {
      console.log('Bereits authentifiziert, aktualisiere Status');
      this.handleGoogleAuthentication();
      return;
    }
    
    console.log('Starte Login-Flow (Implicit Flow)');
    this.loginInProgress = true;
    
    try {
      this.oauthService.initImplicitFlow();
    } catch (error) {
      console.error('Fehler beim Starten des Login-Flows:', error);
      this.loginInProgress = false;
    }
  }

  public register(request: RegisterRequest): Observable<AuthResponse> {
    console.log('Sende Registrierungsanfrage:', { email: request.email, name: request.name });
    
    // Füge Felder hinzu, falls sie fehlen
    const fullRequest = {
      ...request,
      email: request.email || '',
      password: request.password || '',
      name: request.name || ''
    };
    
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, fullRequest)
      .pipe(
        tap(response => {
          console.log('Registrierungsantwort erhalten:', {
            success: !response.token?.startsWith('ERROR:'),
            token: response.token ? (response.token.length > 20 ? response.token.substring(0, 20) + '...' : response.token) : 'kein Token'
          });

          if (response.token?.startsWith('ERROR:')) {
            throw new Error(response.token.substring(6)); // ERROR: entfernen
          }
          
          this.storeAuthData(response);
          
          // Kurze Verzögerung für die Weiterleitung
          console.log('Weiterleitung zum Dashboard nach erfolgreicher Registrierung...');
          setTimeout(() => {
            // Direkt zum Dashboard navigieren, ohne Reinitialisierung des AuthService
            window.location.replace('/dashboard');
          }, 100);
        })
      );
  }

  public login(request: LoginRequest): Observable<AuthResponse> {
    console.log('Sende Login-Anfrage:', { email: request.email });
    
    // Füge Felder hinzu, falls sie fehlen
    const fullRequest = {
      ...request,
      email: request.email || '',
      password: request.password || ''
    };
    
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, fullRequest)
      .pipe(
        tap(response => {
          console.log('Login-Antwort erhalten:', {
            success: !response.token?.startsWith('ERROR:'),
            token: response.token ? (response.token.length > 20 ? response.token.substring(0, 20) + '...' : response.token) : 'kein Token'
          });

          if (response.token?.startsWith('ERROR:')) {
            throw new Error(response.token.substring(6)); // ERROR: entfernen
          }
          
          this.storeAuthData(response);
          
          // Kurze Verzögerung für die Weiterleitung
          console.log('Weiterleitung zum Dashboard nach erfolgreichem Login...');
          setTimeout(() => {
            // Direkt zum Dashboard navigieren, ohne Reinitialisierung des AuthService
            window.location.replace('/dashboard');
          }, 100);
        })
      );
  }

  public logout() {
    console.log('Logout durchgeführt');
    
    // Lösche lokale Auth-Daten
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_id');
    localStorage.removeItem('user_email');
    localStorage.removeItem('user_name');
    this.authToken.next(null);
    
    // Logout von Google OAuth
    this.oauthService.logOut();
    
    this.isAuthenticatedSubject.next(false);
    this.loginInProgress = false;
    this.navigateTo('login');
  }

  public get identityClaims(): any {
    // Versuche zuerst, die Claims aus dem OAuth-Service zu bekommen
    const oauthClaims = this.oauthService.getIdentityClaims();
    if (oauthClaims) {
      return oauthClaims;
    }
    
    // Wenn keine OAuth-Claims vorhanden sind, erstelle Claims aus den lokalen Benutzerdaten
    const userId = localStorage.getItem('user_id');
    const email = localStorage.getItem('user_email');
    const name = localStorage.getItem('user_name');
    
    if (email) {
      return {
        sub: userId,
        email: email,
        name: name || email.split('@')[0],
        local_user: true
      };
    }
    
    return null;
  }

  public updateAuthStatus(isAuthenticated: boolean) {
    console.log('Aktualisiere Auth-Status:', isAuthenticated);
    this.isAuthenticatedSubject.next(isAuthenticated);
    
    if (isAuthenticated) {
      // Stelle sicher, dass das Token im BehaviorSubject ist
      const token = localStorage.getItem('auth_token');
      if (token && this.authToken.value !== token) {
        this.authToken.next(token);
      }
    }
  }
}
