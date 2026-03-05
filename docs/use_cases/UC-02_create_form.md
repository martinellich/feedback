# Use Case: Feedback-Formular erstellen

## Overview

**Use Case ID:** UC-02
**Use Case Name:** Feedback-Formular erstellen
**Primary Actor:** Authentifizierter Benutzer
**Goal:** Ein neues Feedback-Formular basierend auf einer Vorlage mit vordefinierten Fragen erstellen
**Status:** Implemented

## Preconditions

- Benutzer ist authentifiziert

## Main Success Scenario

1. Benutzer klickt auf "Neu erstellen" im Dashboard
2. System zeigt Dialog mit Eingabefeldern
3. Benutzer gibt Titel ein (Pflichtfeld)
4. Benutzer gibt optional Speaker, Datum und Ort ein
5. Benutzer bestaetigt
6. System erstellt Formular im Status DRAFT mit eindeutigem publicToken (UUID)
7. System fuegt 13 Vorlagenfragen hinzu (9 Bewertung, 4 Text)
8. Dashboard wird aktualisiert und Erfolgsmeldung angezeigt

## Alternative Flows

### A1: Titel nicht ausgefuellt

**Trigger:** Benutzer laesst das Pflichtfeld Titel leer
**Flow:**

1. Validierung verhindert Erstellung
2. Benutzer muss Titel eingeben

## Postconditions

### Success Postconditions

- Neues Formular im Status DRAFT erstellt
- 13 Vorlagenfragen (9 Bewertung, 4 Text) zugeordnet
- Benutzer ist Besitzer des Formulars

### Failure Postconditions

- Kein Formular wird erstellt
- Dashboard bleibt unveraendert

## Business Rules

### BR-005: Vorlagenfragen

Jedes neue Formular erhaelt 9 Bewertungsfragen (RATING, Skala 1-5) und 4 Textfragen (TEXT, Freitext) als vordefinierte deutschsprachige Fragen zu Praesentationen

### BR-006: Eindeutiger Public Token

Jedes Formular erhaelt einen eindeutigen publicToken (UUID) fuer den oeffentlichen Zugang
