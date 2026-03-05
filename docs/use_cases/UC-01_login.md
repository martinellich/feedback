# UC-01: Anmelden

## Beschreibung
Ein Benutzer meldet sich per E-Mail-basiertem Login-Code an.

## Akteur
Anonymer Benutzer

## Vorbedingungen
- Benutzer ist nicht authentifiziert

## Nachbedingungen
- Benutzer ist authentifiziert mit ROLE_USER
- Session ist erstellt

## Hauptszenario

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

## Alternativszenarien

**3a. Ungueltiger Code**
1. System zeigt Fehlermeldung
2. Benutzer kann Code erneut eingeben

**3b. Abgelaufener Code**
1. System zeigt Fehlermeldung
2. Benutzer kann neuen Code anfordern

## Geschaeftsregeln
- Code ist 8-stellig numerisch
- Code laeuft nach 10 Minuten ab
- Code kann nur einmal verwendet werden
- Vorherige Tokens werden beim Anfordern eines neuen Codes geloescht

## Beteiligte Klassen
- `views/LoginView.java`
- `service/TokenService.java`
- `entity/AccessToken.java`
- `security/SecurityConfig.java`
