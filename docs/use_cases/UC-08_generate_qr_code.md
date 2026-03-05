# UC-08: QR-Code generieren

## Beschreibung
Ein Benutzer generiert einen QR-Code fuer den oeffentlichen Formular-Link.

## Akteur
Formular-Besitzer, Geteilter Benutzer

## Vorbedingungen
- Benutzer ist authentifiziert
- Benutzer hat Zugriff auf das Formular

## Nachbedingungen
- Keine Aenderungen am System

## Hauptszenario

1. Benutzer klickt "QR-Code" im Dashboard
2. System generiert QR-Code als PNG-Bild fuer die Formular-URL
3. System zeigt Dialog mit QR-Code-Bild und URL
4. Benutzer kann URL in die Zwischenablage kopieren

## Details
- URL-Format: `{scheme}://{server}:{port}/form/{publicToken}`
- QR-Code wird als PNG-Bild generiert (ZXing-Bibliothek)
- Verfuegbar fuer alle Formularstatus (DRAFT, PUBLIC, CLOSED)

## Beteiligte Klassen
- `views/DashboardView.java`
- `service/QrCodeService.java`
