# Use Case: QR-Code generieren

## Overview

**Use Case ID:** UC-08
**Use Case Name:** QR-Code generieren
**Primary Actor:** Formular-Besitzer, Geteilter Benutzer
**Goal:** Einen QR-Code fuer den oeffentlichen Formular-Link generieren
**Status:** Implemented

## Preconditions

- Benutzer ist authentifiziert
- Benutzer hat Zugriff auf das Formular

## Main Success Scenario

1. Benutzer klickt "QR-Code" im Dashboard
2. System generiert QR-Code als PNG-Bild fuer die Formular-URL
3. System zeigt Dialog mit QR-Code-Bild und URL
4. Benutzer kann URL in die Zwischenablage kopieren

## Postconditions

### Success Postconditions

- Keine Aenderungen am System

### Failure Postconditions

- Keine Aenderungen am System

## Business Rules

### BR-018: URL-Format

URL-Format: `{scheme}://{server}:{port}/form/{publicToken}`

### BR-019: QR-Code-Format

QR-Code wird als PNG-Bild generiert (ZXing-Bibliothek)

### BR-020: Statusunabhaengige Verfuegbarkeit

QR-Code ist fuer alle Formularstatus verfuegbar (DRAFT, PUBLIC, CLOSED)
