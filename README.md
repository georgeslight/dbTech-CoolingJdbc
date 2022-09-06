# 2. Übungsblatt: JDBC
Für die folgende Aufgabe müssen die Tabellen desProben-Kühlschranksystemsin IhrerDatenbank vorhanden sein.  
Laden Sie die Dateien **dbtech_lib.zip** und **dbtech_jdbc.zip** herunter und importierenSie die darin befindlichen Java-Projekte über das Eclipse-Hauptmenü:

File > Import ...  
Existing Projects into Workspace  
Zip-Datei unter Punkt **’select archive file:’** auswählen  
Finish aktivieren  
* Das Projektdbtech_libenthält Bibliotheken
* Das Projektdbtech_jdbcenthält vorbereitete Klassen

Während der Entwicklung des Programmcodes können Sie jederzeit Ihren aktuellen Zu-stand mit der KlasseCoolingJdbcTest(im Paketde.htwberlin.test) testen. Dazuaktivieren Sie aus dem Kontextmenü der Testklasse, das Sie über die rechte Maustasteerreichen, den MenüpunktRun as > JUnit Test.

Sie konfigurieren eine Datenbankverbindung über die SchnittstelleDbCred(im Paketde.htwberlin.utils). Dort müssen Sie die Werte der Konstantenurl,user,passwordundschemaanpassen.

Bedenken Sie bitte, dass eine erfolgreiche Ausführung aller Tests nicht automatisch dieKorrektheit Ihrer Lösung sicherstellt. Tests können immer nur die Anwesenheit von Feh-lern zeigen, nicht aber deren Abwesenheit. Das liegt daran, dass Tests im Allgemeinennicht vollständig alle Fehlersituationen abdecken. In der Bewertung Ihrer Lösung ist da-her der erfolgreiche Durchlauf aller Tests eine notwendige Bedingung zum Erreichen dervollen Punktzahl. Es kann aber trotzdem Punktabzug geben, falls Ihre Lösung Fehlerenthält, die durch die Tests nicht aufgedeckt werden.
   
## Aufgabe 1: (10 Punkte)
Ihre Aufgabe besteht darin, die KlasseCoolingJdbczu implementieren, deren Funktio-nalität als Kommentare in der SchnittstelleICoolingJdbcbeschrieben wird. Alle anderenKlassen des Projekts dürfen nicht verändert werden.

## Zusatzaufgabe: (2 Bonuspunkte)
Präsentieren und erklären Sie Ihre erarbeitete Lösung in der nächsten Übung.

## Abgabe
Bitte geben Sie nur die von Ihnen geänderte DateiCoolingJdbc.javaab. Vergessen Sienicht, die Namen Ihrer Gruppenmitglieder als Kommentar in die Datei einzutragen.
