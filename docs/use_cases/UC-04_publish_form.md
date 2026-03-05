# Use Case: Formular veroeffentlichen

## Overview

**Use Case ID:** UC-04
**Use Case Name:** Formular veroeffentlichen
**Primary Actor:** Formular-Besitzer
**Goal:** Ein Entwurfsformular veroeffentlichen, sodass es oeffentlich zugaenglich wird
**Status:** Implemented

## Preconditions

- Benutzer ist authentifiziert
- Formular ist im Status DRAFT
- Benutzer ist Besitzer des Formulars

## Main Success Scenario

1. Benutzer klickt "Veroeffentlichen" im Dashboard
2. System aendert Formularstatus von DRAFT auf PUBLIC
3. Dashboard wird aktualisiert

## Postconditions

### Success Postconditions

- Formularstatus ist PUBLIC
- Formular ist ueber den oeffentlichen Link erreichbar
- Anonyme Benutzer koennen Feedback abgeben

### Failure Postconditions

- Formularstatus bleibt DRAFT
