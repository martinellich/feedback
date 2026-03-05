# Use Case: Formular loeschen

## Overview

**Use Case ID:** UC-11
**Use Case Name:** Formular loeschen
**Primary Actor:** Formular-Besitzer
**Goal:** Ein geschlossenes Formular dauerhaft loeschen
**Status:** Implemented

## Preconditions

- Benutzer ist authentifiziert
- Formular ist im Status CLOSED
- Benutzer ist Besitzer des Formulars

## Main Success Scenario

1. Benutzer klickt "Loeschen" im Dashboard
2. System loescht Formular mit allen abhaengigen Daten (Kaskade)
3. Dashboard wird aktualisiert

## Postconditions

### Success Postconditions

- Formular und alle zugehoerigen Daten geloescht (Fragen, Antworten, Feedbacks, Freigaben)

### Failure Postconditions

- Formular bleibt unveraendert

## Business Rules

### BR-021: Nur geschlossene Formulare

Loeschen ist nur fuer geschlossene Formulare moeglich

### BR-022: Nicht rueckgaengig machbar

Loeschen ist nicht rueckgaengig zu machen

### BR-023: Kaskadierendes Loeschen

Kaskadierendes Loeschen aller abhaengigen Daten: Fragen, Antworten, Feedbacks, Freigaben
