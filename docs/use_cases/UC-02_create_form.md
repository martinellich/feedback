# UC-02: Feedback-Formular erstellen

## Beschreibung
Ein Benutzer erstellt ein neues Feedback-Formular basierend auf einer Vorlage mit vordefinierten Fragen.

## Akteur
Authentifizierter Benutzer

## Vorbedingungen
- Benutzer ist authentifiziert

## Nachbedingungen
- Neues Formular im Status DRAFT erstellt
- 13 Vorlagenfragen (9 Bewertung, 4 Text) zugeordnet
- Benutzer ist Besitzer des Formulars

## Hauptszenario

1. Benutzer klickt auf "Neu erstellen" im Dashboard
2. System zeigt Dialog mit Eingabefeldern
3. Benutzer gibt Titel ein (Pflichtfeld)
4. Benutzer gibt optional Speaker, Datum und Ort ein
5. Benutzer bestaetigt
6. System erstellt Formular im Status DRAFT mit eindeutigem publicToken (UUID)
7. System fuegt 13 Vorlagenfragen hinzu
8. Dashboard wird aktualisiert und Erfolgsmeldung angezeigt

## Alternativszenarien

**3a. Titel nicht ausgefuellt**
1. Validierung verhindert Erstellung
2. Benutzer muss Titel eingeben

## Vorlagenfragen
- 9 Bewertungsfragen (RATING, Skala 1-5)
- 4 Textfragen (TEXT, Freitext)
- Vordefinierte deutschsprachige Fragen zu Praesentationen

## Beteiligte Klassen
- `views/DashboardView.java`
- `service/FormService.java`
- `entity/FeedbackForm.java`
- `entity/FeedbackQuestion.java`
