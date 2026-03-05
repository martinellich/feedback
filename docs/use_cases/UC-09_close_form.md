# Use Case: Formular schliessen

## Overview

**Use Case ID:** UC-09
**Use Case Name:** Formular schliessen
**Primary Actor:** Formular-Besitzer
**Goal:** Ein oeffentliches Formular schliessen, sodass keine weiteren Feedbacks abgegeben werden koennen
**Status:** Implemented

## Preconditions

- Benutzer ist authentifiziert
- Formular ist im Status PUBLIC
- Benutzer ist Besitzer des Formulars

## Main Success Scenario

1. Benutzer klickt "Schliessen" im Dashboard
2. System aendert Formularstatus von PUBLIC auf CLOSED
3. Dashboard wird aktualisiert

## Postconditions

### Success Postconditions

- Formularstatus ist CLOSED
- Keine neuen Feedbacks moeglich
- Bestehende Ergebnisse bleiben erhalten

### Failure Postconditions

- Formularstatus bleibt PUBLIC
