# Use Case: Formular teilen

## Overview

**Use Case ID:** UC-07
**Use Case Name:** Formular teilen
**Primary Actor:** Formular-Besitzer
**Goal:** Ein Formular fuer andere Benutzer freigeben
**Status:** Implemented

## Preconditions

- Benutzer ist authentifiziert
- Benutzer ist Besitzer des Formulars

## Main Success Scenario

1. Benutzer klickt "Teilen" im Dashboard
2. System zeigt Dialog mit aktuellen Freigaben
3. Benutzer gibt E-Mail-Adresse ein
4. Benutzer klickt "Hinzufuegen"
5. System validiert E-Mail und erstellt FormShare
6. Freigabeliste wird aktualisiert

## Alternative Flows

### A1: Ungueltige E-Mail

**Trigger:** E-Mail-Format ist ungueltig
**Flow:**

1. System zeigt Fehlermeldung

### A2: E-Mail ist Formular-Besitzer

**Trigger:** Benutzer gibt seine eigene E-Mail-Adresse ein
**Flow:**

1. System zeigt Fehlermeldung (kann nicht mit sich selbst teilen)

### A3: E-Mail bereits geteilt

**Trigger:** Freigabe fuer diese E-Mail existiert bereits
**Flow:**

1. System verhindert Duplikat

### A4: Freigabe entfernen

**Trigger:** Benutzer moechte eine bestehende Freigabe widerrufen
**Flow:**

1. Benutzer klickt "Entfernen" neben einer Freigabe
2. System loescht FormShare
3. Freigabeliste wird aktualisiert

## Postconditions

### Success Postconditions

- Geteilter Benutzer sieht das Formular in seinem Dashboard
- Geteilter Benutzer kann Ergebnisse und QR-Code einsehen

### Failure Postconditions

- Keine Freigabe wird erstellt
- Bestehende Freigaben bleiben unveraendert

## Business Rules

### BR-014: Nur Besitzer kann teilen

Nur der Besitzer kann ein Formular teilen

### BR-015: Keine Weiterfreigabe

Geteilte Benutzer koennen nicht weiter teilen

### BR-016: E-Mail-Validierung

E-Mail-Format wird validiert

### BR-017: Keine doppelten Freigaben

Eindeutige Einschraenkung auf (form_id, shared_with_email)
