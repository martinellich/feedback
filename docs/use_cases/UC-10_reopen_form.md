# UC-10: Formular wieder oeffnen

## Beschreibung
Der Formular-Besitzer oeffnet ein geschlossenes Formular wieder, sodass erneut Feedbacks abgegeben werden koennen.

## Akteur
Formular-Besitzer

## Vorbedingungen
- Benutzer ist authentifiziert
- Formular ist im Status CLOSED
- Benutzer ist Besitzer des Formulars

## Nachbedingungen
- Formularstatus ist PUBLIC
- Anonyme Benutzer koennen wieder Feedback abgeben

## Hauptszenario

1. Benutzer klickt "Wieder oeffnen" im Dashboard
2. System aendert Formularstatus von CLOSED auf PUBLIC
3. Dashboard wird aktualisiert

## Beteiligte Klassen
- `views/DashboardView.java`
- `service/FormService.java`
- `entity/FeedbackForm.java`
- `entity/FormStatus.java`
