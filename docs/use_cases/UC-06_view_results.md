# UC-06: Ergebnisse anzeigen

## Beschreibung
Der Formular-Besitzer oder ein geteilter Benutzer betrachtet die Feedback-Ergebnisse.

## Akteur
Formular-Besitzer, Geteilter Benutzer

## Vorbedingungen
- Benutzer ist authentifiziert
- Benutzer hat Zugriff auf das Formular (Besitzer oder geteilt)

## Nachbedingungen
- Keine Aenderungen am System

## Hauptszenario

1. Benutzer klickt "Ergebnisse" im Dashboard
2. System prueft Zugriffsberechtigung
3. System zeigt Formulartitel, Speaker und Anzahl Antworten
4. Fuer jede RATING-Frage:
   - Fragentext mit Nummer
   - Durchschnittliche Bewertung (2 Dezimalstellen) oder "N/A"
5. Fuer jede TEXT-Frage:
   - Fragentext mit Nummer
   - Liste aller nicht-leeren Textantworten als Aufzaehlung

## Alternativszenarien

**2a. Kein Zugriff**
1. System leitet zum Dashboard weiter

**3a. Keine Antworten vorhanden**
1. System zeigt "Noch keine Antworten" Meldung

## Geschaeftsregeln
- Ergebnisse sind unabhaengig vom Formularstatus einsehbar
- Durchschnittsberechnung nur ueber nicht-null Bewertungen
- Leere Textantworten werden nicht angezeigt

## Beteiligte Klassen
- `views/ResultsView.java`
- `service/FormService.java`
- `entity/FeedbackAnswer.java`
