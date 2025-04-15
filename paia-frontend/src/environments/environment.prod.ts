// Lade die .env-Datei zur Buildzeit und setze die Werte
// KEINE SENSIBLEN DATEN HIER SPEICHERN

export const environment = {
  production: true,
  apiUrl: 'https://api.paia.com/api', // Angepasste URL für Produktion
  auth: {
    issuer: 'https://accounts.google.com',
    redirectUri: 'https://paia.com/dashboard', // Angepasste URL für Produktion
    clientId: '715010568183-ioeel2lnima1n6m6aji336mtcsd2sqjq.apps.googleusercontent.com', // Wird durch set-env.js ersetzt
    scope: 'openid profile email',
    responseType: 'token id_token',
    showDebugInformation: false, // In Produktion auf false gesetzt
    strictDiscoveryDocumentValidation: false,
    tokenEndpoint: 'https://oauth2.googleapis.com/token',
    oidc: true,
    requireHttps: true, // In Produktion auf true gesetzt
    useSilentRefresh: true,
    sessionChecksEnabled: false,
    clearHashAfterLogin: true,
    timeoutFactor: 0.75,
    useIdTokenHintForSilentRefresh: true,
    logoutUrl: 'https://accounts.google.com/logout'
  }
}; 