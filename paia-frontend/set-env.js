// set-env.js
require('dotenv').config();
const fs = require('fs');
const path = require('path');

// Pfade zu den Umgebungsdateien
const devEnvFile = path.join(__dirname, 'src/environments/environment.development.ts');
const prodEnvFile = path.join(__dirname, 'src/environments/environment.prod.ts');
const baseEnvFile = path.join(__dirname, 'src/environments/environment.ts');

// Prüfe, ob die CLIENT_ID in der .env-Datei vorhanden ist
if (!process.env.CLIENT_ID) {
  console.error('FEHLER: CLIENT_ID ist in der .env-Datei nicht definiert!');
  process.exit(1); // Beende den Prozess mit Fehlercode
}

// Funktion zum Lesen der Datei und Ersetzen der Umgebungsvariablen
function replaceEnvVars(filePath) {
  try {
    if (fs.existsSync(filePath)) {
      let content = fs.readFileSync(filePath, 'utf8');
      const clientId = process.env.CLIENT_ID;
      
      // Ersetze alle Platzhalter mit der tatsächlichen clientId
      content = content.replace(/'__CLIENT_ID__'/g, `'${clientId}'`);
      
      // Extra Sicherheitsprüfung: Suche und entferne jede hartcodierte Client-ID im Format von Google OAuth
      content = content.replace(/['"](\d{12}-[a-zA-Z0-9]+)\.apps\.googleusercontent\.com['"]/g, `'${clientId}'`);
      
      // Schreibe die aktualisierte Datei zurück
      fs.writeFileSync(filePath, content);
      console.log(`Umgebungsvariablen in ${filePath} wurden ersetzt.`);
    }
  } catch (error) {
    console.error(`Fehler beim Verarbeiten von ${filePath}:`, error);
    process.exit(1); // Beende den Prozess mit Fehlercode
  }
}

// Ersetze die Umgebungsvariablen in allen Environment-Dateien
replaceEnvVars(devEnvFile);
replaceEnvVars(prodEnvFile);
replaceEnvVars(baseEnvFile); // Auch die Basis-Environment-Datei bearbeiten 