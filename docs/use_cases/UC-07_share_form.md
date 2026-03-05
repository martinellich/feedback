# UC-07: Formular teilen

## Beschreibung
Der Formular-Besitzer gibt ein Formular fuer andere Benutzer frei.

## Akteur
Formular-Besitzer

## Vorbedingungen
- Benutzer ist authentifiziert
- Benutzer ist Besitzer des Formulars

## Nachbedingungen
- Geteilter Benutzer sieht das Formular in seinem Dashboard
- Geteilter Benutzer kann Ergebnisse und QR-Code einsehen

## Hauptszenario

1. Benutzer klickt "Teilen" im Dashboard
2. System zeigt Dialog mit aktuellen Freigaben
3. Benutzer gibt E-Mail-Adresse ein
4. Benutzer klickt "Hinzufuegen"
5. System validiert E-Mail und erstellt FormShare
6. Freigabeliste wird aktualisiert

## Erweiterung: Freigabe entfernen

1. Benutzer klickt "Entfernen" neben einer Freigabe
2. System loescht FormShare
3. Freigabeliste wird aktualisiert

## Alternativszenarien

**4a. Ungueltige E-Mail**
1. System zeigt Fehlermeldung

**4b. E-Mail ist Formular-Besitzer**
1. System zeigt Fehlermeldung (kann nicht mit sich selbst teilen)

**4c. E-Mail bereits geteilt**
1. System verhindert Duplikat

## Geschaeftsregeln
- Nur der Besitzer kann teilen
- Geteilte Benutzer koennen nicht weiter teilen
- E-Mail-Format wird validiert
- Keine doppelten Freigaben moeglich
- Eindeutige Einschraenkung: (form_id, shared_with_email)

## Beteiligte Klassen
- `views/DashboardView.java`
- `service/FormService.java`
- `entity/FormShare.java`
