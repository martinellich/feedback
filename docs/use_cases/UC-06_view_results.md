# Use Case: Ergebnisse anzeigen

## Overview

**Use Case ID:** UC-06
**Use Case Name:** Ergebnisse anzeigen
**Primary Actor:** Formular-Besitzer, Geteilter Benutzer
**Goal:** Feedback-Ergebnisse eines Formulars betrachten
**Status:** Implemented

## Preconditions

- Benutzer ist authentifiziert
- Benutzer hat Zugriff auf das Formular (Besitzer oder geteilt)

## Main Success Scenario

1. Benutzer klickt "Ergebnisse" im Dashboard
2. System prueft Zugriffsberechtigung
3. System zeigt Formulartitel, Speaker und Anzahl Antworten
4. Fuer jede RATING-Frage:
   - Fragentext mit Nummer
   - Durchschnittliche Bewertung (2 Dezimalstellen) oder "N/A"
5. Fuer jede TEXT-Frage:
   - Fragentext mit Nummer
   - Liste aller nicht-leeren Textantworten als Aufzaehlung

## Alternative Flows

### A1: Kein Zugriff

**Trigger:** Benutzer hat keine Berechtigung fuer das Formular
**Flow:**

1. System leitet zum Dashboard weiter

### A2: Keine Antworten vorhanden

**Trigger:** Es wurden noch keine Feedbacks abgegeben
**Flow:**

1. System zeigt "Noch keine Antworten" Meldung

## Postconditions

### Success Postconditions

- Keine Aenderungen am System

### Failure Postconditions

- Keine Aenderungen am System
- Benutzer wird zum Dashboard weitergeleitet

## Business Rules

### BR-011: Statusunabhaengige Ergebnisse

Ergebnisse sind unabhaengig vom Formularstatus einsehbar

### BR-012: Durchschnittsberechnung

Durchschnittsberechnung nur ueber nicht-null Bewertungen

### BR-013: Leere Textantworten

Leere Textantworten werden nicht angezeigt
