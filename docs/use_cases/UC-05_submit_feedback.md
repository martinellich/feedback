# Use Case: Feedback abgeben

## Overview

**Use Case ID:** UC-05
**Use Case Name:** Feedback abgeben
**Primary Actor:** Anonymer Benutzer
**Goal:** Feedback ueber ein oeffentliches Formular abgeben
**Status:** Implemented

## Preconditions

- Benutzer hat den Formular-Link oder QR-Code
- Formular existiert und ist im Status PUBLIC

## Main Success Scenario

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

## Alternative Flows

### A1: Formular nicht gefunden

**Trigger:** publicToken existiert nicht in der Datenbank
**Flow:**

1. System zeigt "Formular nicht gefunden" Meldung

### A2: Formular nicht oeffentlich

**Trigger:** Formular ist nicht im Status PUBLIC
**Flow:**

1. System zeigt "Formular nicht verfuegbar" Meldung

## Postconditions

### Success Postconditions

- Neue FeedbackResponse mit FeedbackAnswers gespeichert
- Dankesseite wird angezeigt

### Failure Postconditions

- Keine FeedbackResponse wird erstellt
- Fehlermeldung wird angezeigt

## Business Rules

### BR-007: Bewertungsskala

Bewertungen sind 1-5 (Sehr schlecht bis Sehr gut)

### BR-008: Optionale Textantworten

Textantworten sind optional (nur nicht-leere werden gespeichert)

### BR-009: Neue Response pro Abgabe

Jede Abgabe erzeugt eine neue FeedbackResponse

### BR-010: Keine Authentifizierung

Keine Authentifizierung erforderlich fuer die Feedback-Abgabe
