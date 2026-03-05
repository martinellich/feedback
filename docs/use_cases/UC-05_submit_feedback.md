# UC-05: Feedback abgeben

## Beschreibung
Ein anonymer Benutzer gibt Feedback ueber ein oeffentliches Formular ab.

## Akteur
Anonymer Benutzer

## Vorbedingungen
- Benutzer hat den Formular-Link oder QR-Code
- Formular existiert und ist im Status PUBLIC

## Nachbedingungen
- Neue FeedbackResponse mit FeedbackAnswers gespeichert
- Dankesseite wird angezeigt

## Hauptszenario

1. Benutzer oeffnet `/form/{publicToken}` (via Link oder QR-Code)
2. System laedt Formular anhand des Tokens
3. System zeigt Formulartitel, Speaker, Datum und Ort an
4. Fuer jede Frage:
   - RATING: RadioButtonGroup mit Optionen 1-5 (lokalisierte Labels)
   - TEXT: TextArea mit Platzhalter
5. Benutzer fuellt das Formular aus
6. Benutzer klickt "Absenden"
7. System erstellt FeedbackResponse mit FeedbackAnswers
8. System zeigt Dankesseite

## Alternativszenarien

**2a. Formular nicht gefunden**
1. System zeigt "Formular nicht gefunden" Meldung

**2b. Formular nicht oeffentlich**
1. System zeigt "Formular nicht verfuegbar" Meldung

## Geschaeftsregeln
- Bewertungen sind 1-5 (Sehr schlecht bis Sehr gut)
- Textantworten sind optional (nur nicht-leere werden gespeichert)
- Jede Abgabe erzeugt eine neue FeedbackResponse
- Keine Authentifizierung erforderlich

## Beteiligte Klassen
- `views/PublicFormView.java`
- `service/FormService.java`
- `entity/FeedbackResponse.java`
- `entity/FeedbackAnswer.java`
