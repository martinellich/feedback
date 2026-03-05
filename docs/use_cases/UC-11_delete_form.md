# UC-11: Formular loeschen

## Beschreibung
Der Formular-Besitzer loescht ein geschlossenes Formular dauerhaft.

## Akteur
Formular-Besitzer

## Vorbedingungen
- Benutzer ist authentifiziert
- Formular ist im Status CLOSED
- Benutzer ist Besitzer des Formulars

## Nachbedingungen
- Formular und alle zugehoerigen Daten geloescht (Fragen, Antworten, Feedbacks, Freigaben)

## Hauptszenario

1. Benutzer klickt "Loeschen" im Dashboard
2. System loescht Formular mit allen abhaengigen Daten (Kaskade)
3. Dashboard wird aktualisiert

## Geschaeftsregeln
- Loeschen ist nur fuer geschlossene Formulare moeglich
- Loeschen ist nicht rueckgaengig zu machen
- Kaskadierendes Loeschen: Fragen, Antworten, Feedbacks, Freigaben

## Beteiligte Klassen
- `views/DashboardView.java`
- `service/FormService.java`
- `entity/FeedbackForm.java`
