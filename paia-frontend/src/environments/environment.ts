// Diese Datei wird nie direkt verwendet - sie dient nur als Vorlage
// Die tatsächlichen Umgebungsdateien werden je nach Build-Konfiguration verwendet (development/production)
export const environment = {
  production: false,
  apiUrl: 'http://localhost:8080/api',
  auth: {
    issuer: 'https://accounts.google.com',
    redirectUri: 'http://localhost:4200/dashboard',
    clientId: '715010568183-ioeel2lnima1n6m6aji336mtcsd2sqjq.apps.googleusercontent.com', // Platzhalter, der durch set-env.js ersetzt wird
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