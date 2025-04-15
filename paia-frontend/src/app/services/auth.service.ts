import { Injectable } from '@angular/core';
import { OAuthService, AuthConfig, OAuthEvent, OAuthInfoEvent } from 'angular-oauth2-oidc';
import { environment } from '../../environments/environment';
import { Observable, BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';

export const authConfig: AuthConfig = environment.auth;

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private loginInProgress = false;
  private initialized = false;

  constructor(
    private oauthService: OAuthService,
    private router: Router
  ) {
    this.configureOAuth();
  }

  private configureOAuth() {
    this.oauthService.events.subscribe(event => {
      if (event.type === 'token_received' || event.type === 'token_refreshed') {
        console.log('Token-Ereignis empfangen:', event.type);
        this.debugTokenInfo();
        this.isAuthenticatedSubject.next(this.oauthService.hasValidAccessToken());
      }
    });
    
    this.oauthService.configure(authConfig);
    console.log('OAuth Konfiguration gestartet');
    console.log('Redirect URI:', authConfig.redirectUri);
    
    this.oauthService.loadDiscoveryDocument().then(() => {
      this.oauthService.setupAutomaticSilentRefresh();
      
      return this.oauthService.tryLogin();
    }).then(loginResult => {
      console.log('Login-Ergebnis:', loginResult);
      this.isAuthenticatedSubject.next(this.oauthService.hasValidAccessToken());
      this.initialized = true;
      
      this.debugTokenInfo();
      
      if (this.oauthService.hasValidAccessToken()) {
        console.log('Gültiges Token gefunden, leite zum Dashboard weiter');
        this.router.navigateByUrl('/dashboard');
      } else if (!window.location.hash.includes('access_token')) {
        console.log('Kein gültiges Token gefunden, leite zur Login-Seite weiter');
        this.router.navigateByUrl('/login');
      }
    }).catch(error => {
      console.error('Fehler beim OAuth-Setup:', error);
      this.initialized = true;
      this.isAuthenticatedSubject.next(false);
      this.router.navigateByUrl('/login');
    });
  }

  private debugTokenInfo() {
    const idToken = this.oauthService.getIdToken();
    const accessToken = this.oauthService.getAccessToken();
    const claims = this.oauthService.getIdentityClaims();
    
    console.log('Token-Debug-Informationen:');
    console.log('ID-Token vorhanden:', !!idToken);
    console.log('Access-Token vorhanden:', !!accessToken);
    
    if (accessToken) {
      console.log('Access-Token (gekürzt):', accessToken.substr(0, 20) + '...');
    }
    
    if (idToken) {
      console.log('ID-Token (gekürzt):', idToken.substr(0, 20) + '...');
      try {
        const tokenParts = idToken.split('.');
        if (tokenParts.length === 3) {
          const tokenPayload = JSON.parse(atob(tokenParts[1]));
          console.log('ID-Token Payload:', {
            sub: tokenPayload.sub,
            iss: tokenPayload.iss,
            exp: new Date(tokenPayload.exp * 1000).toISOString(),
            iat: new Date(tokenPayload.iat * 1000).toISOString()
          });
        }
      } catch (error) {
        console.error('Fehler beim Dekodieren des ID-Tokens:', error);
      }
    }
    
    console.log('Identity Claims:', claims);
  }

  private handleAuthentication() {
    const hasValidToken = this.oauthService.hasValidAccessToken();
    console.log('Token-Status:', {
      hasValidToken,
      accessToken: this.oauthService.getAccessToken(),
      idToken: this.oauthService.getIdToken(),
      claims: this.oauthService.getIdentityClaims()
    });
    
    this.isAuthenticatedSubject.next(hasValidToken);
    this.initialized = true;
    
    if (hasValidToken) {
      console.log('Erfolgreich authentifiziert, leite zur Dashboard-Seite weiter');
      this.oauthService.setupAutomaticSilentRefresh();
      this.router.navigateByUrl('/dashboard');
    } else {
      console.log('Nicht authentifiziert, leite zur Login-Seite weiter');
      this.router.navigateByUrl('/login');
    }
  }

  private handleAuthenticationError() {
    this.initialized = true;
    this.loginInProgress = false;
    this.isAuthenticatedSubject.next(false);
    this.router.navigateByUrl('/login');
  }

  public login() {
    if (this.loginInProgress || !this.initialized) {
      console.log('Login wird übersprungen: Login läuft bereits oder Service nicht initialisiert');
      return;
    }
    
    if (this.oauthService.hasValidAccessToken()) {
      console.log('Bereits authentifiziert, aktualisiere Status');
      this.isAuthenticatedSubject.next(true);
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

  public logout() {
    console.log('Logout durchgeführt');
    this.oauthService.logOut();
    this.isAuthenticatedSubject.next(false);
    this.loginInProgress = false;
    this.router.navigateByUrl('/login');
  }

  public get accessToken(): string {
    return this.oauthService.getAccessToken();
  }

  public get identityClaims(): any {
    return this.oauthService.getIdentityClaims();
  }
}
