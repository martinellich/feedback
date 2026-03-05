# UC-04: Formular veroeffentlichen

## Beschreibung
Der Formular-Besitzer veroeffentlicht ein Entwurfsformular, sodass es oeffentlich zugaenglich wird.

## Akteur
Formular-Besitzer

## Vorbedingungen
- Benutzer ist authentifiziert
- Formular ist im Status DRAFT
- Benutzer ist Besitzer des Formulars

## Nachbedingungen
- Formularstatus ist PUBLIC
- Formular ist ueber den oeffentlichen Link erreichbar
- Anonyme Benutzer koennen Feedback abgeben

## Hauptszenario

1. Benutzer klickt "Veroeffentlichen" im Dashboard
2. System aendert Formularstatus von DRAFT auf PUBLIC
3. Dashboard wird aktualisiert

## Beteiligte Klassen
- `views/DashboardView.java`
- `service/FormService.java`
- `entity/FeedbackForm.java`
- `entity/FormStatus.java`
