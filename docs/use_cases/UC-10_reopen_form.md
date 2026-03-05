# Use Case: Formular wieder oeffnen

## Overview

**Use Case ID:** UC-10
**Use Case Name:** Formular wieder oeffnen
**Primary Actor:** Formular-Besitzer
**Goal:** Ein geschlossenes Formular wieder oeffnen, sodass erneut Feedbacks abgegeben werden koennen
**Status:** Implemented

## Preconditions

- Benutzer ist authentifiziert
- Formular ist im Status CLOSED
- Benutzer ist Besitzer des Formulars

## Main Success Scenario

1. Benutzer klickt "Wieder oeffnen" im Dashboard
2. System aendert Formularstatus von CLOSED auf PUBLIC
3. Dashboard wird aktualisiert

## Postconditions

### Success Postconditions

- Formularstatus ist PUBLIC
- Anonyme Benutzer koennen wieder Feedback abgeben

### Failure Postconditions

- Formularstatus bleibt CLOSED
