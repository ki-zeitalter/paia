// Lade die .env-Datei zur Buildzeit und setze die Werte
// KEINE SENSIBLEN DATEN HIER SPEICHERN

export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  auth: {
    issuer: 'https://accounts.google.com',
    redirectUri: 'http://localhost:4200/dashboard',
    clientId: '715010568183-ioeel2lnima1n6m6aji336mtcsd2sqjq.apps.googleusercontent.com', // Wird durch set-env.js ersetzt
    scope: 'openid profile email',
    responseType: 'token id_token',
    showDebugInformation: true,
    strictDiscoveryDocumentValidation: false,
    tokenEndpoint: 'https://oauth2.googleapis.com/token',
    oidc: true,
    requireHttps: false,
    useSilentRefresh: true,
    sessionChecksEnabled: false,
    clearHashAfterLogin: true,
    timeoutFactor: 0.75,
    useIdTokenHintForSilentRefresh: true,
    logoutUrl: 'https://accounts.google.com/logout'
  }
}; 