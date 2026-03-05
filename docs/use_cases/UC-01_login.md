# Use Case: Anmelden

## Overview

**Use Case ID:** UC-01
**Use Case Name:** Anmelden
**Primary Actor:** Anonymer Benutzer
**Goal:** Per E-Mail-basiertem Login-Code anmelden
**Status:** Implemented

## Preconditions

- Benutzer ist nicht authentifiziert

## Main Success Scenario

1. Benutzer navigiert zu `/login`
2. Benutzer gibt E-Mail-Adresse ein
3. System loescht vorhandene Tokens fuer diese E-Mail
4. System generiert 8-stelligen Login-Code
5. System speichert AccessToken mit 10-Minuten-Ablaufzeit
6. System sendet Code per E-Mail
7. Benutzer gibt erhaltenen Code ein
8. System validiert Code (nicht verwendet, nicht abgelaufen)
9. System erstellt authentifizierte Session
10. Benutzer wird zum Dashboard weitergeleitet

## Alternative Flows

### A1: Ungueltiger Code

**Trigger:** Benutzer gibt einen falschen Code ein
**Flow:**

1. System zeigt Fehlermeldung
2. Benutzer kann Code erneut eingeben

### A2: Abgelaufener Code

**Trigger:** Code ist aelter als 10 Minuten
**Flow:**

1. System zeigt Fehlermeldung
2. Benutzer kann neuen Code anfordern

## Postconditions

### Success Postconditions

- Benutzer ist authentifiziert mit ROLE_USER
- Session ist erstellt

### Failure Postconditions

- Benutzer bleibt unauthentifiziert
- Kein neuer Token wird verbraucht

## Business Rules

### BR-001: Code-Format

Code ist 8-stellig numerisch

### BR-002: Code-Ablaufzeit

Code laeuft nach 10 Minuten ab

### BR-003: Einmalige Verwendung

Code kann nur einmal verwendet werden

### BR-004: Token-Bereinigung

Vorherige Tokens werden beim Anfordern eines neuen Codes geloescht
