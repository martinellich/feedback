# Use Case: Formular bearbeiten

## Overview

**Use Case ID:** UC-03
**Use Case Name:** Formular bearbeiten
**Primary Actor:** Formular-Besitzer
**Goal:** Details und Fragen eines Entwurfsformulars bearbeiten
**Status:** Implemented

## Preconditions

- Benutzer ist authentifiziert
- Formular ist im Status DRAFT
- Benutzer ist Besitzer des Formulars

## Main Success Scenario

1. Benutzer klickt "Bearbeiten" im Dashboard
2. System prueft Zugriffsberechtigung
3. System zeigt FormEditorView mit Formulardetails und Fragenliste
4. Benutzer bearbeitet Titel, Speaker, Datum und/oder Ort
5. Benutzer klickt "Speichern"
6. System speichert Aenderungen und zeigt Erfolgsmeldung

## Alternative Flows

### A1: Kein Zugriff

**Trigger:** Benutzer ist nicht der Besitzer des Formulars
**Flow:**

1. System leitet zum Dashboard weiter

### A2: Frage hinzufuegen

**Trigger:** Benutzer moechte eine neue Frage zum Formular hinzufuegen
**Flow:**

1. Benutzer gibt Fragentext ein
2. Benutzer waehlt Fragetyp (RATING oder TEXT)
3. Benutzer klickt "Hinzufuegen"
4. System ordnet naechsten orderIndex zu
5. Frage erscheint in der Fragenliste

## Postconditions

### Success Postconditions

- Formulardetails und/oder Fragen aktualisiert

### Failure Postconditions

- Formular bleibt unveraendert
- Benutzer wird zum Dashboard weitergeleitet
