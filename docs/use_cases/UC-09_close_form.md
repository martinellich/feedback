# UC-09: Formular schliessen

## Beschreibung
Der Formular-Besitzer schliesst ein oeffentliches Formular, sodass keine weiteren Feedbacks abgegeben werden koennen.

## Akteur
Formular-Besitzer

## Vorbedingungen
- Benutzer ist authentifiziert
- Formular ist im Status PUBLIC
- Benutzer ist Besitzer des Formulars

## Nachbedingungen
- Formularstatus ist CLOSED
- Keine neuen Feedbacks moeglich
- Bestehende Ergebnisse bleiben erhalten

## Hauptszenario

1. Benutzer klickt "Schliessen" im Dashboard
2. System aendert Formularstatus von PUBLIC auf CLOSED
3. Dashboard wird aktualisiert

## Beteiligte Klassen
- `views/DashboardView.java`
- `service/FormService.java`
- `entity/FeedbackForm.java`
- `entity/FormStatus.java`
