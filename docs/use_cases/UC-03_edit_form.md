# UC-03: Formular bearbeiten

## Beschreibung
Der Formular-Besitzer bearbeitet die Details und Fragen eines Entwurfsformulars.

## Akteur
Formular-Besitzer

## Vorbedingungen
- Benutzer ist authentifiziert
- Formular ist im Status DRAFT
- Benutzer ist Besitzer des Formulars

## Nachbedingungen
- Formulardetails und/oder Fragen aktualisiert

## Hauptszenario

1. Benutzer klickt "Bearbeiten" im Dashboard
2. System prueft Zugriffsberechtigung
3. System zeigt FormEditorView mit Formulardetails und Fragenliste
4. Benutzer bearbeitet Titel, Speaker, Datum und/oder Ort
5. Benutzer klickt "Speichern"
6. System speichert Aenderungen und zeigt Erfolgsmeldung

## Erweiterung: Frage hinzufuegen

1. Benutzer gibt Fragentext ein
2. Benutzer waehlt Fragetyp (RATING oder TEXT)
3. Benutzer klickt "Hinzufuegen"
4. System ordnet naechsten orderIndex zu
5. Frage erscheint in der Fragenliste

## Alternativszenarien

**2a. Kein Zugriff**
1. System leitet zum Dashboard weiter

## Beteiligte Klassen
- `views/FormEditorView.java`
- `service/FormService.java`
- `entity/FeedbackForm.java`
- `entity/FeedbackQuestion.java`
